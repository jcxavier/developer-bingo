package com.jcxavier.devbingo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bump.api.BumpAPIIntents;
import com.bump.api.IBumpAPI;
import com.jcxavier.devbingo.logic.BingoEntryAdapter;
import com.jcxavier.devbingo.logic.DownloadImageTask;

public class BingoActivity extends FragmentActivity {
	private static final String TAG = "BingoActivity";
	private static final String API_KEY_DEV = "a0ebb53706cb41519a2ac6501416947b";
	private static final String API_KEY_PROD = API_KEY_DEV;
	private IBumpAPI api;

	private ServiceConnection connection;
	private BroadcastReceiver receiver;
	private boolean isWaitingForResponse;
	private BingoEntryAdapter adapter;

	private Bundle extras;
	private boolean isServiceBound;
	private TextView status;

	private void setupBump() {
		connection = new ServiceConnection() {
			@Override
			public void onServiceConnected(final ComponentName className, final IBinder binder) {
				Log.i(TAG, "onServiceConnected");
				api = IBumpAPI.Stub.asInterface(binder);
				try {
					api.configure(API_KEY_PROD, android.os.Build.SERIAL);
					status.setText("Connected");
					isServiceBound = true;
				} catch (RemoteException e) {
					isServiceBound = false;
					status.setText("Error");
					Log.w(TAG, e);
				}
				Log.d(TAG, "Service connected");
			}

			@Override
			public void onServiceDisconnected(final ComponentName className) {
				Log.d(TAG, "Service disconnected");
				status.setText("Disconnected");
				isServiceBound = false;
			}
		};

		receiver = new BroadcastReceiver()  {
			@Override
			public void onReceive(final Context context, final Intent intent) {
				final String action = intent.getAction();
				try {
					if (action.equals(BumpAPIIntents.DATA_RECEIVED)) {
						Log.i(TAG, "Received data from: " + api.userIDForChannelID(intent.getLongExtra("channelID", 0)));

						String data = new String(intent.getByteArrayExtra("data"));
						Log.i(TAG, "Data: " + data);

						if (!data.contains(",")) {
							for (int i = 0; i != entries.length; i++) {
								if (entries[i].equals(data)) {
									adapter.markAsCompleted(i);
									break;
								}
							}
						}

						if (isWaitingForResponse) {
							int idx = data.indexOf(";");
							boolean willSend = false;


							String entriesStr;
							if (idx != -1) {
								willSend = true;
								entriesStr = data.substring(0, idx);
							} else {
								entriesStr = data;
							}

							Log.i(TAG, "Data: " + entriesStr);

							String[] otherEntries = entriesStr.split(",");
							Collections.sort(Arrays.asList(otherEntries));

							for (String otherEntry : otherEntries) {
								for (int i = 0; i != entries.length; i++) {
									if (otherEntry.equals(entries[i]) && !adapter.isCompleted(i)) {
										if (willSend) {
											String profileInfo = data.substring(idx + 1);
											String[] profileFields = profileInfo.split(";");

											FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
											Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
											if (prev != null) {
												ft.remove(prev);
											}
											ft.addToBackStack(null);
											PersonFragment dlg = PersonFragment.newInstance(profileFields, otherEntry);
											dlg.show(getSupportFragmentManager(), "lol");
										}

										adapter.markAsCompleted(i);

										if (adapter.isFinished()) {
											AlertDialog.Builder altDialog= new AlertDialog.Builder(getBaseContext());
											altDialog.setMessage("BINGO!"); // here add your message
											altDialog.setNeutralButton("OK", null);
											altDialog.show();
										}

										api.send(intent.getLongExtra("channelID", 0),  otherEntry.getBytes());
										isWaitingForResponse = false;
										return;
									}
								}
							}
						}


					} else if (action.equals(BumpAPIIntents.MATCHED)) {
						long channelID = intent.getLongExtra("proposedChannelID", 0);
						Log.i(TAG, "Matched with: " + api.userIDForChannelID(channelID));
						api.confirm(channelID, true);
						Log.i(TAG, "Confirm sent");
					} else if (action.equals(BumpAPIIntents.CHANNEL_CONFIRMED)) {
						long channelID = intent.getLongExtra("channelID", 0);
						Log.i(TAG, "Channel confirmed with " + api.userIDForChannelID(channelID));
						String entriesStr = "";
						for (String entry : entries) {
							entriesStr += "," + entry;
						}
						entriesStr += ";" + extras.getString("name") + ";" + extras.getString("photo_url") + ";"
								+ extras.getString("brag") + ";" + extras.getString("id") + ";" + extras.getString("profile_url");
						Log.i(TAG, "Sent: " + entriesStr);

						api.send(channelID, entriesStr.getBytes());
						isWaitingForResponse = true;
					} else if (action.equals(BumpAPIIntents.NOT_MATCHED)) {
						Log.i(TAG, "Not matched.");
					} else if (action.equals(BumpAPIIntents.CONNECTED)) {
						Log.i(TAG, "Connected to Bump...");
						api.enableBumping();
						status.setText("Ready");
					}
				} catch (RemoteException e) {
					status.setText("Error");
				}
			}
		};

		getApplicationContext().bindService(new Intent(IBumpAPI.class.getName()), connection, Context.BIND_AUTO_CREATE);
		Log.i(TAG, "boot");
		status.setText("Loading");

		IntentFilter filter = new IntentFilter();
		filter.addAction(BumpAPIIntents.CHANNEL_CONFIRMED);
		filter.addAction(BumpAPIIntents.DATA_RECEIVED);
		filter.addAction(BumpAPIIntents.NOT_MATCHED);
		filter.addAction(BumpAPIIntents.MATCHED);
		filter.addAction(BumpAPIIntents.CONNECTED);
		registerReceiver(receiver, filter);
	}

	private String[] totalEntries;
	private String[] entries;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bingo);
		status = (TextView) findViewById(R.id.status);

		extras = getIntent().getExtras();
		totalEntries = ((ArrayList<String>) extras.get("entries")).toArray(new String[0]);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		setupBump();

		Collections.shuffle(Arrays.asList(totalEntries));
		entries = Arrays.copyOfRange(totalEntries, 0, 6);

		GridView grid = (GridView) findViewById(R.id.grid);
		adapter = new BingoEntryAdapter(this, entries);
		grid.setAdapter(adapter);

	}

	@Override
	public void onStart() {
		Log.i(TAG, "onStart");
		super.onStart();
	}

	@Override
	public void onStop() {
		Log.i(TAG, "onStop");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "onDestroy");
		super.onDestroy();

		try {
			if (connection != null && isServiceBound) {
				unbindService(connection);
			}
		} catch (IllegalArgumentException iae) {
			//
		}

		unregisterReceiver(receiver);
	}

	@SuppressLint("ValidFragment")
	public static class PersonFragment extends DialogFragment {
		private String name;
		private String photoUrl;
		private String brag;
		private String id;
		private String profileUrl;
		private String entry;

		static PersonFragment newInstance(final String[] profileFields, final String entry) {
			PersonFragment f = new PersonFragment();
			Bundle args = new Bundle();
			args.putStringArray("profileFields", profileFields);
			args.putString("entry", entry);
			f.setArguments(args);

			return f;
		}

		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			String[] profileFields = getArguments().getStringArray("profileFields");
			name = profileFields[0];
			photoUrl = profileFields[1];
			brag = profileFields[2];
			id = profileFields[3];
			profileUrl = profileFields[4];

			entry = getArguments().getString("entry");

			setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
		}

		@Override
		public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
				final Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.person, container, false);
			TextView txtName = (TextView) view.findViewById(R.id.name);
			TextView txtStat = (TextView) view.findViewById(R.id.status);
			ImageView btnProfile = (ImageView) view.findViewById(R.id.profile);
			ImageView pic = (ImageView) view.findViewById(R.id.picture);
			new DownloadImageTask(pic).execute(photoUrl.replace("sz=50", "sz=" + (int) (120 * getResources().getDisplayMetrics().density)));

			txtName.setText(name);
			txtStat.setText("Received a new skill: " + entry);

			btnProfile.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View v) {
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(profileUrl)));
				}
			});


			return view;
		}
	}
}

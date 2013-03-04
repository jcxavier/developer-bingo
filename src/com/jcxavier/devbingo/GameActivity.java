package com.jcxavier.devbingo;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jcxavier.devbingo.logic.DownloadImageTask;
import com.jcxavier.devbingo.logic.DummyData;

public class GameActivity extends Activity implements OnClickListener {

	private TextView mSignInStatus;
	private TextView mSignInBrag;
	private ImageView mSignInImage;
	private Button mPlayButton;
	private LinearLayout mContainer;

	private int mCount;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_activity);

		mContainer = (LinearLayout) findViewById(R.id.container);
		mSignInStatus = (TextView) findViewById(R.id.sign_in_status);
		mSignInBrag = (TextView) findViewById(R.id.sign_in_brag);
		mSignInImage = (ImageView) findViewById(R.id.sign_in_image);
		findViewById(R.id.add_skill_button).setOnClickListener(this);
		mPlayButton = (Button) findViewById(R.id.play_button);
		mPlayButton.setOnClickListener(this);

		mCount = 0;

		Bundle extras = getIntent().getExtras();
		mSignInStatus.setText(extras.getString("name"));
		mSignInBrag.setText(Html.fromHtml(extras.getString("brag")));
		new DownloadImageTask(mSignInImage).execute(extras.getString("photo_url").replace("sz=50", "sz=" + (int) (120 * getResources().getDisplayMetrics().density)));
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(final int featureId, final MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.sign_out) {
			Intent data = new Intent();
			if (getParent() == null) {
				setResult(101, data);
			} else {
				getParent().setResult(101, data);
			}
			finish();
			return true;
		} else if (itemId == R.id.add_skill) {
			addSkill();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(final View v) {
		switch (v.getId()) {
		case R.id.play_button:
			ArrayList<String> uniqueEntries = new ArrayList<String>();
			for (int i = 0; i != mContainer.getChildCount(); i++) {
				AutoCompleteTextView actv = (AutoCompleteTextView) mContainer.getChildAt(i).findViewById(R.id.skill);
				if (!DummyData.isValidValue(actv.getText().toString()) || uniqueEntries.contains(actv.getText().toString())) {
					actv.setError("Invalid value");
					actv.requestFocus();
					return;
				} else {
					uniqueEntries.add(actv.getText().toString());
				}
			}

			Intent i = new Intent(this, BingoActivity.class);
			i.putExtras(getIntent());
			i.putExtra("entries", uniqueEntries);
			startActivity(i);
			break;

		case R.id.add_skill_button:
			addSkill();
			break;
		}
	}

	private void addSkill() {
		View skillEntry = LayoutInflater.from(this).inflate(R.layout.skill_entry, null);
		skillEntry.setTag(mCount);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, DummyData.FRAMEWORKS_AND_PROG_LANGS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		AutoCompleteTextView actv = (AutoCompleteTextView) skillEntry.findViewById(R.id.skill);
		actv.setAdapter(adapter);

		View btn = skillEntry.findViewById(R.id.delete);
		btn.setTag(mCount);
		mContainer.addView(skillEntry, 0);
		mCount++;

		if (mCount >= 6) {
			mPlayButton.setEnabled(true);
		}

		actv.requestFocus();

		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				int viewTag = (Integer) v.getTag();
				for (int i = 0; i != mContainer.getChildCount(); i++) {
					if (viewTag == (Integer) (mContainer.getChildAt(i).getTag())) {
						mContainer.removeViewAt(i);
						mCount--;

						if (mCount < 6) {
							mPlayButton.setEnabled(false);
						}

						break;
					}
				}
			}
		});
	}
}

package com.jcxavier.devbingo.logic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jcxavier.devbingo.R;

public class BingoEntryAdapter extends BaseAdapter {

	private static class ViewHolder {
		private TextView entry;
	}

	private final LayoutInflater inflater;
	private final String[] entries;
	private final boolean[] completed;

	private final int selected;
	private final int unselected;

	public BingoEntryAdapter(final Context context, final String[] entries) {
		this.inflater = LayoutInflater.from(context);
		this.entries = entries;
		this.completed = new boolean[entries.length];

		this.selected = context.getResources().getColor(android.R.color.darker_gray);
		this.unselected = context.getResources().getColor(android.R.color.background_light);
	}

	@Override
	public final int getCount() {
		return entries.length;
	}

	@Override
	public final Object getItem(final int position) {
		return entries[position];
	}

	@Override
	public final long getItemId(final int position) {
		return position;
	}

	@Override
	public final View getView(final int position, final View convertView,
			final ViewGroup parent) {
		ViewHolder holder;
		View view = convertView;

		if (view == null) {
			view = inflater.inflate(R.layout.bingo_entry, null);
			holder = new ViewHolder();
			holder.entry = (TextView) view.findViewById(R.id.entry);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		holder.entry.setText(entries[position]);

		if (completed[position]) {
			holder.entry.setBackgroundColor(selected);
		} else {
			holder.entry.setBackgroundColor(unselected);
		}


		return view;
	}

	public void markAsCompleted(final int i) {
		completed[i] = true;
		notifyDataSetChanged();
	}

	public boolean isCompleted(final int i) {
		return completed[i];
	}

	public boolean isFinished() {
		for (int i = 0; i != completed.length; i++) {
			if (!completed[i]) {
				return false;
			}
		}

		return true;
	}
}

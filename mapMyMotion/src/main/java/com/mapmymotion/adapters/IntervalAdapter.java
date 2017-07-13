package com.mapmymotion.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

public class IntervalAdapter extends SimpleAdapter {
	Resources res;
	private int[] colors = new int[] { 0x3066FF66, 0x30FFFF6F };

	public IntervalAdapter(Context context,
			ArrayList<HashMap<String, CharSequence>> jsonIntervallist,
			int resource, String[] from, int[] to) {
		super(context, jsonIntervallist, resource, from, to);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);

		int colorPos = position % colors.length;

		view.setBackgroundColor(colors[colorPos]);

		return view;
	}

}
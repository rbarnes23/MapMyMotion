package com.mapmymotion.adapters;

import com.mapmymotion.Constants;
import com.mapmymotion.R;
import com.mapmymotion.utilities.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

 public class ReplayAdapter extends BaseAdapter{
	private Context mContext;
	private ViewHolder holder;

    
	public ReplayAdapter(Context context) {
		// super(context, msg, resource, from, to);
		super();
		mContext = context;

	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}
	@Override
	public int getCount() {
		return Constants.SPEED_TITLES.length;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@SuppressLint("NewApi") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.activityimages, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.thumbImage.setTag(position);
		BitmapDrawable bmp = Utils.writeOnCircle(mContext.getResources(), mContext.getString(Constants.SPEED_TITLES[position]), 120, 120, 40, Color.WHITE);
		holder.thumbImage.setImageDrawable(bmp);

		return view;
	}
	class ViewHolder {
		public ImageView thumbImage;
		public TextView title;
		public TextView subtitle;

		public ViewHolder(View view) {
			thumbImage = (ImageView) view.findViewById(R.id.thumbImage);
			thumbImage.setVisibility(View.VISIBLE);
			title = (TextView) view.findViewById(R.id.title);
			title.setVisibility(View.GONE);
			title.setTextSize(18);
			subtitle = (TextView) view.findViewById(R.id.subTitle);
			title.setVisibility(View.GONE);
		}

	}

}

package com.mapmymotion.adapters;

import com.mapmymotion.Constants;
import com.mapmymotion.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

 public class ActivitiesAdapter extends BaseAdapter{
	private Context mContext;
	private ViewHolder holder;
    
	public ActivitiesAdapter(Context context) {
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
		return Constants.ACTIVITYTYPES.length;
	}



	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
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
		holder.thumbImage.setImageResource(Constants.ACTIVITYTYPES[position]);
		holder.thumbImage.setMaxHeight(40);
		holder.thumbImage.setMaxWidth(40);

		//holder.title.setText(arr_titles[position]);
		holder.title.setText(Constants.ACTIVITY_TITLES[position]);

		return view;
	}
	class ViewHolder {
		public ImageView thumbImage;
		public TextView title;
		public TextView subtitle;

		public ViewHolder(View view) {
			thumbImage = (ImageView) view.findViewById(R.id.thumbImage);
			title = (TextView) view.findViewById(R.id.title);
			subtitle = (TextView) view.findViewById(R.id.subTitle);
		}

	}

}

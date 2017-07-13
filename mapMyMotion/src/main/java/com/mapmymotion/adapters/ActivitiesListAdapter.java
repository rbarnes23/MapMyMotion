package com.mapmymotion.adapters;

import java.util.List;
import java.util.Locale;

import com.mapmymotion.AppSettings;
import com.mapmymotion.Constants;
import com.mapmymotion.MainActivity;
import com.mapmymotion.R;
import com.mapmymotion.pojo.Events;
import com.mapmymotion.pojo.Motion;
import com.mapmymotion.sql.MotionDataSource;
import com.mapmymotion.utilities.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActivitiesListAdapter extends BaseAdapter {
	private Context mContext;
	private ViewHolder holder;
	private List<Motion> mDataList;
	private InputMethodManager imm;
	
	public ActivitiesListAdapter(Context context, List<Motion> mMotionList) {
		// super(context, msg, resource, from, to);
		super();
		mContext = context;
		mDataList = mMotionList;
		imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

	}

	public void replaceAllData(List<Motion> dataList) {
		mDataList = dataList;
		notifyDataSetChanged();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public Motion getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	public void deleteRow(final int row) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.app_name);
		builder.setIcon(R.drawable.delete);
		builder.setMessage(R.string.deleteActivity)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								MotionDataSource mds = new MotionDataSource(
										mContext);
								mds.deleteActivity(mDataList.get(row)
										.getActivityid());
								mDataList.remove(row);
								notifyDataSetChanged();
								dialog.dismiss();
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;

		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = mInflater.inflate(R.layout.motionrowlist, null);

			holder = new ViewHolder(view);
			// createChart();
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
	holder.row = position;
		// holder._id.setTag(position);
		// holder._id.setText(Long.toString(mDataList.get(position).getId()));
//		holder.activityId.setText(mDataList.get(position).getActivityid());
		holder.epochtime.setText(Utils.getHMS(null, mDataList.get(position)
				.getTimeElapsed()));

		// All below This is just for testing
		double avgSpeed = mDataList.get(position).getTotalDistance()
				/ mDataList.get(position).getTimeElapsed();
		// String avs=String.format(Locale.US,
		// " %4.1f",Utils.convertSpeed(avgSpeed));
		String pace = Utils.convertSpeedToPace(null, avgSpeed);
		holder.pace.setText(pace);
		String totalDistance = String
				.format(Locale.US,
						" %.2f ",
						(mDataList.get(position).getTotalDistance() * Constants.UNIT_MULTIPLIERS[AppSettings
								.getUOM()]));
		holder.distance.setText(totalDistance);
		holder.activityWeight.setText(Double.toString(mDataList.get(position).getWeight()));
		// holder.lat.setText(pace);


		holder.activityDate.setText(Utils.formatDate(mContext
				.getString(R.string.dateLabel), mDataList.get(position)
				.getCurrentTime()));
		return view;
	}

	class ViewHolder {
		public TextView activityDate;
		public TextView activityWeight;
		public TextView pace;
		public TextView distance;
		public TextView epochtime;
		public ImageView replay;
		public ImageView graphs;
		public ImageView syncData;
		public ImageView deleteData;
		public int row;
		public RelativeLayout group;
		public ViewHolder(View view) {
			activityDate = (TextView) view.findViewById(R.id.activityDate);
			activityWeight=(TextView) view.findViewById(R.id.activityWeight);
			distance = (TextView) view.findViewById(R.id.distance);
			epochtime = (TextView) view.findViewById(R.id.epochtime);
			pace = (TextView) view.findViewById(R.id.pace);
			replay = (ImageView) view.findViewById(R.id.replayActivity);
			graphs = (ImageView) view.findViewById(R.id.graphs);
			syncData = (ImageView) view.findViewById(R.id.syncToServer);
			deleteData = (ImageView) view.findViewById(R.id.deleteActivity);

			// Set up all the listeners

			replay.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Message msg = MainActivity.mUiHandler
							.obtainMessage(Constants.REPLAY);
					Bundle bundle = new Bundle();
					String act = mDataList.get(row).getActivityid();
					bundle.putString("activityId", act);
					msg.setData(bundle);
					sendUpdateMessage(msg);
				}
			});

			graphs.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					Message msg = MainActivity.mUiHandler
							.obtainMessage(Constants.CHARTVIEW);
					Bundle bundle = new Bundle();
					String act = mDataList.get(row).getActivityid();
					bundle.putString("activityId", act);
					msg.setData(bundle);
					sendUpdateMessage(msg);
				}
			});

			syncData.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Message msg = MainActivity.mUiHandler
							.obtainMessage(Constants.SYNCDATA);
					Bundle bundle = new Bundle();
					String act = mDataList.get(row).getActivityid();
					bundle.putString("activityId", act);
					msg.setData(bundle);
					sendUpdateMessage(msg);
				}
			});
			
			deleteData.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteRow(row);
				}
			});
		}

	}

	private boolean sendUpdateMessage(Message msgToActivity) {
		return MainActivity.mUiHandler.sendMessage(msgToActivity);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

}

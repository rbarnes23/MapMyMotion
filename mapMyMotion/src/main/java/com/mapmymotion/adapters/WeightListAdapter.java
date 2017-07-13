package com.mapmymotion.adapters;

import java.util.Date;
import java.util.List;


import com.mapmymotion.R.color;
import com.mapmymotion.pojo.Events;
import com.mapmymotion.sql.MotionDataSource;
import com.mapmymotion.AppSettings;
import com.mapmymotion.Constants;
import com.mapmymotion.MainActivity;
import com.mapmymotion.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Message;
import android.text.InputType;
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

public class WeightListAdapter extends BaseAdapter {
	private Context mContext;
	private ViewHolder holder;
	private List<Events> mDataList;
	private InputMethodManager imm;

	public WeightListAdapter(Context context, List<Events> mEventsList) {
		// super(context, msg, resource, from, to);
		super();
		mContext = context;
		mDataList = mEventsList;
		imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);

	}

	public void replaceAllData(List<Events> dataList) {
		mDataList = dataList;
		notifyDataSetChanged();
	}

	public void addRow() {
		Events event = new Events();
		event.setActivityId(AppSettings.getMemberid());
		event.setEventType(Constants.WEIGHTTYPE);
		event.setEventSubType(0);
		event.setEventTime(System.currentTimeMillis());
		mDataList.add(0,event);
		notifyDataSetChanged();

	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public Events getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	public void updateEvent(final int row) {
		MotionDataSource mds = new MotionDataSource(mContext);
		Events event = new Events();
		event.setActivityId(AppSettings.getMemberid());
		event.setEventType(Constants.WEIGHTTYPE);
		event.setEventSubType(0);
		event.setEventTime(mDataList.get(row).getEventTime());
		event.setWeight(mDataList.get(row).getWeight());
		mds.createEvent(event);
		notifyDataSetChanged();
		// Utils.setToast(mContext, "saved", Toast.LENGTH_SHORT);
	}

	public void deleteRow(final int row) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(R.string.app_name);
		builder.setIcon(R.drawable.delete);
		builder.setMessage(R.string.deleteEvent)
				.setPositiveButton(R.string.yes,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								MotionDataSource mds = new MotionDataSource(
										mContext);
								mds.deleteEvent(mDataList.get(row)
										.getActivityId(), Constants.WEIGHTTYPE, mDataList.get(row).getEventTime());
								mDataList.remove(row);
								notifyDataSetChanged();
								if(getCount()==0){addRow();}
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
			view = mInflater.inflate(R.layout.weight, null);
			holder = new ViewHolder(view);
			view.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.row = position;
		// holder._id.setTag(position);
		// holder._id.setText(Long.toString(mDataList.get(position).getId()));
		holder.dateWeight.setText(Constants.TODATEFORMAT.format(mDataList.get(
				position).getEventTime()));

		holder.weightAmount.setText(Double.toString(mDataList.get(position)
				.getWeight()));
		return view;
	}

	class ViewHolder {
		public ViewGroup weightLayout;
		public Button saveWeight;
		public EditText weightAmount;
		public EditText dateWeight;
		public int row;
		public ImageView deleteIndicator;
		public ImageView addIndicator;

		// public TextView altitude;
		// public TextView stopNo;

		public ViewHolder(View view) {
			weightLayout = (ViewGroup) view.findViewById(R.id.weightLayout);
			saveWeight = (Button) view.findViewById(R.id.saveWeight);
			weightAmount = (EditText) view.findViewById(R.id.weightAmount);
			dateWeight = (EditText) view.findViewById(R.id.dateWeight);
			deleteIndicator = (ImageView) view
					.findViewById(R.id.deleteIndicator);
			addIndicator = (ImageView) view.findViewById(R.id.addIndicator);
			// Set up all the listeners
			addIndicator.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					addRow();
				}
			});

			deleteIndicator.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteRow(row);
				}
			});
			

			saveWeight.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
						imm.hideSoftInputFromWindow(
								saveWeight.getWindowToken(), 0);
						v.performClick();

					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));

					}
				}
			});

			saveWeight.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mDataList.get(row).setWeight(
							Double.parseDouble(weightAmount.getText()
									.toString()));
					String weightDate = dateWeight.getText().toString();
					Date dateOfWeight = new Date(weightDate);
					long epochTime = dateOfWeight.getTime();
					// //weightDate=Long.toString(epochTime);

					mDataList.get(row).setEventTime(epochTime);

					updateEvent(row);
				}
			});

			// attach the onFocusChange listener to the EditText
			weightAmount.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
						weightAmount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);

					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
//						imm.hideSoftInputFromWindow(
//								weightAmount.getWindowToken(), 0);

//						mDataList.get(row).setWeight(
//								Double.parseDouble(weightAmount.getText()
//										.toString()));
					}
				}
			});
			// attach the onFocusChange listener to the EditText
			dateWeight.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (hasFocus) {
						v.setBackgroundColor(Color.rgb(255, 248, 220));
						//dateWeight.setInputType(InputType.TYPE_DATETIME_VARIATION_DATE);

					} else {
						// set the row background white
						v.setBackgroundColor(Color.rgb(255, 255, 255));
//						imm.hideSoftInputFromWindow(
//								dateWeight.getWindowToken(), 0);
//						String weightDate = dateWeight.getText().toString();
//						Date dateOfWeight = new Date(weightDate);
//						long epochTime = dateOfWeight.getTime();
//						// //weightDate=Long.toString(epochTime);
//
//						mDataList.get(row).setEventTime(epochTime);

					}
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

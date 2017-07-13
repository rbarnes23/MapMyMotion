package com.mapmymotion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mapmymotion.R;
import com.mapmymotion.adapters.ActivitiesListAdapter;
import com.mapmymotion.adapters.WeightListAdapter;
import com.mapmymotion.pojo.Events;
import com.mapmymotion.pojo.Motion;
import com.mapmymotion.sql.MotionDataSource;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;

public class WeightFragment extends Fragment {
	View root;
	String weight;
	String weightDate;
	long epochTime;
	private MotionDataSource datasource;

	private WeightListAdapter mWeightAdapter;
	// private static ListView mList;// Going to use this list for multiple
	// purposes
	private List<Events> mEventsList = new ArrayList<Events>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// View view = super.onCreateView(inflater, container,
		// savedInstanceState);
		// view.setBackgroundColor(getResources().getColor(R.color.actionbar_orange));
		super.onCreateView(inflater, container, savedInstanceState);

		// Used to create the layout for this fragment
		root = (ViewGroup) inflater.inflate(R.layout.weight, null);
		return root;
	}

	// Load the Events info
	private ArrayList<Events> loadListofEvents() {
		datasource = new MotionDataSource(getActivity());
		ArrayList<Events> eventsList = (ArrayList<Events>) datasource
				.getEventsForActivity(AppSettings.getMemberid(), Constants.WEIGHTTYPE);
		return eventsList;
	}

	public void updateEvent(String time,String weight) {

		MotionDataSource mds = new MotionDataSource(getActivity());
		Events event = new Events();
		event.setActivityId(AppSettings.getMemberid());
		event.setEventType(Constants.WEIGHTTYPE);
		event.setEventSubType(0);
		event.setEventTime(Long.parseLong(time));
		event.setWeight(Double.parseDouble(weight));
		mds.createEvent(event);
		// Utils.setToast(mContext, "saved", Toast.LENGTH_SHORT);
	}

}


//@Override
//public View onCreateView(LayoutInflater inflater, ViewGroup container,
//		Bundle savedInstanceState) {
//	// View view = super.onCreateView(inflater, container,
//	// savedInstanceState);
//	// view.setBackgroundColor(getResources().getColor(R.color.actionbar_orange));
//	super.onCreateView(inflater, container, savedInstanceState);
//
//	// Used to create the layout for this fragment
//	root = (ViewGroup) inflater.inflate(R.layout.weight, null);
//	EditText weightAmount = (com.mapproject.utilities.EditTextWithFocusChanged) root
//			.findViewById(R.id.weightAmount);
//
//	// Will actually store epochtime
//	epochTime = System.currentTimeMillis();
//	EditText dateWeight = (com.mapproject.utilities.DateEditText) root
//			.findViewById(R.id.dateWeight);
//	dateWeight.setText(Constants.TODATEFORMAT.format(epochTime));
//	weightDate=dateWeight.getText().toString();
//	Date ddate;
//	ddate =new Date(weightDate);
//    epochTime = ddate.getTime();
//	//weightDate=Long.toString(epochTime);
//	weight = weightAmount.getText().toString();
//
//	Button saveWeight = (Button) root.findViewById(R.id.saveWeight);
//	saveWeight.setOnFocusChangeListener(new OnFocusChangeListener() {
//
//		@Override
//		public void onFocusChange(View v, boolean hasFocus) {
//			if (hasFocus) {
//				v.setBackgroundColor(Color.rgb(255, 248, 220));
//
//			} else {
//				// set the row background white
//				v.setBackgroundColor(Color.rgb(255, 255, 255));
//
//			}
//		}
//	});
//
//	saveWeight.setOnClickListener(new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			updateEvent(Long.toString(epochTime), weight);
////			Utils.setToast(getActivity(), dateWeight.getText().toString(), Toast.LENGTH_LONG);
//		}
//	});
//
//	return root;
//}

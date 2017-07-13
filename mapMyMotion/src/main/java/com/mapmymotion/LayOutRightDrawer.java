package com.mapmymotion;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.mapmymotion.adapters.ActivitiesListAdapter;
import com.mapmymotion.adapters.WeightListAdapter;
import com.mapmymotion.pojo.Events;
import com.mapmymotion.pojo.Motion;
import com.mapmymotion.sql.MotionDataSource;
import com.mapmymotion.utilities.GraphUtils;
import com.mapmymotion.utilities.Utils;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LayOutRightDrawer extends Fragment {
	ViewGroup root;
	private MotionDataSource datasource;

	private ActivitiesListAdapter mActivitiesAdapter;
	private WeightListAdapter mWeightAdapter;
	// private static ListView mList;// Going to use this list for multiple
	// purposes
	private List<Motion> mMotionList = new ArrayList<Motion>();
	private List<Events> mEventsList = new ArrayList<Events>();

	private GraphicalView mChart;
	private Context mContext;
	private XYSeries mXYSeries;
	private TimeSeries mTimeSeries;
	private RelativeLayout group;

	// private int mHeightOfWeightGroup = 280;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		root = (ViewGroup) inflater.inflate(R.layout.activities, null);
		// RelativeLayout weightGroup = (RelativeLayout)
		// inflater.inflate(R.layout.weight, null);
		// mHeightOfWeightGroup = weightGroup.getHeight();
		mContext = getActivity();
		mContext.setTheme(R.style.PreferenceStyle);
		init();

		return root;
	}

	// Load the activity info
	private ArrayList<Motion> loadListofActivities() {
		datasource = new MotionDataSource(mContext);
		// datasource.deleteTable(); FOR TESTING ONLY
		ArrayList<Motion> motionList = (ArrayList<Motion>) datasource
				.getActivitiesList();
		return motionList;
	}

	public void init() {
		// Load the info
		mMotionList = loadListofActivities();
		// Create adapter
		mActivitiesAdapter = new ActivitiesListAdapter(mContext, mMotionList);
		// Get the listview for the adapter
		ListView listofActivities = (ListView) root
				.findViewById(R.id.listActivites);
		listofActivities.setAdapter(mActivitiesAdapter);
		// Get the listview for the adapter
		mEventsList = loadListofEvents();
		mWeightAdapter = new WeightListAdapter(mContext, mEventsList);
		ListView listofEvents = (ListView) root.findViewById(R.id.listOfEvents);
		listofEvents.setAdapter(mWeightAdapter);

		// Load the data into the adapter
		mActivitiesAdapter.notifyDataSetChanged();
		mWeightAdapter.notifyDataSetChanged();
		
		// Add the chart to the group section of the main.xml
		chartSettings(Constants.LINECHART, "Pace per Split Time", "Splits",
				"Pace");

		// Set the clickhandler to refresh list
		ImageView reloadImage = (ImageView) root.findViewById(R.id.reload);
		reloadImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMotionList = loadListofActivities();
				mActivitiesAdapter.replaceAllData(mMotionList);
			}
		});
		// Set the clickhandler to refresh list
		ImageView weightImage = (ImageView) root.findViewById(R.id.weightImage);
		weightImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ViewGroup weightFragment=(ViewGroup)
				// root.findViewById(R.id.weightEntry);
				ViewGroup weightList = (ViewGroup) root
						.findViewById(R.id.listOfEvents);
				if (weightList.getVisibility() == View.VISIBLE) {
					weightList.setVisibility(View.GONE);
				} else {
					weightList.setVisibility(View.VISIBLE);
					if(mWeightAdapter.getCount()==0){mWeightAdapter.addRow();}
				}
				// android.view.ViewGroup.LayoutParams params =
				// weightList.getLayoutParams();
				// if (params.height== mHeightOfWeightGroup) {
				// params.height = 0;
				// } else {
				// params.height = mHeightOfWeightGroup;
				// }
				// weightFragment.setLayoutParams(params);
			}
		});

	}

	// Load the Events info
	private ArrayList<Events> loadListofEvents() {
		datasource = new MotionDataSource(getActivity());
		ArrayList<Events> eventsList = (ArrayList<Events>) datasource
				.getEventsForActivity(AppSettings.getMemberid(),
						Constants.WEIGHTTYPE);
		return eventsList;
	}

	// Does necessary calculation to update the motion pojo, the database
	// and send data to the UI
	private class SyncDataTask extends AsyncTask<String, Void, String> {
		final ProgressDialog mPd = new ProgressDialog(getActivity());

		@Override
		protected void onPreExecute() {
			mPd.setIcon(R.drawable.appicon);
			mPd.setTitle(R.string.app_name);
			mPd.setIndeterminate(true);
			mPd.setMessage(getString(R.string.working));
			mPd.setCancelable(false);
			mPd.show();

		}

		// -- called if cancelled
		@Override
		protected void onCancelled() {
			super.onCancelled();
			mPd.dismiss();
		}

		@Override
		protected void onPostExecute(String message) {
			super.onPostExecute(message);
			if (message != null) {
				// Message msg = ChatService.mServiceHandler
				// .obtainMessage(Constants.UPDATEDATA);
				// Message msg = MainActivity.mUiHandler
				// .obtainMessage(Constants.UPDATEDATA);
				//
				// Bundle bundle = new Bundle();
				// bundle.putString("message", message);
				// msg.setData(bundle);
				UpdateActivity updateActivity = new UpdateActivity();
				String activityId = updateActivity.UpdateActivity(message);

				// sendUpdateMessage(msg);
			}
			mPd.dismiss();
		}

		@Override
		protected String doInBackground(String... params) {
			String activityId = params[0];
			String message = syncData(activityId);
			return message;
		}

	}

	// Make Sure data is up to date in the list...this automatically happens in
	// notifydatasetchanged
	private String syncData(String activityId) {
		String message;
		datasource = new MotionDataSource(mContext);
		try {
			JSONObject syncList = datasource.syncData(activityId);
			message = syncList.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			message = e.getMessage();
		}
		return message;

	}

	// Create the basic chart without any data
	public void chartSettings(int chartType, String chartLabel, String labelX,
			String labelY) {
		group = (RelativeLayout) root.findViewById(R.id.group);
		// Used to set position of group
		final TextView paceHeading = (TextView) group
				.findViewById(R.id.paceHeading);
		LayoutParams params = new RelativeLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0);
		params.addRule(RelativeLayout.BELOW, paceHeading.getId());
		mChart = createChart(getActivity(), chartLabel, labelX, labelY,
				chartType);
		group.addView(mChart, params);

		mChart.setVisibility(View.GONE);
		group.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mChart.getVisibility() == View.GONE) {
					mChart.setVisibility(View.VISIBLE);
					mChart.setBackgroundColor(Color.TRANSPARENT);
					LayoutParams params = new RelativeLayout.LayoutParams(
							android.view.ViewGroup.LayoutParams.MATCH_PARENT,
							500);
					params.addRule(RelativeLayout.BELOW, paceHeading.getId());
					mChart.setLayoutParams(params);
					mChart.repaint();
				} else {
					mChart.setVisibility(View.GONE);

				}
				// Utils.setToast(mContext, "CL", Toast.LENGTH_LONG);
			}
		});

	}

	private XYMultipleSeriesDataset createDataSet() {
		int count = 5;
		Date[] dt = new Date[5];
		for (int i = 0; i < count; i++) {
			GregorianCalendar gc = new GregorianCalendar(2012, 10, i + 1);
			dt[i] = gc.getTime();
		}

		int[] visits = { 2000, 2500, 2700, 2100, 2800 };
		int[] views = { 2200, 2700, 2900, 2800, 3200 };

		List<TimeSeries> timeSeries = new ArrayList<TimeSeries>();
		timeSeries.add(new TimeSeries("Visits"));
		timeSeries.add(new TimeSeries("Views"));
		mTimeSeries = GraphUtils.createTimeSeries("test");
		timeSeries.add(mTimeSeries);
		// Adding data to Visits and Views Series
		for (int i = 0; i < dt.length; i++) {
			timeSeries.get(0).add(dt[i], visits[i]);
			timeSeries.get(1).add(dt[i], views[i]);
			// timeSeries.get(2).add(mTimeSeries.getX(0),mTimeSeries.getY(0));
		}

		// Creating a dataset to hold each series
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(timeSeries.get(0));
		dataset.addSeries(timeSeries.get(1));
		// dataset.addSeries(timeSeries.get(2));
		return dataset;
	}

	public GraphicalView createChart2(final Context context, String chartTitle,
			String titleX, String titleY, int ChartType) {

		// Creating a dataset to hold each series
		XYMultipleSeriesDataset dataset = createDataSet();

		// Creating XYSeriesRenderer to customize visitsSeries
		XYSeriesRenderer visitsRenderer = new XYSeriesRenderer();
		visitsRenderer.setColor(Color.RED);
		visitsRenderer.setPointStyle(PointStyle.CIRCLE);
		visitsRenderer.setFillPoints(true);
		visitsRenderer.setLineWidth(2);
		visitsRenderer.setDisplayChartValues(true);

		// Creating XYSeriesRenderer to customize viewsSeries
		XYSeriesRenderer viewsRenderer = new XYSeriesRenderer();
		viewsRenderer.setColor(Color.YELLOW);
		viewsRenderer.setPointStyle(PointStyle.CIRCLE);
		viewsRenderer.setFillPoints(true);
		viewsRenderer.setLineWidth(2);
		viewsRenderer.setDisplayChartValues(true);

		// Creating a XYMultipleSeriesRenderer to customize the whole chart
		XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

		multiRenderer.setChartTitle("Visits vs Views Chart");
		multiRenderer.setXTitle("Days");
		multiRenderer.setYTitle("Count");
		multiRenderer.setZoomButtonsVisible(true);

		// Adding visitsRenderer and viewsRenderer to multipleRenderer
		// Note: The order of adding dataseries to dataset and renderers to
		// multipleRenderer
		// should be same
		multiRenderer.addSeriesRenderer(visitsRenderer);
		multiRenderer.addSeriesRenderer(viewsRenderer);

		// Creating a Time Chart
		mChart = (GraphicalView) ChartFactory.getTimeChartView(context,
				dataset, multiRenderer, "dd-MMM-yyyy");

		multiRenderer.setClickEnabled(true);
		multiRenderer.setSelectableBuffer(10);

		// Setting a click event listener for the graph
		mChart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Format formatter = new SimpleDateFormat("dd-MMM-yyyy");

				SeriesSelection seriesSelection = mChart
						.getCurrentSeriesAndPoint();

				if (seriesSelection != null) {
					int seriesIndex = seriesSelection.getSeriesIndex();
					String selectedSeries = "Visits";
					if (seriesIndex == 0)
						selectedSeries = "Visits";
					else
						selectedSeries = "Views";

					// Getting the clicked Date ( x value )
					long clickedDateSeconds = (long) seriesSelection
							.getXValue();
					Date clickedDate = new Date(clickedDateSeconds);
					String strDate = formatter.format(clickedDate);

					// Getting the y value
					int amount = (int) seriesSelection.getValue();

					// Displaying Toast Message
					Utils.setToast(context, selectedSeries + " on " + strDate
							+ " : " + amount, Toast.LENGTH_LONG);
				}
			}
		});
		return mChart;
	}

	public GraphicalView createChart(Context context, String chartTitle,
			String titleX, String titleY, int ChartType) {
		XYMultipleSeriesDataset dataset;
		XYSeriesRenderer xySeriesRenderer;
		XYMultipleSeriesRenderer xyMultipleSeriesRenderer;
		XYSeries xySeries;
		// TimeSeries timeSeries;
		// Create the Empty XYSeries
		xySeries = GraphUtils.createXYSeries(titleY);
		mXYSeries = xySeries;
		// timeSeries = GraphUtils.createTimeSeries(titleY);
		// mTimeSeries = timeSeries;
		// populateChart("0");//("0");//createXYSeries(titleY, motionList);
		dataset = GraphUtils.createXYSeriesDataSet(xySeries);
		xySeriesRenderer = GraphUtils.createXYSeriesRenderer(getResources()
				.getColor(R.color.lightyellow));

		xyMultipleSeriesRenderer = GraphUtils.createRendererForEachSeries(
				xySeriesRenderer, chartTitle, titleX, titleY,
				mXYSeries.getMinY(), mXYSeries.getMaxY());
		if (xyMultipleSeriesRenderer.isInScroll()) {
			xyMultipleSeriesRenderer.setInScroll(true);
		}
		GraphicalView chartView = null;
		if (ChartType == Constants.LINECHART) {
			chartView = ChartFactory.getLineChartView(context, dataset,
					xyMultipleSeriesRenderer);
		} else if (ChartType == Constants.BARCHART) {
			chartView = ChartFactory.getBarChartView(context, dataset,
					xyMultipleSeriesRenderer, Type.STACKED);
		} else if (ChartType == Constants.TIMESERIESCHART) {
			String dateFormat = new SimpleDateFormat("dd-MMM-yyyy").toPattern();
			// dateFormat="dd-MMM-yyyy";
			chartView = ChartFactory.getTimeChartView(context, dataset,
					xyMultipleSeriesRenderer, dateFormat);
		}
		return chartView;
	}

	// Populate the chart with data based on the activity selected
	public void populateIntervalChart(String activityId) {
		datasource = new MotionDataSource(mContext);
		List<Motion> motionList = (List<Motion>) datasource
				.getIntervalsForActivity(activityId);
		// mTimeSeries.clear();
		mXYSeries.clear();
		int size = motionList.size();
		// Make sure no index out of bounds exception occurs
		if (size < 2) {
			mChart.repaint();
			return;
		}
		MotionCalculator motionCalc = new MotionCalculator();

		for (Motion motion : motionList) {
			motion = motionCalc.replayCalculations(motion);
			double count = motion.getCount()
					* AppSettings.getNotificationDistanceInterval();

			Double pace = Utils.convertSpeedToPace(motion.getAverageSpeed());
			// for (int j = 0; j < size; j++) {
			// float epochtime = motion.getCount()
			// * AppSettings.getNotificationDistanceInterval();
			// intervalCount=motion.getCount();
			// if (epochtime<1E12){continue;}
			// Double distance = motion.getCurrentDistance();
			// mTimeSeries.add(epochtime, distance);
			mXYSeries.add(count, pace);
		}
		mChart.repaint();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		//init();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	public void setMessage(Message msg) {
		Bundle bundle;
		// final String activityId = null;
		bundle = new Bundle(msg.getData());
		final String activityId = bundle.getString("activityId");
		bundle = new Bundle(msg.getData());
		if (msg.getData() == null) {
			return;
		}
		AlertDialog dialog;
		switch (msg.what) {

		case Constants.CHARTVIEW:
			// activityId = bundle.getString("activityId");
			populateIntervalChart(activityId);
			if (mChart.getVisibility() != View.VISIBLE) {
				group.performClick();
			}
			break;
		case Constants.SYNCDATA:
			// activityId = bundle.getString("activityId");
			ExecutorService executor = Executors.newSingleThreadExecutor();
			new SyncDataTask().executeOnExecutor(executor, activityId);
			executor.shutdown();
			break;
		// case Constants.DELETEDATA:
		// // activityId = bundle.getString("activityId");
		//
		// AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// builder.setTitle(R.string.app_name);
		// builder.setIcon(R.drawable.delete);
		// builder.setMessage(R.string.deleteActivity)
		// .setPositiveButton(R.string.yes,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int id) {
		// datasource = new MotionDataSource(mContext);
		// // Will have to send message to database
		// // server indicating this
		// // activity is DONE
		// int rowsDeleted = datasource
		// .deleteActivity(activityId);
		// if (rowsDeleted > 0) {
		// for (int i = 0; i < mMotionList.size(); i++) {
		// if (mMotionList.get(i)
		// .getActivityid()
		// .contentEquals(activityId)) {
		// mMotionList.remove(i);
		// }
		// }
		// mActivitiesAdapter.notifyDataSetChanged();
		// }
		//
		// dialog.dismiss();
		// }
		// })
		// .setNegativeButton(R.string.no,
		// new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog,
		// int id) {
		// dialog.dismiss();
		// }
		// });
		// dialog = builder.create();
		// dialog.show();
		//
		// // activityId = bundle.getString("activityId");
		// // datasource = new MotionDataSource(mContext);
		// // Will have to send message to database server indicating this
		// // activity is DONE
		// // int rowsDeleted = datasource.deleteActivity(activityId);
		// // if (rowsDeleted > 0) {
		// // for (int i = 0; i < mMotionList.size(); i++) {
		// // if (mMotionList.get(i).getActivityid()
		// // .contentEquals(activityId)) {
		// // mMotionList.remove(i);
		// // }
		// // }
		// // mActivitiesAdapter.notifyDataSetChanged();
		// // }
		// break;
		case Constants.ACTIVITIES:

			Motion motion = new Motion();
			JSONObject jMotion;
			try {
				// Extract the passed object into a JSONObject
				jMotion = new JSONObject(bundle.getString("motion"));
				// Turn the json back into the pojo
				motion.populateMotionFromJSON(jMotion);
				mMotionList.add(motion);
				mActivitiesAdapter.notifyDataSetChanged();

			} catch (JSONException e) {
				// Not doing anything with this
				e.printStackTrace();
			}
			break;
		}
	}

	public XYSeries initXYSeries(String title) {
		if (mXYSeries == null) {
			mXYSeries = new XYSeries(title);
		}
		return mXYSeries;
	}

	private boolean sendUpdateMessage(Message msgToActivity) {
		return MainActivity.mUiHandler.sendMessage(msgToActivity);
		// return ChatService.mServiceHandler.sendMessage(msgToActivity);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// MainActivity.setToast("LAND", 1);
		} else {
			// MainActivity.setToast("PORT", 1);
		}
	}
}
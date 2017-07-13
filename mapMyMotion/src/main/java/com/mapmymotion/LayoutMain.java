        package com.mapmymotion;

        import android.graphics.Color;
        import android.location.Location;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.view.LayoutInflater;
        import android.view.MotionEvent;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.view.View.OnTouchListener;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.RelativeLayout;
        import android.widget.RelativeLayout.LayoutParams;
        import android.widget.TextView;
        import android.widget.Toast;
        import android.widget.ToggleButton;

        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
        import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.CameraPosition;
        import com.google.android.gms.maps.model.Circle;
        import com.google.android.gms.maps.model.CircleOptions;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.maps.model.Polyline;
        import com.google.android.gms.maps.model.PolylineOptions;
        import com.mapmymotion.adapters.InfoAdapter;
        import com.mapmymotion.pojo.Motion;
        import com.mapmymotion.services.LocationIntentService;
        import com.mapmymotion.sql.MotionDataSource;
        import com.mapmymotion.utilities.Utils;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.Locale;
        import java.util.Map;

public class LayoutMain extends LayoutMainMaster {
    private GoogleMap mMap;
    private int mZoomLevel = 14;
    private Circle mCircle;
    // private ArrayList<LatLng> mLatLngList = new ArrayList<LatLng>();
    private List<List<LatLng>> mLatLngLists = new ArrayList<List<LatLng>>();
    private ViewGroup root; // Leave this here for now...doesnt probably need to
    // be here
    // private int mMeasurementIndex; //Removed this as i want to display the
    // most current uom if settings change
    // Keep track of our markers
    private List<MarkerOptions> mMarkerOptions = new ArrayList<MarkerOptions>();
    // Animated Count to display on marker icon
    private int mMarkerCount = 1;
    // These textviews are used to update telematics
    TextView currentSpeedView;
    TextView avgSpeedView;
    TextView distanceView;
    TextView paceView;
    TextView altitudeView;
    TextView timeElapseView;
    ImageView imageToMove;

    private PolylineOptions mPolylineOptions = new PolylineOptions().width(24)
            .color(Color.CYAN).geodesic(true);
    private Polyline mPolyline;
    // TODO need to change circleoptions to use lastknown address as this is
    // hard coded to my location
    private CircleOptions mCircleOptions = new CircleOptions().radius(500)
            .center(new LatLng(27.91147942, -82.45750849))
            .strokeColor(Color.BLUE).fillColor(Color.TRANSPARENT).visible(true)
            .strokeWidth((float) 2.0);
    private TextToSound mTts;
    private int counter;
    private LayoutInflater mInflater;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTts = new TextToSound(getActivity());
    }

    private void createMoveableImage(ViewGroup root){
        imageToMove=(ImageView) root.findViewById(R.id.imageToMove);
//	imageToMove.setOnLongClickListener(new OnLongClickListener() {
//
//		@Override
//		public boolean onLongClick(View v) {
//			//Utils.setToast(getActivity(), "test3", Toast.LENGTH_LONG);
//			v.setTag("TEST");
//            ClipData.Item item = new ClipData.Item((CharSequence)v.getTag());
//
//            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
//            ClipData dragData = new ClipData(v.getTag().toString(),
//            mimeTypes, item);
//
//            // Instantiates the drag shadow builder.
//            View.DragShadowBuilder myShadow = new DragShadowBuilder(v);
//            // Starts the drag
//            v.startDrag(dragData,  // the data to be dragged
//            myShadow,  // the drag shadow builder
//            null,      // no need to use local data
//            0          // flags (not currently used, set to 0)
//            );
//
//			return true;
//		}
//	});
//    // Create and set the drag event listener for the View
//    imageToMove.setOnDragListener( new OnDragListener(){
//       @Override
//       public boolean onDrag(View v,  DragEvent event){
//    	   LayoutParams layoutParams = (RelativeLayout.LayoutParams) imageToMove.getLayoutParams();
//		String msg = null;
//		switch(event.getAction())
//           {
//              case DragEvent.ACTION_DRAG_STARTED:
//                 //layoutParams = (RelativeLayout.LayoutParams)
//                 v.getLayoutParams();
//                 Log.d(msg, "Action is DragEvent.ACTION_DRAG_STARTED");
//                 // Do nothing
//                 break;
//              case DragEvent.ACTION_DRAG_ENTERED:
//                 Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENTERED");
//                 int x_cord = (int) event.getX();
//                 int y_cord = (int) event.getY();
//                 break;
//              case DragEvent.ACTION_DRAG_EXITED :
//                 Log.d(msg, "Action is DragEvent.ACTION_DRAG_EXITED");
//                 x_cord = (int) event.getX();
//                 y_cord = (int) event.getY();
//                 layoutParams.leftMargin = x_cord;
//                 layoutParams.topMargin = y_cord;
//                 v.setLayoutParams(layoutParams);
//
////	                layoutParams.leftMargin = x_cord - 25;
////	                layoutParams.topMargin = y_cord - 75;
////	                imageToMove.setLayoutParams(layoutParams);
//
//                 break;
//              case DragEvent.ACTION_DRAG_LOCATION  :
//                 Log.d(msg, "Action is DragEvent.ACTION_DRAG_LOCATION");
//                 x_cord = (int) event.getX();
//                 y_cord = (int) event.getY();
////	                layoutParams.leftMargin = x_cord - 25;
////	                layoutParams.topMargin = y_cord - 75;
////	                imageToMove.setLayoutParams(layoutParams);
//                 break;
//              case DragEvent.ACTION_DRAG_ENDED   :
//                 Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENDED");
//                 // Do nothing
//                 break;
//              case DragEvent.ACTION_DROP:
//                 Log.d(msg, "ACTION_DROP event");
//                 Utils.setToast(getActivity(), v.getTag().toString(),Toast.LENGTH_LONG);
//                 // Do nothing
//                 break;
//              default: break;
//              }
//              return true;
//           }
//
//
//    });

        imageToMove.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) imageToMove.getLayoutParams();
                switch(event.getActionMasked())
                {
                    case MotionEvent.ACTION_DOWN:

                        break;
                    case MotionEvent.ACTION_MOVE:
                        int x_cord = (int) event.getRawX();
                        int y_cord = (int) event.getRawY();
//		                if (x_cord > windowwidth) {
//		                    x_cord = windowwidth;
//		                }
//		                if (y_cord > windowheight) {
//		                    y_cord = windowheight;
//		                }
                        layoutParams1.leftMargin = x_cord - 25;
                        layoutParams1.topMargin = y_cord - 175;
                        imageToMove.setLayoutParams(layoutParams1);
                        break;
                    default:
                        break;
                }
                return true;
            }
        })	;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Used to create the layout for this fragment
        root = (ViewGroup) inflater.inflate(R.layout.mapviewsupport, null);
        mInflater = inflater;
        SupportMapFragment mapfragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapit);

        mapfragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                init(root);
                mMap.setInfoWindowAdapter(new InfoAdapter(mInflater));
                // Set the List of latlngs
                ArrayList<LatLng> latLngLst = new ArrayList<LatLng>();
                mLatLngLists.add(latLngLst);
                ArrayList<LatLng> latLngReplay = new ArrayList<LatLng>();
                mLatLngLists.add(latLngReplay);
                final ToggleButton onoff = (ToggleButton) root.findViewById(R.id.onoff);
                onoff.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Message msg = LocationIntentService.mServiceHandler
                                .obtainMessage();
                        String sayMessage = "";
                        if (((ToggleButton) v).isChecked()) {
                            msg.what = Constants.START;
                            sayMessage = getString(R.string.start);
                            ToggleButton resumeLastActivity = (ToggleButton) root
                                    .findViewById(R.id.resumeLastActivity);
                            resumeLastActivity.setVisibility(View.GONE);
                            // ToggleButton pauseResume = (ToggleButton) root
                            // .findViewById(R.id.pauseResume);
                            // pauseResume.setVisibility(View.VISIBLE);

                        } else {
                            msg.what = Constants.STOP;
                            // Create END Marker
                            if (mLatLngLists.get(Constants.ACTIVITYLINE).size() > 0) {
                                LatLng latLng = mLatLngLists
                                        .get(Constants.ACTIVITYLINE).get(
                                                mLatLngLists
                                                        .get(Constants.ACTIVITYLINE)
                                                        .size() - 1);
                                MarkerOptions newMarkerOption = new MarkerOptions()
                                        .position(latLng)
                                        .snippet(getString(R.string.stop))
                                        .draggable(false)
                                        .title(getString(R.string.stop))
                                        .icon(BitmapDescriptorFactory
                                                .fromResource(R.drawable.marker_destination));
                                mMap.addMarker(newMarkerOption);
                            }
                            sayMessage = getString(R.string.stop);
                        }
                        LocationIntentService.mServiceHandler.sendMessage(msg);
                        mTts.sayMessage(sayMessage);

                    }
                });

                final ToggleButton resumeLastActivity = (ToggleButton) root
                        .findViewById(R.id.resumeLastActivity);
                resumeLastActivity.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Message msg = LocationIntentService.mServiceHandler
                                .obtainMessage();
                        String sayMessage = "";
                        if (((ToggleButton) v).isChecked()) {
                            msg.what = Constants.NEWACTIVITY;
                            sayMessage = getString(R.string.newactivity);
                        } else {
                            msg.what = Constants.LASTACTIVITY;
                            sayMessage = getString(R.string.resume_last);
                        }
                        LocationIntentService.mServiceHandler.sendMessage(msg);
                        Utils.setToast(getActivity(), sayMessage, Toast.LENGTH_LONG);
                        mTts.sayMessage(sayMessage);

                    }
                });
                createMoveableImage(root);

            }
        });


//        mMap.setInfoWindowAdapter(new InfoAdapter(mInflater));
        // mMap.setOnInfoWindowClickListener((OnInfoWindowClickListener) this);
/*
        // Set the List of latlngs
        ArrayList<LatLng> latLngLst = new ArrayList<LatLng>();
        mLatLngLists.add(latLngLst);
        ArrayList<LatLng> latLngReplay = new ArrayList<LatLng>();
        mLatLngLists.add(latLngReplay);

        final ToggleButton onoff = (ToggleButton) root.findViewById(R.id.onoff);
        onoff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Message msg = LocationIntentService.mServiceHandler
                        .obtainMessage();
                String sayMessage = "";
                if (((ToggleButton) v).isChecked()) {
                    msg.what = Constants.START;
                    sayMessage = getString(R.string.start);
                    ToggleButton resumeLastActivity = (ToggleButton) root
                            .findViewById(R.id.resumeLastActivity);
                    resumeLastActivity.setVisibility(View.GONE);
                    // ToggleButton pauseResume = (ToggleButton) root
                    // .findViewById(R.id.pauseResume);
                    // pauseResume.setVisibility(View.VISIBLE);

                } else {
                    msg.what = Constants.STOP;
                    // Create END Marker
                    if (mLatLngLists.get(Constants.ACTIVITYLINE).size() > 0) {
                        LatLng latLng = mLatLngLists
                                .get(Constants.ACTIVITYLINE).get(
                                        mLatLngLists
                                                .get(Constants.ACTIVITYLINE)
                                                .size() - 1);
                        MarkerOptions newMarkerOption = new MarkerOptions()
                                .position(latLng)
                                .snippet(getString(R.string.stop))
                                .draggable(false)
                                .title(getString(R.string.stop))
                                .icon(BitmapDescriptorFactory
                                        .fromResource(R.drawable.marker_destination));
                        mMap.addMarker(newMarkerOption);
                    }
                    sayMessage = getString(R.string.stop);
                }
                LocationIntentService.mServiceHandler.sendMessage(msg);
                mTts.sayMessage(sayMessage);

            }
        });

        final ToggleButton resumeLastActivity = (ToggleButton) root
                .findViewById(R.id.resumeLastActivity);
        resumeLastActivity.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Message msg = LocationIntentService.mServiceHandler
                        .obtainMessage();
                String sayMessage = "";
                if (((ToggleButton) v).isChecked()) {
                    msg.what = Constants.NEWACTIVITY;
                    sayMessage = getString(R.string.newactivity);
                } else {
                    msg.what = Constants.LASTACTIVITY;
                    sayMessage = getString(R.string.resume_last);
                }
                LocationIntentService.mServiceHandler.sendMessage(msg);
                Utils.setToast(getActivity(), sayMessage, Toast.LENGTH_LONG);
                mTts.sayMessage(sayMessage);

            }
        });
  */
//        createMoveableImage(root);
        return root;
    }


    // The IDataToSendListener sent the data here
    public void setMessage(Message msg) {
        ToggleButton onoff = (ToggleButton) root.findViewById(R.id.onoff);
        Bundle bundle;
        switch (msg.what) {
            case Constants.AUTOSTART:
                onoff.performClick();
                break;

            case Constants.AUTOSTOP:
                onoff.performClick();
                break;

            case Constants.REPLAY: // This should have its own list of markers as we
                // dont want to interfere with an active
                // activity
                bundle = new Bundle(msg.getData());
                if (msg.getData() == null) {
                    return;
                }
                String activityId = bundle.getString("activityId");
                // Get all the point stored in activity table for an activity
                MotionDataSource motionDataSource = new MotionDataSource(
                        getActivity());

                // final List<Events> listofEvents = motionDataSource
                // .getEventsForActivity(activityId, Constants.MAINACTIVITYTYPE);

                final List<Motion> listofDataPoints = motionDataSource
                        .getMotionListForActivity(activityId);
                int sizeOfList = listofDataPoints.size();
                // reset this to 0 each time it is run otherwise will
                counter = 0;
                mMarkerCount = 1;
                // Clear the list....we want to make sure that a new activity is not
                // going on for now...probably create a new latlnglist
                mLatLngLists.get(Constants.REPLAYLINE).clear();
                // Remove all the markers from the options list
                mMarkerOptions.clear();

                final Handler handler = new Handler();
                final Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
					/* do what you need to do */
                        if (counter == listofDataPoints.size() - 1) {
                            Motion motion = listofDataPoints.get(counter);
                            Map<String, String> info = Utils
                                    .createFormattedMessage(getActivity(), motion);
                            String toDisplay = info.get("currentSpeed")
                                    + info.get("averageSpeed") + info.get("pace")
                                    + info.get("distance") + info.get("duration")
                                    + info.get("currentTime");

                            MarkerOptions newMarkerOption = new MarkerOptions()
                                    .position(motion.getLatLng())
                                    .snippet(toDisplay)
                                    .draggable(false)
                                    .title(getString(R.string.stop))
                                    .icon(BitmapDescriptorFactory
                                            .fromResource(R.drawable.marker_destination));
                            Marker marker = mMap.addMarker(newMarkerOption);
                            // marker.showInfoWindow();
                            // Capture screen for sharing
                            Utils.captureMapScreen("mapmymotion", mMap, toDisplay,
                                    getActivity());

                            // marker.showInfoWindow();
                            // CalendarUtils.addEventToCalendar(getActivity(),
                            // "Statistics", toDisplay, "Here", 0,
                            // motion.getCurrentTime(), false, true);

                        }
                        if (counter < listofDataPoints.size()) {
                            Motion motion = listofDataPoints.get(counter);
                            updateTelematics(motion);
                            upDateMap(motion, Constants.REPLAYLINE);
                            // mLatLngLists.get(1).add(motion.getLatLng());
                            counter++;
                            //Want to be able to change the replay speed on the fly so need to use settings for each loop
                            int replayValue=Constants.SPEED.values()[AppSettings.getReplaySpeed()].getReplayValue();
                            if (AppSettings.getReplaySpeed() == 0) {
                                handler.removeCallbacks(this);
                            } else {
                                handler.postDelayed(this,
                                        Constants.MINIMUMTIMEBETWEENUPDATES
                                                / replayValue);
                            }
                        } else {
                            handler.removeCallbacks(this);
                        }
                    }
                };
                if (counter == 0) {
                    mMap.clear();
                    handler.postDelayed(runnable, 100);
                }

                else {
                    handler.removeCallbacks(runnable);
                    Utils.setToast(getActivity(), activityId + " " + sizeOfList,
                            Toast.LENGTH_SHORT);
                }

                break;
            case Constants.ACTIVITIES:
                bundle = new Bundle(msg.getData());
                if (msg.getData() == null) {
                    return;
                }

                Motion motion = new Motion();
                JSONObject jMotion;
                try {
                    // Extract the passed object into a JSONObject
                    jMotion = new JSONObject(bundle.getString("motion"));
                    // Turn the json back into the pojo
                    motion.populateMotionFromJSON(jMotion);
                    // Update the Screen
                    updateTelematics(motion);
                    // Update the Map
                    upDateMap(motion, Constants.ACTIVITYLINE);
                    // mLatLngLists.get(0).add(new LatLng(motion.getLatitude(),
                    // motion
                    // .getLongitude()));

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
        }
    }

    // Initial Settings
    private void init(ViewGroup root) {
        // mMeasurementIndex = AppSettings.getUOM();
        // Set up text views in advance
        currentSpeedView = (TextView) root.findViewById(R.id.currentSpeed);
        avgSpeedView = (TextView) root.findViewById(R.id.averageSpeed);
        distanceView = (TextView) root.findViewById(R.id.distance);
        paceView = (TextView) root.findViewById(R.id.pace);
        altitudeView = (TextView) root.findViewById(R.id.altitude);
        timeElapseView = (TextView) root.findViewById(R.id.timeElapsed);
        // Need to change the setmaptype so it is populated from preferences so
        // i can use different map types
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Probably dont need these features but for now i am leaving them
        mMap.setBuildingsEnabled(true);
        mMap.setIndoorEnabled(true);

        // Camera change listener is currently used to set zoom levels
        mMap.setOnCameraChangeListener(getCameraChangeListener());


        // For now this is a test to see how far my click is from center
        // of the
        // circle
        mMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng position) {
                if (mCircle == null) {
                    return;
                }
                LatLng center = mCircle.getCenter();
                float[] distance = new float[2];
                boolean isInCircle;
                Location.distanceBetween(position.latitude, position.longitude,
                        center.latitude, center.longitude, distance);
                // Check to see if is the circle
                isInCircle = (distance[0] < mCircle.getRadius()) ? true : false;
                if (isInCircle) {
                    Utils.setToast(getActivity(),
                            Utils.getMFI("Inside", distance[0]),
                            Toast.LENGTH_SHORT);
                } else {
                    Utils.setToast(getActivity(),
                            Utils.getMFI("Outside", distance[0]),
                            Toast.LENGTH_SHORT);
                }
            }
        });
        // Zoom in, animating the camera.
        // mMap.animateCamera(CameraUpdateFactory.zoomTo(mZoomLevel), 2000,
        // null);
        updateCamera(new LatLng(27.91147942, -82.45750849));

    }

    // Updates the Screen with motion information
    private void updateTelematics(Motion motion) {
        currentSpeedView.setText(String.format(Locale.US, " %4.1f",
                Utils.convertSpeed(motion.getCurrentSpeed())));
        avgSpeedView.setText(String.format(Locale.US, " %4.1f",
                Utils.convertSpeed(motion.getAverageSpeed())));

        paceView.setText(Utils.convertSpeedToPace(null,
                motion.getAverageSpeed()));
        // paceView.setText(AppSettings.getMS(Utils.convertSpeed(motion.getPace())));
        timeElapseView.setText(AppSettings.getHMS(motion.getTimeElapsed()));
        distanceView
                .setText(String.format(
                        Locale.US,
                        " %.2f ",
                        (motion.getTotalDistance() * Constants.UNIT_MULTIPLIERS[AppSettings
                                .getUOM()])));
        // distanceView.setText(Utils.getMF(motion.getTotalDistance()));

        altitudeView.setText(String.format(Locale.US, " %.2f ",
                (Utils.convertMetricToFeet(motion.getCurrentAltitude()))));
        if ((Utils
                .roundDecimal(Utils.convertSpeed(motion.getCurrentSpeed()), 2) <= AppSettings
                .getAutoStopInterval())
                && AppSettings.getAutoStopInterval() > Constants.STOP) {
            ToggleButton onoff = (ToggleButton) root.findViewById(R.id.onoff);
            onoff.performClick();
        }

    }

    // Create marker optons and adding polyline points and Text to Speech
    private void createMarkerInfo(Motion motion, int listNo) {
        Map<String, String> info = Utils.createFormattedMessage(getActivity(),
                motion);
        // String to display in the marker
        String toDisplay = info.get("currentSpeed") + info.get("averageSpeed")
                + info.get("pace") + info.get("distance")
                + info.get("duration");
        addMarkerOptionToList(
                getString(R.string.information) + " "
                        + Integer.toString(mMarkerCount),
                toDisplay,
                mLatLngLists.get(listNo).get(
                        mLatLngLists.get(listNo).size() - 1),
                Integer.toString(mMarkerCount), motion.getActivityType());

        mTts.createVoiceMessage(motion, info);
    }

    // Adds each marker to the list of marker options to be used to create
    // markers
    private void addMarkerOptionToList(String title, String snippet,
                                       LatLng position, String toDisplayOnBitmap, int activityType) {
        // Make a new marker
        MarkerOptions newMarkerOption = new MarkerOptions()
                .position(position)
                .snippet(snippet)
                .draggable(false)
                .title(title)
                // .icon(BitmapDescriptorFactory.fromBitmap(Utils.writeOnCircle(
                // getResources(), toDisplayOnBitmap, 40, 40, 18,
                // Color.WHITE).getBitmap()));
                .icon(BitmapDescriptorFactory.fromBitmap(Utils.writeOnDrawable(
                        getResources(), Constants.ACTIVITYTYPES[activityType],
                        toDisplayOnBitmap).getBitmap()));
        // Add the marker to the list
        mMarkerOptions.add(newMarkerOption);
        mMarkerCount++;
    }

    // Update Map related information
    private void upDateMap(Motion motion, int listNo) {
        // Make sure i dont add a null item to the list
        if (motion.getLatitude() < 1) {
            return;
        }
        mLatLngLists.get(listNo).add(motion.getLatLng());
        // New to move camera initially
        if (mLatLngLists.get(listNo).size() == 1) {
            // Set the center of the circle
            mCircleOptions.center(motion.getLatLng());
            // Add the circle with the new latlng
            mCircle = mMap.addCircle(mCircleOptions);
            mCircle.setVisible(false);
            // If an activity set color to red ;if replay set it to cyan
            mPolylineOptions
                    .color((listNo == Constants.ACTIVITYLINE) ? Color.RED
                            : Color.CYAN);
            // On activity make line width slightly smaller than replay
            mPolylineOptions
                    .width((listNo == Constants.ACTIVITYLINE) ? 18 : 24);
            mPolyline = mMap.addPolyline(mPolylineOptions);
            MarkerOptions newMarkerOption = new MarkerOptions()
                    .position(motion.getLatLng())
                    .snippet(getString(R.string.start))
                    .draggable(false)
                    .title(getString(R.string.start))
                    .icon(BitmapDescriptorFactory
                            .fromResource(R.drawable.marker_departure));
            mMap.addMarker(newMarkerOption);

        } else if (mLatLngLists.get(listNo).size() > 1) {
            // Always keep the polyline up to date
            mPolyline.setPoints(mLatLngLists.get(listNo));

            if (motion.isAnimateToPoint()) {
                // isLocationInCircle(motion);
                mCircle.setVisible(true);
                // mPolyline.setPoints(mLatLngList);
                // Create a marker
                createMarkerInfo(motion, listNo);

                // // Formats the message for speech and says it
                // Map<String,String> voice = Utils.createFormattedMessage(
                // getActivity(), motion);
                //
                // mTts.createVoiceMessage(motion,voice);

                // Display all the markers
                showMarkers();
                // updateCamera();
            }
        }
        updateCamera(mLatLngLists.get(listNo).get(
                mLatLngLists.get(listNo).size() - 1));
    }

    // Moves and zooms the camera to a particular location
    private void updateCamera(LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mZoomLevel));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(mZoomLevel), 2000, null);
    }

    // Adds markers to the map from the markeroptions that are stored in an
    // array
    private void showMarkers() {
        for (int i = 0; i < mMarkerOptions.size(); i++) {
            mMap.addMarker(mMarkerOptions.get(i));
        }
    }

    // This is used to detect if a location is in the circle...may be used in
    // future to detect to see if object is within an area and also for rating
    // in Dispatch app
    private boolean isLocationInCircle(Motion motion) {
        float[] distance = new float[2];
        LatLng latLng = motion.getLatLng();
        boolean isInCircle;
        Location.distanceBetween(latLng.latitude, latLng.longitude,
                mCircle.getCenter().latitude, mCircle.getCenter().longitude,
                distance);
        // Check to see if is the circle
        isInCircle = (distance[0] < mCircle.getRadius()) ? true : false;
        if (isInCircle) {
            Utils.setToast(getActivity(), Utils.getMFI("Inside", distance[0]),
                    Toast.LENGTH_SHORT);
        } else {
            Utils.setToast(getActivity(), Utils.getMFI("Outside", distance[0]),
                    Toast.LENGTH_SHORT);
        }
        return isInCircle;

    }

    // Used to detect changes in the camera ....used for zoom level currently
    public OnCameraChangeListener getCameraChangeListener() {
        return new OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {

                // if (mZoomLevel != (int) Math.round(position.zoom)) {
                // isZooming = true;
                // }

                mZoomLevel = Math.round(position.zoom);

            }

        };
    }

}

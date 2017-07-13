package com.mapmymotion;

//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Locale;
//import android.graphics.drawable.Drawable;
//import android.speech.tts.TextToSpeech.OnInitListener;
//import android.widget.ArrayAdapter;

//import org.json.JSONException;
//import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.mapmymotion.adapters.ActivitiesAdapter;
import com.mapmymotion.adapters.ReplayAdapter;
import com.mapmymotion.interfaces.IDataToSendListener;
import com.mapmymotion.interfaces.IMessageReceivedListener;
import com.mapmymotion.pojo.ActivityAssignment;
import com.mapmymotion.pojo.Events;
import com.mapmymotion.pojo.Motion;
import com.mapmymotion.services.ChatIntentService;
import com.mapmymotion.services.LocationIntentService;
import com.mapmymotion.sql.MotionDataSource;
import com.mapmymotion.utilities.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public abstract class CustomFragmentActivity extends ActionBarActivity
        implements
        IDataToSendListener, IMessageReceivedListener {
    protected static DrawerLayout mDrawerLayout;
    protected ActionBarDrawerToggle mDrawerToggle;
    protected static Menu mMenu;
    protected static Context sContext;
    protected static TextToSpeech mTts;
    protected IMessageReceivedListener mListener;
    protected Intent mChatIntentService;
    protected ResponseReceiver mResponseReceiver;

    String mMessage;
    String mTo;
    protected android.support.v7.app.ActionBar mActionBar;

    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    protected void initializeActionBar() {
        mActionBar = getSupportActionBar();
        //mActionBar.setBackgroundDrawable(new ColorDrawable(R.color.orange));
        //mActionBar.setCustomView(ActionBar.DISPLAY_SHOW_CUSTOM);
        //mActionBar.setTitle(R.string.altitude_title);
        //mActionBar.setSubtitle(R.string.angle);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle((Activity) sContext, /*
                                                                         * host
																		 * Activity
																		 */
                mDrawerLayout, /* DrawerLayout object */
                R.drawable.ic_drawer, /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open, /* "open drawer" description */
                R.string.drawer_close /* "close drawer" description */

        ) {

            /** Called when a drawer has settled in a completely closed state. */
            @Override
            public void onDrawerClosed(View view) {
                mActionBar.setTitle(getResources().getText(R.string.menu));
            }

            /** Called when a drawer has settled in a completely open state. */
            @Override
            public void onDrawerOpened(View drawerView) {

                if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                    mActionBar.setTitle(
                            getResources().getText(R.string.menu));

                } else if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    mActionBar.setTitle(
                            getApplication().getText(R.string.activities));
                } else {
                    mActionBar.setTitle(
                            getApplication().getText(R.string.menu));
                }

            }
        };
        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        //mActionBar.setBackgroundDrawable(new ColorDrawable(R.color.orange));

    }

    // Used to close all drawers from MainActivity
    public static void closeAllDrawers() {
        mDrawerLayout.closeDrawers();
    }

    // Not using response receiver currently as passing messages around using
    // handlers
    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "com.mapproject.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            String text = intent
                    .getStringExtra(ChatIntentService.PARAM_OUT_MSG);
            Utils.setToast(getContext(), text, Toast.LENGTH_SHORT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuactionbar, menu);
        mMenu = menu;

        AppSettings.setMenu(menu);
        // Set Up Activities Spinner
        MenuItem activitySpinnerItem = mMenu.findItem(R.id.activityTypeMenu);
//        Spinner s = (Spinner) mMenu.findItem(R.id.activityTypeMenu).getActionView();
        View activitySpinnerView = activitySpinnerItem.getActionView();
        if (activitySpinnerView instanceof Spinner) {

            Spinner spinner = (Spinner) activitySpinnerView;
            ActivitiesAdapter adapter = new ActivitiesAdapter(this);
            spinner.setAdapter(adapter);
            // int test=AppSettings.getActivityType();
            spinner.setSelection(AppSettings.getActivityType());
            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {
                    AppSettings.setActivityType(Integer.toString(position));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    // Do Nothing

                }

            });
        }

        // Set Up Activities Spinner
        MenuItem replaySpinnerItem = mMenu.findItem(R.id.replaySpeed);
//        Spinner rs = (Spinner) mMenu.findItem(R.id.replaySpeed).getActionView();

        Spinner replayView = (Spinner) replaySpinnerItem.getActionView();
        if (replayView instanceof Spinner) {
            Spinner replaySpinner = (Spinner) replayView;
            replaySpinner.setVerticalScrollBarEnabled(false);
            ReplayAdapter replayAdapter = new ReplayAdapter(this);
            replaySpinner.setAdapter(replayAdapter);
            replaySpinner.setSelection(AppSettings.getReplaySpeed());
            replaySpinner
                    .setOnItemSelectedListener(new OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent,
                                                   View view, int position, long id) {
                            AppSettings.setReplaySpeed(Integer
                                    .toString(position));
                            // Utils.setToast(getContext(), "Pos: "+
                            // Integer.toString(position), Toast.LENGTH_SHORT);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Do Nothing

                        }

                    });

        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
        mDrawerToggle.setDrawerIndicatorEnabled(true);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // setContentView(R.layout.activity_main);

            // MainActivity.setToast("portrait", 1);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // setContentView(R.layout.activity_main);

            // MainActivity.setToast("landscape", 1);
        }

        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret;
        mMenu.findItem(R.id.replaySpeed).setVisible(false);
        FragmentManager fm = getSupportFragmentManager();
        // FragmentTransaction ft = fm.beginTransaction();

        mMenu.findItem(R.id.replaySpeed).setVisible(false);
        if (item.getItemId() == android.R.id.home) {
            // setToast("HOME", Toast.LENGTH_SHORT);
            if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
                mDrawerLayout.openDrawer(Gravity.LEFT);
            }

            ret = true;
        } else if (item.getItemId() == R.id.import_data) {
            // Handle Settings
            Utils.setToast(this, "import data", Toast.LENGTH_LONG);
            GetActivitiesForMember getActivitiesForMember = new GetActivitiesForMember();
            String importedData = getActivitiesForMember
                    .getActivities(AppSettings.getMemberid());
            getActivitiesForMember = null;
            MotionDataSource mds = new MotionDataSource(getContext());
            mds.open();
            try {
                JSONArray jActivitiesImport = new JSONArray(importedData);
                for (int j = 0; j < jActivitiesImport.length(); j++) {
                    mds.open();
                    JSONObject jActivity = jActivitiesImport.getJSONObject(j);

                    JSONArray jMotionArray = jActivity.getJSONArray("motion");
                    JSONArray jEvents = jActivity.getJSONArray("events");
                    String activityId = jActivity.getString("activityid");
                    ActivityAssignment activityAssignment = new ActivityAssignment();
                    activityAssignment.setActivityId(activityId);
                    activityAssignment.setMemberId(AppSettings.getMemberid());
                    mds.createActivityAssignment(activityAssignment);

                    int motionCount = jMotionArray.length();
                    for (int i = 0; i < motionCount; i++) {
                        Motion motion = new Motion();
                        motion.populateMotionDBFromJSON(activityId,
                                jMotionArray.getJSONObject(i));
                        mds.createMotion(motion);
                        // Send to db to update
                    }
                    int eventCount = jEvents.length();
                    for (int i = 0; i < eventCount; i++) {
                        Events event = new Events();

                        event.setActivityId(activityId);
                        event.setEventType(jEvents.getJSONObject(i).getInt("eventtype"));
                        event.setEventSubType(jEvents.getJSONObject(i).getInt("eventsubtype"));
                        event.setEventTime(jEvents.getJSONObject(i).getLong("eventtime"));
                        event.setWeight(jEvents.getJSONObject(i).getDouble("weight"));
                        // Send to db to update
                        mds.createEvent(event);
                    }

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mds.close();
            mds = null;
            ret = true;

        } else if (item.getItemId() == R.id.musicPlayer) {
            @SuppressWarnings("deprecation")
            Intent intent = new Intent(MediaStore.INTENT_ACTION_MUSIC_PLAYER);
            startActivity(intent);

            // final PackageManager pm = getPackageManager();
            // try {
            // startActivities(new Intent[] {
            // pm.getLaunchIntentForPackage("com.pandora.android"),pm.getLaunchIntentForPackage("com.mapproject")
            // });
            // } catch (final Exception ignored) {
            // // Nothing to do
            // } finally {
            // finish();
            // }

            ret = true;
        } else if (item.getItemId() == R.id.loads) {
            mMenu.findItem(R.id.replaySpeed).setVisible(true);
            // Handle Settings
            // setToast("Chat", Toast.LENGTH_SHORT);
            if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                mDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }

            ret = true;
        } else if (item.getItemId() == R.id.information) {
            // Handle Information
            Intent infoIntent = new Intent(this, InfoWebActivity.class);
            startActivity(infoIntent);
            ret = true;

        } else if (item.getItemId() == R.id.img_share) {
            String shareFile = getText(R.string.sharefile).toString();
            // File folder = Utils.saveImage(shareFile,
            // surface)drawView.save(shareFile);
            File folder = new File(Environment.getExternalStorageDirectory()
                    + "/mapmymotion" + "/" + shareFile + ".png");

            Uri uriToImage = null;
            uriToImage = Uri.fromFile(folder);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
            shareIntent.setType("image/png");
            startActivity(Intent.createChooser(shareIntent, getResources()
                    .getText(R.string.send_to)));

            ret = true;
        } else if (item.getItemId() == R.id.setup) {
            // Handle Settings
            startActivity(new Intent(this, AppSettings.class));
            ret = true;
        } else if (item.getItemId() == R.id.save) {
            // Handle Save on Current Fragment
            // Fragment currentFragment = fm.findFragmentById(R.id.frag_master);

            // String className = currentFragment.getClass().getName();

            ret = true;

        } else if (item.getItemId() == R.id.quit) {
            // Quit Application
            onBackPressed();
            ret = true;

        } else {
            ret = super.onOptionsItemSelected(item);
        }
        return ret;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        // int height;
        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStackImmediate();
            // Set layout back to the master detail default
            // DrawerLayout drawerLayout = (DrawerLayout)
            // findViewById(R.id.drawer_layout);
            // LinearLayout detailLayout = (LinearLayout)
            // findViewById(R.id.linearlayout02);
            // drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            // height = drawerLayout.getBottom();
            // setLayoutParams(detailLayout, mHeight);
            mMenu.findItem(R.id.save).setVisible(false);
        } else {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.exitapp)
                    .setCancelable(false)
                    .setPositiveButton(R.string.yes,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Intent locationIntent = new Intent(
                                            getApplicationContext(),
                                            LocationIntentService.class);
                                    stopService(locationIntent);
                                    System.exit(0);

                                }
                            }).setNegativeButton(R.string.no, null).show();

        }
    }

    @Override
    public void sendDataToBottomDrawer(Message msg) {
        // LayOutTwo Obj = (LayOutTwo) getSupportFragmentManager()
        // .findFragmentById(R.id.frag_detail);
        // Obj.setMessage(msg);

    }

    @Override
    public void sendDataToRightDrawer(Message msg) {
        LayOutRightDrawer Obj = (LayOutRightDrawer) getSupportFragmentManager()
                .findFragmentById(R.id.right_drawer);
        Obj.setMessage(msg);
    }

    @Override
    public void sendDataToTopDrawer(Message msg) {
        LayoutMain Obj = (LayoutMain) getSupportFragmentManager()
                .findFragmentById(R.id.frag_master);
        Obj.setMessage(msg);

    }

    @Override
    public void sendDataToLeftDrawer(Message msg) {
        Fragment Obj = getSupportFragmentManager().findFragmentById(
                R.id.left_drawer);
        ((LayOutRightDrawer) Obj).setMessage(msg);

    }

    // Receive messages from the TCPClient
    @Override
    public void messageReceived(String message) {
        // GETS MESSAGE FROM TCPCLIENT-DO SOMETHING WITH IT
        Utils.setToast(this, message, Toast.LENGTH_LONG);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Start Location Service
        Intent locationIntent = new Intent(getApplicationContext(),
                LocationIntentService.class);
        stopService(locationIntent);

        System.exit(0);

    }

    protected static void setLayoutParams(LinearLayout linearLayout, int height) {
        ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
        // int paramsHeight = params.height;
        params.height = height;
        linearLayout.setLayoutParams(params);
    }

    // public static void setToast(String text, int duration) {
    //
    // Toast imageToast = new Toast(sContext);
    // LinearLayout toastLayout = new LinearLayout(sContext);
    //
    // toastLayout.setOrientation(LinearLayout.HORIZONTAL);
    // toastLayout.setBackgroundResource(R.drawable.rounded_edges);
    // ImageView image = new ImageView(sContext);
    // image.setImageResource(R.drawable.appicon);
    // toastLayout.addView(image);
    // TextView tv = new TextView(sContext);
    // tv.setTextColor(Color.BLUE);
    // //
    // tv.setTextSize(sContext.getResources().getDimension(R.dimen.text_size_small));
    // tv.setTextSize(18);
    // tv.setTypeface(Typeface.DEFAULT);
    // tv.setBackgroundColor(Color.TRANSPARENT);
    // tv.setText(text);
    // toastLayout.addView(tv);
    // imageToast.setView(toastLayout);
    // imageToast.setDuration(duration == 0 ? Toast.LENGTH_SHORT
    // : Toast.LENGTH_LONG);
    // imageToast.show();
    // }

    public static Context getContext() {
        return sContext;
    }

    protected void getPreferences(Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
    }

    // // Implements TextToSpeech.OnInitListener.
    // public void onInit(int status) {
    // // status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
    // if (status == TextToSpeech.SUCCESS) {
    // int result = mTts.setLanguage(Locale.getDefault());
    // // Try this someday for some interesting results.
    // if (result == TextToSpeech.LANG_MISSING_DATA
    // || result == TextToSpeech.LANG_NOT_SUPPORTED) {
    // Intent installIntent = new Intent();
    // installIntent
    // .setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
    // startActivity(installIntent);
    //
    // } else {
    // String locale = sContext.getResources().getConfiguration().locale
    // .getDisplayName();
    // Toast.makeText(sContext, locale, Toast.LENGTH_SHORT)
    // .show();
    // }
    // } else {
    // // Initialization failed.
    // // Log.e(TAG, "Could not initialize TextToSpeech.");
    // }
    //
    // }
    //
    // protected static void sayMessage(String m) {
    // mTts.speak(m, TextToSpeech.QUEUE_FLUSH, // Drop all pending entries in
    // // the playback queue.
    // null);
    // }

}

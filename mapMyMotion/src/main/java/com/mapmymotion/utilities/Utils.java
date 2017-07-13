package com.mapmymotion.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.mapmymotion.AppSettings;
import com.mapmymotion.Constants;
import com.mapmymotion.R;
import com.mapmymotion.pojo.Motion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Utils {

	public static String copyFileFromAssetandSave(Context context,
			String filePath, String assetFileName, String destPath,
			String fileName) {

		File destFolder = new File(Environment.getExternalStorageDirectory() + "/"
				+ destPath);

		// Check to see if the destination file already exists
		File destinationFile = new File(destFolder + "/" + fileName);

		if (!destinationFile.exists()) {
			// Check if the destination Folder exists and create if it doesn't
			if (!destFolder.exists()) {
				destFolder.mkdir();
			}

			try {
				// File zFile = new File(filePath + "/" + assetFileName);
				InputStream is = context.getAssets().open(
						filePath + "/" + assetFileName);
				int size = is.available();
				byte[] buffer = new byte[size];
				is.read(buffer);
				is.close();

				FileOutputStream fos = new FileOutputStream(
						destinationFile.getAbsolutePath());
				fos.write(buffer);
				fos.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return destinationFile.getAbsolutePath();
	}

	public static String getHMS(String label, long s) {
		// If label is null set to empty string;
		if (label == null) {
			label = "";
		}
		int HH = (int) (s / 3600);
		int MM = (int) ((s - (HH * 3600)) / 60);
		int SS = (int) (s - (HH * 3600) - (MM * 60));
		return label + String.format(" %02d:%02d:%02d", HH, MM, SS);
	}

	@SuppressLint("SimpleDateFormat") 
	public static String getHM(String label, long millis) {
		// If label is null set to empty string;
		if (label == null) {
			label = "";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("h:m a");
		dateFormat.setTimeZone(TimeZone.getDefault());
		// get current date time with Date()
		Date date = new Date(millis);
		return label + " " + dateFormat.format(date);
	}

	//Send email via google intent
	public static void sendEmail(Context context, String[] to, String[] bcc,
			String subject, String body,String filename, int type) {
		File fileToSend = new File(Environment.getExternalStorageDirectory()
				+ "/mapmymotion"+ "/" + filename + ".png");
		Uri uri = Uri.fromFile(fileToSend);
		Intent mailIntent = new Intent(Intent.ACTION_SEND);
		if (type == 0) {
			mailIntent.setClassName("com.google.android.gm",
					"com.google.android.gm.ComposeActivityGmail");
		}
		mailIntent.setType("message/rfc822");
		mailIntent.putExtra(Intent.EXTRA_EMAIL, to);
		mailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		mailIntent.putExtra(Intent.EXTRA_TEXT, body);
		mailIntent.putExtra(Intent.EXTRA_BCC, body);
		mailIntent.putExtra(Intent.EXTRA_STREAM, uri);
		try {
			// context.startActivity(Intent.createChooser(i, "Send mail..."));
			if (type == 0) {
				context.startActivity(mailIntent);
			} else {
				context.startActivity(Intent.createChooser(mailIntent,
						context.getString(R.string.noemailclient)));
			}
			
		} catch (android.content.ActivityNotFoundException ex) {
			setToast(context, context.getString(R.string.noemailclient),
					Toast.LENGTH_LONG);
		}
	}

	public static String getMFI(String label, double meters) {
		// If label is null set to empty string;
		if (label == null) {
			label = "";
		}
		;
		int miles = (int) (meters / 1609.344);
		int feet = (int) ((meters - (miles * 1609.344)) * 3.280839895);
		int inches = (int) (((meters - (miles * 1609.344) - (feet * .3048))) * 39.37007874);
		return label
				+ String.format(" %02d mi %02d' %02d\"", miles, feet, inches);
	}

	public static String getMF(String label, double meters) {
		// If label is null set to empty string;
		if (label == null) {
			label = "";
		}
		;

		int miles = (int) (meters / 1609.344);
		int feet = (int) ((meters - (miles * 1609.344)) * 3.280839895);
		// int inches = (int) ( ( (meters - (miles * 1609.344) - (feet * .3048)
		// ) )*39.37007874);
		return label + String.format(Locale.US, " %d mi %d'", miles, feet);
	}

	public static String formatSpeed(String label, double speed) {
		String unitString = (AppSettings.getUOM() == 0) ? "kilometers per hour"
				: "miles per hour";
		// Set the Current Speed
		String myCurrentSpeed = label
				+ String.format(Locale.US, " %4.1f %s", convertSpeed(speed),
						unitString);
		return myCurrentSpeed;

	}

	public static String getMS(String label, double m) {
		// If label is null set to empty string;
		if (label == null) {
			label = "";
		}
		;
		// label=(label.isEmpty())?"":label;
		int MM = (int) m;
		int SS = (int) (m % 1 * 60);
		if (label.length() == 0) {
			label = String.format(Locale.US, " %02d:%02d", MM, SS);
		} else {

			label = label + String.format(" %2d minutes %2d seconds", MM, SS);
		}
		return label;// + String.format(" %02d:%02d", MM, SS);
	}

	public static Double convertSpeedToPace(double speed) {
		// double uph = speed*Constants.METERSPERSSEC_TOPACE[mMeasurementIndex];
		double uph = speed * 3600
				* Constants.UNIT_MULTIPLIERS[AppSettings.getUOM()];

		uph = (uph > 0) ? (60 / uph) : 0;
		// Dont want ridiculously high pace so i am going to set it to 0
		uph = uph > 1000 ? 0 : uph;
		return uph;
	}
	
	
	public static String convertSpeedToPace(String label, double speed) {
		// double uph = speed*Constants.METERSPERSSEC_TOPACE[mMeasurementIndex];
		double uph = speed * 3600
				* Constants.UNIT_MULTIPLIERS[AppSettings.getUOM()];

		uph = (uph > 0) ? (60 / uph) : 0;
		// Dont want ridiculously high pace so i am going to set it to 0
		uph = uph > 1000 ? 0 : uph;
		return getMS(label, uph);
	}

	public static String getMilesorKm() {
		return (AppSettings.getUOM() == 0) ? " km" : " mi";
	}

	public static BitmapDrawable writeOnCircle(Resources resources,
			String text, int w, int h, int textSize, int circleColor) {
		EmbossMaskFilter mEmboss = new EmbossMaskFilter(
				new float[] { 1, 1, 1 }, 0.4f, 6, 3.5f);

		// Set the size of the bitmap
		Rect rect = new Rect(0, 0, w, h);

		// Create a bitmap to write on
		Bitmap bm = Bitmap.createBitmap(rect.width(), rect.height(),
				Config.ARGB_8888);

		// Create canvas to draw on from the bitmap
		Canvas canvas = new Canvas(bm);
		// Make background of the canvas transparent
		canvas.drawColor(android.R.color.transparent);

		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setColor(circleColor);

		paint.setAntiAlias(true);
		// Draw the circle using the paint color and fill type
		canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 2,
				paint);
		// Set the text style and color
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(textSize);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(Color.BLUE);

		// Check the size of the passed text
		Rect bounds = new Rect();
		paint.setMaskFilter(mEmboss);
		paint.getTextBounds(text, 0, 1, bounds);

		// Draw the text
		canvas.drawText(text, canvas.getWidth() >> 1,
				(canvas.getHeight() + bounds.height()) >> 1, paint);

		// Return the created bitmap
		return new BitmapDrawable(resources, bm);
	}

	// Used to get a bitmapdrawablefrom an id
	public static BitmapDrawable writeOnDrawable(Resources res, int drawableId,
			String text) {
		// Create a bitmap to write on
		Bitmap bm = BitmapFactory.decodeResource(res, drawableId).copy(
				Bitmap.Config.ARGB_8888, true);

		// Create canvas to draw on from the bitmap
		Canvas canvas = new Canvas(bm);
		// Make background of the canvas transparent
		canvas.drawColor(android.R.color.transparent);

		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setTypeface(Typeface.DEFAULT);
		paint.setTextSize(24);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(Color.WHITE);
		paint.setAntiAlias(true);

		// Check the size of the passed text
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, 1, bounds);
		// Draw the text
		canvas.drawText(text, canvas.getWidth() >> 1,
				(canvas.getHeight() + bounds.height()) >> 1, paint);
		// Return the created bitmap
		return new BitmapDrawable(res, bm);
	}

	// Used to get a Create a bitmap with text on it
	public static Bitmap writeOnBitmap(String text, int w, int h) {
		// Create an empty immutable bitmap
		Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		// Create canvas to draw on from the bitmap
		Canvas canvas = new Canvas(bm);
		// Make background of the canvas transparent
		canvas.drawColor(android.R.color.transparent);

		Paint paint = new Paint();
		paint.setStyle(Style.FILL);
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setTextSize(40);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setColor(Color.BLUE);

		// Check the size of the passed text
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, 1, bounds);
		// Draw the text
		canvas.drawText(text, canvas.getWidth() >> 1,
				(canvas.getHeight() + bounds.height()) >> 1, paint);
		// Return the created bitmap
		return bm;
	}

	// Custom toast
	public static void setToast(Context context, String text, int duration) {

		Toast imageToast = new Toast(context);
		LinearLayout toastLayout = new LinearLayout(context);

		toastLayout.setOrientation(LinearLayout.HORIZONTAL);
		toastLayout.setBackgroundResource(R.drawable.rounded_edges);
		ImageView image = new ImageView(context);
		image.setImageResource(R.drawable.appicon);
		toastLayout.addView(image);
		TextView tv = new TextView(context);
		tv.setTextColor(Color.BLUE);
		// tv.setTextSize(sContext.getResources().getDimension(R.dimen.text_size_small));
		tv.setTextSize(18);
		tv.setTypeface(Typeface.DEFAULT);
		tv.setBackgroundColor(Color.TRANSPARENT);
		tv.setText(text);
		toastLayout.addView(tv);
		imageToast.setView(toastLayout);
		imageToast.setDuration(duration == 0 ? Toast.LENGTH_SHORT
				: Toast.LENGTH_LONG);
		imageToast.show();
	}

	// Used to set text to by id. i.e. R.id.mycolumn
	public static void setTextById(int textid, String text, ViewGroup root) {
		Spannable span = new SpannableString(text);
		AbsoluteSizeSpan sizeSpanLarge = null;
		AbsoluteSizeSpan sizeSpanSmall = null;

		int firstPos = text.indexOf(32);

		span.setSpan(sizeSpanLarge, 0, firstPos,
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		span.setSpan(sizeSpanSmall, firstPos + 1, text.length(),
				Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

		TextView tv = ((TextView) root.findViewById(textid));

		tv.setText(span);

	}

	// Pass in an id and return the string for it
	public static String formatDate(String label, long epochTime) {
		// If label is null set to empty string;
		if (label == null) {
			label = "";
		}
		;
		Date date = new Date(epochTime); // *1000 is to convert seconds to
											// milliseconds
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.US); // the format of your date
		// sdf.setTimeZone(TimeZone.getDefault());
		return label + " " + sdf.format(date);
	}

	// Pass in an id and return the string for it
	public static String getStringFromResource(Context context, int id) {
		return context.getResources().getString(id);
	}

	// Converts speed to mph or kmph
	public static double convertSpeed(double speed) {
		return ((speed * Constants.HOUR_MULTIPLIER) * Constants.UNIT_MULTIPLIERS[AppSettings
				.getUOM()]);
	}

	// Converts from to meters to feet
	public static double convertMetricToFeet(double meters) {
		// If in US we need to convert metric to feet else report in meters
		if (AppSettings.getUOM() == Constants.INDEX_MILES) {
			meters = (meters * Constants.METERSANDFEET[AppSettings.getUOM()]);
		}
		return (meters);
	}

	public static double roundDecimal(double value, final int decimalPlace) {
		BigDecimal bd = new BigDecimal(value);

		bd = bd.setScale(decimalPlace, RoundingMode.HALF_UP);
		value = bd.doubleValue();

		return value;
	}

	// Create a list of all messages available to speech and info window
	// purposes
	public static Map<String, String> createFormattedMessage(Context context,
			Motion motion) {
		Map<String, String> toSay = new HashMap<String, String>();

		// Set the Current Speed
		String currentSpeed = formatSpeed(context.getString(R.string.speedis),
				motion.getCurrentSpeed()) + "\n";
		toSay.put("currentSpeed", currentSpeed);

		// Average Speed
		String averageSpeed = formatSpeed(
				context.getString(R.string.averagespeed),
				motion.getAverageSpeed())
				+ "\n";
		toSay.put("averageSpeed", averageSpeed);

		// Set the total Distance
		String distance = context.getString(R.string.distanceis)
				+ String.format(
						" %.2f %s",
						(motion.getTotalDistance() * Constants.UNIT_MULTIPLIERS[AppSettings
								.getUOM()]), (AppSettings.getUOM() == 0) ? "km"
								: "miles") + "\n";
		toSay.put("distance", distance);

		// Pace
		String pace = convertSpeedToPace(context.getString(R.string.pacetext),
				motion.getAverageSpeed()) + "\n";
		toSay.put("pace", pace);

		// Calculate and display Duration...Elapsed Time
		String duration = getHMS(context.getString(R.string.durationis),
				motion.getTimeElapsed())
				+ "\n";
		toSay.put("duration", duration);

		// Current Time
		String currentTime = getHM(context.getString(R.string.current_time),
				motion.getCurrentTime());
		toSay.put("currentTime", currentTime);

		// If pace is below indicated minimum pace tell runner to speed
		// up

		if ((60 / convertSpeed(motion.getAverageSpeed()) > AppSettings
				.getMinPace())
				&& AppSettings.getMinPace() != Constants.MINIMUMPACE) {
			String minPace = context.getString(R.string.pickupspeed) + "\n";
			toSay.put("minPace", minPace);
		}
		toSay.put("activitytype", Integer.toString(motion.getActivityType()));
		return toSay;
	}

	//Check Email address to see if it is valid
	public static boolean checkEmail(String email) {
		return Constants.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
	}

	//Capture picture of the Google Map Screen for transmittal
	public static void captureMapScreen(String filename, GoogleMap map,final String toDisplay,final Context context) {

		File folder = new File(Environment.getExternalStorageDirectory()
				+ "/mapmymotion");

		final File fileName = new File(folder + "/" + filename + ".png");
		
		SnapshotReadyCallback callback = new SnapshotReadyCallback() {
			Bitmap bitmap;

			@Override
			public void onSnapshotReady(Bitmap snapshot) {
				bitmap = snapshot;

				try {
					// FileOutputStream out = new
					// FileOutputStream("/mnt/sdcard/"
					// + "MyMapScreen" + System.currentTimeMillis()
					// + ".png");
					FileOutputStream out = new FileOutputStream(fileName);

					// above "/mnt ..... png" => is a storage path (where image
					// will be stored) + name of image you can customize as per
					// your Requirement

					bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);

					try {
						//JMail jMail=new JMail(context,toDisplay);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
					
//					StringBuilder j =  new StringBuilder();
//					j.append(AppSettings.getEmailAddress());
//					String[] arr =  j.toString().split(",");
//					sendEmail(context,
//							arr,
//							new String[] { "rbarnes0154@verizon.net" }, "Statistics", toDisplay,"mapmymotion",
//							0);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		map.snapshot(callback);

		// myMap is object of GoogleMap +> GoogleMap myMap;
		// which is initialized in onCreate() =>
		// myMap = ((SupportMapFragment)
		// getSupportFragmentManager().findFragmentById(R.id.map_pass_home_call)).getMap();
	}
}

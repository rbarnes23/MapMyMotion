package com.mapmymotion;

import java.util.Locale;
import java.util.Map;

import com.mapmymotion.pojo.Motion;
import com.mapmymotion.utilities.Utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

public class TextToSound implements TextToSpeech.OnInitListener {
	public TextToSpeech mTts;
	private Context mContext;

	public TextToSound(Context context) {
		// Initialize text-to-speech. This is an asynchronous operation.
		// The OnInitListener (second argument) is called after initialization
		// completes.
		mContext = context;
		// mTts = (TextToSpeech)new TextToSpeech(context, this);
		mTts = new TextToSpeech(context, this);
		StringBuffer toSay = new StringBuffer();
		toSay.append("the world is round");
		sayMessage(toSay.toString());

	}

	// Implements TextToSpeech.OnInitListener.
	@Override
	public void onInit(int status) {
		// status can be either TextToSpeech.SUCCESS or TextToSpeech.ERROR.
		if (status == TextToSpeech.SUCCESS) {
			int result = mTts.setLanguage(Locale.getDefault());
			// Try this someday for some interesting results.
			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Utils.setToast(
						mContext,
						"Text to Speech missing Language or text to speech is not available on your device.",
						Toast.LENGTH_LONG);

			} else {
				String locale = mContext.getResources().getConfiguration().locale
						.getDisplayName();
				Utils.setToast(mContext, locale, Toast.LENGTH_SHORT);
			}
		} else {
			// Initialization failed.
			Utils.setToast(mContext, "Unable to initialize Text to Speech",
					Toast.LENGTH_SHORT);
		}

	}

	// Pass in an id and return the string for it
	public String getStringFromResource(int id) {
		return mContext.getResources().getString(id);
	}

	// Create the message to say from the motion object
	public void createVoiceMessage(final Motion motion,
			final Map<String, String> map) {

		new Thread() {
			@Override
			public void run() {
				// String unitString = (AppSettings.getUOM() == 0) ? "km/h"
				// : "mi/h";
				// // Set the Current Speed
				// String myCurrentSpeed
				// =Utils.formatSpeed(getStringFromResource(R.string.speedis),
				// motion.getCurrentSpeed());
				// String myAverageSpeed
				// =Utils.formatSpeed(getStringFromResource(R.string.averagespeed),
				// motion.getAverageSpeed());
				//
				//
				// // Set the total Distance
				// String myDistance =
				// getStringFromResource(R.string.distanceis)
				// + String.format(
				// " %.2f %s",
				// (motion.getTotalDistance() *
				// Constants.UNIT_MULTIPLIERS[AppSettings
				// .getUOM()]),
				// (AppSettings.getUOM() == 0) ? "km" : "miles");
				// // String myPace =
				// // Utils.getMS(getStringFromResource(R.string.pacetext),
				// // Utils.convertSpeed(motion.getPace()));
				// // String myPace =
				// //
				// Utils.getMS(getStringFromResource(R.string.pacetext),(60/Utils.convertSpeed(motion
				// // .getAverageSpeed())));
				// String myPace = Utils.convertSpeedToPace(
				// getStringFromResource(R.string.pacetext),
				// motion.getAverageSpeed());
				//
				// // Calculate and display Duration...Elapsed Time
				// String myDuration = Utils.getHMS(
				// getStringFromResource(R.string.durationis),
				// motion.getTimeElapsed());
				//
				// // Say the voice notifications selected
				// StringBuffer toSay = new StringBuffer("");
				// // If pace is below indicated minimum pace tell runner to
				// speed
				// // up
				//
				// if ( (60 / Utils.convertSpeed(motion.getAverageSpeed()) >
				// AppSettings
				// .getMinPace())&&AppSettings
				// .getMinPace()!=Constants.MINIMUMPACE) {
				// toSay.append(getStringFromResource(R.string.pickupspeed)+". ");
				// }
				// Say the voice notifications selected
				StringBuffer toSay = new StringBuffer("");

				if (map.get("minPace") != null) {
					toSay.append(map.get("minPace") + ". ");
				}

				if (AppSettings.getDistanceCue() == true) {
					toSay.append(map.get("distance") + ". ");
				}

				if (AppSettings.getCurrentSpeedCue() == true) {
					toSay.append(map.get("currentSpeed") + ". ");

				}

				if (AppSettings.getAverageSpeedCue() == true) {
					toSay.append(map.get("averageSpeed") + ". ");
				}
				;
				if (AppSettings.getPaceCue() == true) {
					toSay.append(map.get("pace") + ". ");
				}

				if (AppSettings.getDurationCue() == true) {
					toSay.append(map.get("duration") + ". ");
				}
				if (AppSettings.getAltitudeCue() == true) {
					toSay.append(map.get("altitude") + ". ");
				}

				if (AppSettings.getCurrentTimeCue() == true) {
					toSay.append(map.get("currentTime") + ". ");
				}

				sayMessage(toSay.toString());
			}
		}.start();

	}

	public void sayMessage(String message) {
		if (message != null) {
			mTts.speak(message, TextToSpeech.QUEUE_FLUSH, // Drop all pending
															// entries in
					// the playback queue.
					null);
		}
	}
}

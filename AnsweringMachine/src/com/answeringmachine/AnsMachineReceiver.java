package com.answeringmachine;

import java.io.File;
import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.util.Log;

public class AnsMachineReceiver extends BroadcastReceiver {
	private static String mFileName;
	private static MediaRecorder mRecorder;
	private String LOG_TAG;
	SharedPreferences prefs;
	AnsMachineNotifier notifier;
	String number="";

	@Override
	public void onReceive(Context context, Intent intent) {
		notifier = new AnsMachineNotifier(context);
		// Load preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(context);

		// Check phone state
		String phone_state = intent
				.getStringExtra(TelephonyManager.EXTRA_STATE);
		number += intent
				.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);

		if (phone_state.equals(TelephonyManager.EXTRA_STATE_RINGING)
				&& prefs.getBoolean("enabled", false)) {
			// Check for "second call" restriction
			if (prefs.getBoolean("no_second_call", false)) {
				AudioManager am = (AudioManager) context
						.getSystemService(Context.AUDIO_SERVICE);
				if (am.getMode() == AudioManager.MODE_IN_CALL) {
					return;
				}
			}

			// Check for contact restrictions
			String which_contacts = prefs.getString("which_contacts", "all");
			if (!which_contacts.equals("all")) {
				int is_starred = isStarred(context, number);
				if (which_contacts.equals("contacts") && is_starred < 0) {
					return;
				} else if (which_contacts.equals("starred") && is_starred < 1) {
					return;
				}
			}
			if (prefs.getBoolean("send_msg", false)) {
				SmsManager smsManager = SmsManager.getDefault();
				smsManager.sendTextMessage(number, null, "Sorry, the Receiver is not around. Please leave an audio message after the call has been answered.Thank you!", null,
						null);
			}
			try {
				Thread.sleep(2 * 1000);
			} catch (InterruptedException e) {
				// We don't really care
			}
			// Call a service, since this could take a few seconds
			context.startService(new Intent(context,
					AnsMachineIntentService.class));
			
			try {
				// TELEPHONY MANAGER class object to register one listner
				TelephonyManager tmgr = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);

				// Create Listner
				MyPhoneStateListener PhoneListener = new MyPhoneStateListener(
						context);

				// Register listener for LISTEN_CALL_STATE
				tmgr.listen(PhoneListener, PhoneStateListener.LISTEN_CALL_STATE);

			} catch (Exception e) {
				Log.e("Phone Receive Error", " " + e);
			}
		}
	}

	// returns -1 if not in contact list, 0 if not starred, 1 if starred
	private int isStarred(Context context, String number) {
		int starred = -1;
		Cursor c = context.getContentResolver().query(
				Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, number),
				new String[] { PhoneLookup.STARRED }, null, null, null);
		if (c != null) {
			if (c.moveToFirst()) {
				starred = c.getInt(0);
			}
			c.close();
		}
		return starred;
	}

	private class MyPhoneStateListener extends PhoneStateListener {
		MyPhoneStateListener(Context context) {
		}

		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				Log.d("Answering Machine:", "CALL RECORDING STOPPING!!");
				stopRecording();

				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.d("TestServiceReceiver", "OFFHOOK");
				break;
			case TelephonyManager.CALL_STATE_RINGING:

				try {
					Thread.sleep(5 * 1000);
				} catch (InterruptedException e) {
					// We don't really care
				}
				Log.d("Answering Machine:", "CALL RECORDING STARTING!!");
				if (mRecorder == null) {
					startRecording();
					notifier.playMessage(mFileName);
				}

				break;
			}
		}

		private void stopRecording() {
			// TODO Auto-generated method stub
			if (mRecorder != null) {
				mRecorder.stop();
				mRecorder.reset();
				mRecorder.release();
				mRecorder = null;
			}

		}

		private void startRecording() {
			// TODO Auto-generated method stub
			mFileName = Environment.getExternalStorageDirectory().getPath();
			File file = new File(mFileName, "audioMessages");

			if (!file.exists()) {
				file.mkdirs();
			}
			mFileName = file.getAbsolutePath() + "/"
					+ number + ".mp3";
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_DOWNLINK);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setOutputFile(mFileName);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
			mRecorder.setAudioChannels(1);

			try {
				mRecorder.prepare();
				// mRecorder.start();
			} catch (IOException e) {
				Log.e(LOG_TAG, "prepare() failed");

			}
			mRecorder.start();
		}
	}
}

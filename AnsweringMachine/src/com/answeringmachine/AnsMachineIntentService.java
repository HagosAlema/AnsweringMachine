package com.answeringmachine;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class AnsMachineIntentService extends IntentService {
	public AnsMachineIntentService() {
		super("AnsMachineIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("AutoAnswerService:", "Answering Machine started!!!");
		Context context = getBaseContext();

		// Load preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		try {
			Thread.sleep(4 * 1000);
		} catch (InterruptedException e) {
			// We don't really care
		}

		// Make sure the phone is still ringing
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		// ListenToPhoneState listener = new ListenToPhoneState();
		// tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		if (tm.getCallState() != TelephonyManager.CALL_STATE_RINGING) {
			return;
		}
		// Answer the phone
		//to answer using answerPhoneAidl you have to have root permission on your mobile
		/*try {
			answerPhoneAidl(context);
		} catch (Exception e) {
			e.printStackTrace();
			Log.d("AutoAnswer",
					"Error trying to answer using telephony service.  Falling back to headset.");
			Toast.makeText(
					this,
					"Error trying to answer using telephony service.  Falling back to headset.",
					Toast.LENGTH_SHORT).show();
			answerPhoneHeadsethook(context);
		}*/
		answerPhoneHeadsethook(context);
		// Enable the speakerphone
		if (prefs.getBoolean("use_speakerphone", false)) {
			enableSpeakerPhone(context);
		}

		return;
	}

	private void enableSpeakerPhone(Context context) {
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		audioManager.setSpeakerphoneOn(true);
	}

	private void answerPhoneHeadsethook(Context context) {
		// Simulate a press of the headset button to pick up the call
		Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
		buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
				KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
		context.sendOrderedBroadcast(buttonDown,
				"android.permission.CALL_PRIVILEGED");

		// froyo and beyond trigger on buttonUp instead of buttonDown
		Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
		buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(
				KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
		context.sendOrderedBroadcast(buttonUp,
				"android.permission.CALL_PRIVILEGED");
	}

	/*@SuppressWarnings("unchecked")
	private void answerPhoneAidl(Context context) throws Exception {
		// Set up communication with the telephony service (thanks to Tedd's
		// Droid Tools!)
		//startRecording();
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		Class c = Class.forName(tm.getClass().getName());
		Method m = c.getDeclaredMethod("getITelephony");
		m.setAccessible(true);
		ITelephony telephonyService;
		telephonyService = (ITelephony) m.invoke(tm);

		// Silence the ringer and answer the call!
		telephonyService.silenceRinger();
		telephonyService.answerRingingCall();

	}*/
	
}
package com.answeringmachine;

import com.answeringmachine.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

public class AnsMachineNotifier {

        private final static int NOTIFICATION_ID = 1;
        
        private Context mContext;
        private NotificationManager mNotificationManager;
        private SharedPreferences mSharedPreferences;

		private int NOTIFICATION_ID2 = 123;
        
        public AnsMachineNotifier(Context context) {
                mContext = context;
                mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                
                mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        }

        public void updateNotification() {
                if (mSharedPreferences.getBoolean("enabled", false)) {
                        this.enableNotification();
                }
                else {
                        this.disableNotification();
                }               
        }
        
        private void enableNotification() {
                // Intent to call to turn off AutoAnswer
                Intent notificationIntent = new Intent(mContext, AnsMachinePreferenceActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
                
                // Create the notification
                Notification n = new Notification(R.drawable.icon, null, 0);
                n.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
                n.setLatestEventInfo(mContext, mContext.getString(R.string.notification_title), mContext.getString(R.string.notification_text), pendingIntent);
                mNotificationManager.notify(NOTIFICATION_ID, n);
                
        }
        
        private void disableNotification() {
                mNotificationManager.cancel(NOTIFICATION_ID);
        }
        public void playMessage(String fileName){
        	Intent playMusic = new Intent(Intent.ACTION_VIEW);
        	playMusic.setDataAndType(Uri.parse("file:///"+fileName), "audio/mp3");
        	PendingIntent pendingMessage = PendingIntent.getActivity(mContext, 1, playMusic, 0);
        	 Notification n = new Notification(R.drawable.icon, "You have one Message", System.currentTimeMillis());
             n.flags |= Notification.FLAG_AUTO_CANCEL;
             n.defaults = Notification.DEFAULT_ALL;
             
             n.setLatestEventInfo(mContext, "New Audio Message", "You have one Message", pendingMessage);
             mNotificationManager.notify(NOTIFICATION_ID2 , n);
        }
}

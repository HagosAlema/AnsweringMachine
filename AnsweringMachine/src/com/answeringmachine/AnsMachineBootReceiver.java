package com.answeringmachine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AnsMachineBootReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
                AnsMachineNotifier notifier = new AnsMachineNotifier(context);
                notifier.updateNotification();
        }

}

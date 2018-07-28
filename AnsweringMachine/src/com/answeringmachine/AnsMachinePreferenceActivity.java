package com.answeringmachine;

import java.io.File;

import com.answeringmachine.R;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AnsMachinePreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

        private AnsMachineNotifier mNotifier;
        
        @SuppressWarnings("deprecation")
		@Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                mNotifier = new AnsMachineNotifier(this);
                mNotifier.updateNotification();
                SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
                sharedPreferences.registerOnSharedPreferenceChangeListener(this);
                addPreferencesFromResource(R.xml.preferences);
        }

        @SuppressWarnings("deprecation")
		@Override
        protected void onDestroy() {
                getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
                super.onDestroy();
        }
        
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("enabled")) {
                        mNotifier.updateNotification();
                }
        }
}
 
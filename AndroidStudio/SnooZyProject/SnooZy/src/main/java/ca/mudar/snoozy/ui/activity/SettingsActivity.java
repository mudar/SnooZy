/*
    SnooZy Charger
    Power Connection manager. Turn the screen off on power connection
    or disconnection, to save battery consumption by the phone's display.

    Copyright (C) 2013 Mudar Noufal <mn@mudar.ca>

    This file is part of SnooZy Charger.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ca.mudar.snoozy.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import java.util.List;

import ca.mudar.snoozy.Const;
import ca.mudar.snoozy.R;
import ca.mudar.snoozy.util.ComponentHelper;

import static ca.mudar.snoozy.util.LogUtils.makeLogTag;

public class SettingsActivity extends PreferenceActivity {
    private static final String TAG = makeLogTag(SettingsActivity.class);

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SettingsFragment.class.getName().equals(fragmentName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle(R.string.activity_settings);

        showBreadCrumbs(getResources().getString(R.string.prefs_breadcrumb), null);
    }

    @Override
    public Intent getIntent() {
        // Override the original intent to remove headers and directly show SettingsFragment
        final Intent intent = new Intent(super.getIntent());
        intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, SettingsFragment.class.getName());
        intent.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
        return intent;
    }

    /**
     * Populate the activity with the top-level headers.
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Respond to the action bar's Up/Home button
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This fragment shows the preferences for the first header.
     */
    public static class SettingsFragment extends PreferenceFragment implements
            OnSharedPreferenceChangeListener {

        protected SharedPreferences mSharedPrefs;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            PreferenceManager pm = this.getPreferenceManager();
            pm.setSharedPreferencesName(Const.APP_PREFS_NAME);
            pm.setSharedPreferencesMode(Context.MODE_PRIVATE);

            // Load the preferences from an XML resource
            final Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            if (v.hasVibrator()) {
                addPreferencesFromResource(R.xml.preferences);
            } else {
                addPreferencesFromResource(R.xml.preferences_no_vibrator);
            }

            mSharedPrefs = pm.getSharedPreferences();

            final PreferenceScreen deviceAdminPrefScreen = (PreferenceScreen) findPreference(Const.PrefsNames.DEVICE_ADMIN);
            deviceAdminPrefScreen.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    launchDeviceAdminSettings();

                    return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();

            /**
             * Set up a listener whenever a key changes
             */
            if (mSharedPrefs != null) {
                mSharedPrefs.registerOnSharedPreferenceChangeListener(this);
            }

            /**
             * Based on device admin status, toggle device admin summary and screen lock options
             */
            final boolean isDeviceAdmin = ComponentHelper.isDeviceAdmin(getActivity());

            final PreferenceScreen deviceAdminPrefScreen = (PreferenceScreen) findPreference(Const.PrefsNames.DEVICE_ADMIN);
            final Preference onPowerLoss = findPreference(Const.PrefsNames.ON_POWER_LOSS);
            final Preference onScreenLock = findPreference(Const.PrefsNames.ON_SCREEN_LOCK);
            final Preference delayToLock = findPreference(Const.PrefsNames.DELAY_TO_LOCK);
            final Preference cacheAge = findPreference(Const.PrefsNames.CACHE_AGE);

            deviceAdminPrefScreen.setSummary(isDeviceAdmin ?
                    R.string.prefs_device_admin_summary_enabled : R.string.prefs_device_admin_summary_disabled);
            onPowerLoss.setEnabled(isDeviceAdmin);
            onScreenLock.setEnabled(isDeviceAdmin);
            delayToLock.setSummary(getLockDelaySummary());
            cacheAge.setSummary(getCacheAgeSummary());
        }

        @Override
        public void onPause() {
            /**
             * Remove the listener onPause
             */
            if (mSharedPrefs != null) {
                mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
            }

            super.onPause();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            /**
             * onChanged, new preferences values are sent to the AppHelper.
             */
            if (key.equals(Const.PrefsNames.IS_ENABLED)) {
                ComponentHelper.togglePowerConnectionReceiver(getActivity().getApplicationContext(),
                        sharedPreferences.getBoolean(key, false));
            } else if (key.equals(Const.PrefsNames.DELAY_TO_LOCK)) {
                final Preference delayToLock = findPreference(Const.PrefsNames.DELAY_TO_LOCK);
                delayToLock.setSummary(getLockDelaySummary());
            } else if (key.equals(Const.PrefsNames.CACHE_AGE)) {
                final Preference cacheAge = findPreference(Const.PrefsNames.CACHE_AGE);
                cacheAge.setSummary(getCacheAgeSummary());
            }
        }

        private void launchDeviceAdminSettings() {
            try {
                Intent intentDeviceAdmin = new Intent(Settings.ACTION_SETTINGS);
                intentDeviceAdmin.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intentDeviceAdmin.setClassName(Const.IntentActions.ANDROID_SETTINGS, Const.IntentActions.ANDROID_DEVICE_ADMIN);
                startActivity(intentDeviceAdmin);
            } catch (Exception e) {
                e.printStackTrace();
                Intent intentSecuritySettings = new Intent(Settings.ACTION_SECURITY_SETTINGS);
                intentSecuritySettings.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentSecuritySettings);
            }
        }

        private int getLockDelaySummary() {
            final String value = mSharedPrefs.getString(Const.PrefsNames.DELAY_TO_LOCK, Const.PrefsValues.DELAY_FAST);
            int res;
            if (Const.PrefsValues.DELAY_SLOW.equals(value)) {
                res = R.string.prefs_delay_slow;
            } else if (Const.PrefsValues.DELAY_MODERATE.equals(value)) {
                res = R.string.prefs_delay_moderate;
            } else {
                res = R.string.prefs_delay_fast;
            }

            return res;
        }

        private int getCacheAgeSummary() {
            final String value = mSharedPrefs.getString(Const.PrefsNames.CACHE_AGE, Const.PrefsValues.CACHE_ALL);
            int res;
            if (Const.PrefsValues.CACHE_NONE.equals(value)) {
                res = R.string.prefs_cache_age_none;
            } else if (Const.PrefsValues.CACHE_SMALL.equals(value)) {
                res = R.string.prefs_cache_age_small;
            } else if (Const.PrefsValues.CACHE_MEDIUM.equals(value)) {
                res = R.string.prefs_cache_age_medium;
            } else if (Const.PrefsValues.CACHE_LARGE.equals(value)) {
                res = R.string.prefs_cache_age_large;
            } else {
                res = R.string.prefs_cache_age_all;
            }

            return res;
        }
    }
}

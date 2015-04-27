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

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.widget.Toast;

import ca.mudar.snoozy.Const;
import ca.mudar.snoozy.R;
import ca.mudar.snoozy.ui.fragment.HistoryFragment;
import ca.mudar.snoozy.util.CacheHelper;
import ca.mudar.snoozy.util.ComponentHelper;

import static ca.mudar.snoozy.util.LogUtils.makeLogTag;

public class MainActivity extends BaseActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = makeLogTag(MainActivity.class);
    private boolean mHasDeviceAdminIntent = false;
    private Toast mToast;

    public static Intent newIntent(Context context) {
        final Intent intent = new Intent(context, MainActivity.class);

        final Bundle extras = new Bundle();
        extras.putBoolean(Const.IntentExtras.INCREMENT_NOTIFY_GROUP, true);
        extras.putBoolean(Const.IntentExtras.RESET_NOTIFY_NUMBER, true);
        intent.putExtras(extras);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializePrefsAndListener();

        if (getIntent().getExtras() != null) {
            updateNotifyPrefs(getIntent());
            getIntent().removeExtra(Const.IntentExtras.RESET_NOTIFY_NUMBER);
            getIntent().removeExtra(Const.IntentExtras.INCREMENT_NOTIFY_GROUP);
        }

        final FragmentManager fm = getFragmentManager();

        if (fm.findFragmentById(android.R.id.content) == null) {
            HistoryFragment fragment = new HistoryFragment();
            fm.beginTransaction().add(android.R.id.content, fragment).commit();
        }

        if (ComponentHelper.isDeviceAdmin(this)) {
            // Start the service component which will register the BroadcastReceiver
            togglePowerConnectionReceiver(true);
        } else {
            togglePowerConnectionReceiver(false);

            Intent intent = ComponentHelper.getDeviceAdminAddIntent(this);
            startActivityForResult(intent, Const.RequestCodes.ENABLE_ADMIN);
            mHasDeviceAdminIntent = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        final SharedPreferences sharedPrefs = getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
        final boolean isEnabledPrefs = sharedPrefs.getBoolean(Const.PrefsNames.IS_ENABLED, false);

        if (ComponentHelper.isDeviceAdmin(this)) {
            if (!isEnabledPrefs) {
                mToast = mToast.makeText(this, R.string.toast_service_disabled, Toast.LENGTH_SHORT);
                mToast.show();
            }
        } else {
            if (isEnabledPrefs && !mHasDeviceAdminIntent) {
                mToast = mToast.makeText(this, R.string.toast_running_no_admin, Toast.LENGTH_LONG);
                mToast.show();
            }
        }

        if (CacheHelper.isCacheClearRequired(getApplicationContext())) {
            CacheHelper.clearHistory(getApplicationContext());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Const.RequestCodes.ENABLE_ADMIN) {
            mHasDeviceAdminIntent = false;
            togglePowerConnectionReceiver(resultCode == RESULT_OK);
        }
    }

    @Override
    protected void onDestroy() {
        final SharedPreferences sharedPrefs = getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getExtras() != null) {
            updateNotifyPrefs(intent);
        }

        intent.removeExtra(Const.IntentExtras.RESET_NOTIFY_NUMBER);
        intent.removeExtra(Const.IntentExtras.INCREMENT_NOTIFY_GROUP);
        setIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mToast != null) {
            mToast.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Const.PrefsNames.CACHE_AGE.equals(key)) {
            CacheHelper.clearHistory(getApplicationContext());
        }
    }

    private void togglePowerConnectionReceiver(boolean isDeviceAdmin) {

        final SharedPreferences sharedPrefs = getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
        final boolean isEnabledPrefs = sharedPrefs.getBoolean(Const.PrefsNames.IS_ENABLED, false);

        if (isDeviceAdmin && isEnabledPrefs) {
            ComponentHelper.togglePowerConnectionReceiver(
                    getApplicationContext(),
                    true);
        } else {
            ComponentHelper.togglePowerConnectionReceiver(
                    getApplicationContext(),
                    isEnabledPrefs);
        }
    }

    private void initializePrefsAndListener() {
        PreferenceManager.setDefaultValues(this, Const.APP_PREFS_NAME, Context.MODE_PRIVATE, R.xml.preferences, false);

        // Merge prefs legacy values
        final SharedPreferences legacySp = getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
        if (legacySp.contains(Const.PrefsNames.ON_SCREEN_LOCK) ||
                legacySp.contains(Const.PrefsNames.ON_POWER_LOSS)) {
            final SharedPreferences.Editor editor = legacySp.edit();

            boolean hasChanges = false;
            if (legacySp.contains(Const.PrefsNames.ON_SCREEN_LOCK)) {
                hasChanges = true;
                boolean onScreenLock = legacySp.getBoolean(Const.PrefsNames.ON_SCREEN_LOCK, true);
                editor.remove(Const.PrefsNames.ON_SCREEN_LOCK);

                editor.putString(Const.PrefsNames.SCREEN_LOCK_STATUS,
                        onScreenLock ? Const.PrefsValues.SCREEN_LOCKED : Const.PrefsValues.IGNORE);
            }
            if (legacySp.contains(Const.PrefsNames.ON_POWER_LOSS)) {
                hasChanges = true;
                boolean onPowerLoss = legacySp.getBoolean(Const.PrefsNames.ON_POWER_LOSS, false);
                editor.remove(Const.PrefsNames.ON_POWER_LOSS);

                editor.putString(Const.PrefsNames.POWER_CONNECTION_STATUS,
                        onPowerLoss ? Const.PrefsValues.CONNECTION_OFF : Const.PrefsValues.IGNORE);
            }

            if (hasChanges) {
                // Remove legacy and save new prefs
                editor.apply();
            }
        }

        // Register listener
        legacySp.registerOnSharedPreferenceChangeListener(this);
    }

    private void updateNotifyPrefs(Intent intent) {
        final SharedPreferences sharedPrefs = getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();

        if (intent != null) {
            final boolean hasResetNotifyNumber = intent
                    .getBooleanExtra(Const.IntentExtras.RESET_NOTIFY_NUMBER, false);
            final boolean hasIncrementNotifyGroup = intent
                    .getBooleanExtra(Const.IntentExtras.INCREMENT_NOTIFY_GROUP, false);

            if (hasResetNotifyNumber) {
                sharedPrefsEditor.putInt(Const.PrefsNames.NOTIFY_COUNT, 1);
            }

            if (hasIncrementNotifyGroup) {
                final int notifyGroup = sharedPrefs.getInt(Const.PrefsNames.NOTIFY_GROUP, 1);
                sharedPrefsEditor.putInt(Const.PrefsNames.NOTIFY_GROUP, notifyGroup + 1);
            }

            sharedPrefsEditor.apply();
        }
    }
}

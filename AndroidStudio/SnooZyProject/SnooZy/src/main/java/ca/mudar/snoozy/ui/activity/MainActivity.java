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
    private SharedPreferences mSharedPrefs;
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

        setContentView(R.layout.activity_main);
        setTitle(R.string.activity_main);

        mSharedPrefs = getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);

        setupDeviceAdminIfNecessary();
        setupPreferences();

        // Register listener
        mSharedPrefs.registerOnSharedPreferenceChangeListener(this);

        if (getIntent().getExtras() != null) {
            updateNotificationCount(getIntent());
            getIntent().removeExtra(Const.IntentExtras.RESET_NOTIFY_NUMBER);
            getIntent().removeExtra(Const.IntentExtras.INCREMENT_NOTIFY_GROUP);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_content, new HistoryFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        showMessageIfNecessary();

        if (CacheHelper.isCacheClearRequired(getApplicationContext())) {
            CacheHelper.clearHistory(getApplicationContext());
        }
    }

    @Override
    protected void onDestroy() {
        mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);

        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.getExtras() != null) {
            updateNotificationCount(intent);
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Const.PrefsNames.CACHE_AGE.equals(key)) {
            CacheHelper.clearHistory(getApplicationContext());
        }
    }

    private void setupDeviceAdminIfNecessary() {
        final boolean isFirstLaunch = !mSharedPrefs.contains(Const.PrefsNames.IS_ENABLED);
        final boolean isDeviceAdmin = ComponentHelper.isDeviceAdmin(this);

        if (isFirstLaunch && !isDeviceAdmin) {
            startActivityForResult(ComponentHelper.getDeviceAdminAddIntent(this),
                    Const.RequestCodes.ENABLE_ADMIN);
            mHasDeviceAdminIntent = true;
        }
    }

    private void setupPreferences() {
        PreferenceManager.setDefaultValues(this, Const.APP_PREFS_NAME, Context.MODE_PRIVATE, R.xml.default_preferences, false);

        // Merge prefs legacy values
        if (mSharedPrefs.contains(Const.PrefsNames.ON_SCREEN_LOCK) ||
                mSharedPrefs.contains(Const.PrefsNames.ON_POWER_LOSS)) {
            final SharedPreferences.Editor editor = mSharedPrefs.edit();

            boolean hasChanges = false;
            if (mSharedPrefs.contains(Const.PrefsNames.ON_SCREEN_LOCK)) {
                hasChanges = true;
                boolean onScreenLock = mSharedPrefs.getBoolean(Const.PrefsNames.ON_SCREEN_LOCK, true);
                editor.remove(Const.PrefsNames.ON_SCREEN_LOCK);

                editor.putString(Const.PrefsNames.SCREEN_LOCK_STATUS,
                        onScreenLock ? Const.PrefsValues.SCREEN_LOCKED : Const.PrefsValues.IGNORE);
            }
            if (mSharedPrefs.contains(Const.PrefsNames.ON_POWER_LOSS)) {
                hasChanges = true;
                boolean onPowerLoss = mSharedPrefs.getBoolean(Const.PrefsNames.ON_POWER_LOSS, false);
                editor.remove(Const.PrefsNames.ON_POWER_LOSS);

                editor.putString(Const.PrefsNames.POWER_CONNECTION_STATUS,
                        onPowerLoss ? Const.PrefsValues.CONNECTION_OFF : Const.PrefsValues.IGNORE);
            }

            if (hasChanges) {
                // Remove legacy and save new prefs
                editor.apply();
            }
        }

        final boolean isEnabledPrefs = mSharedPrefs.getBoolean(Const.PrefsNames.IS_ENABLED, false);
        ComponentHelper.togglePowerConnectionReceiver(getApplicationContext(), isEnabledPrefs);
    }

    private void showMessageIfNecessary() {
        final boolean isEnabled = mSharedPrefs.getBoolean(Const.PrefsNames.IS_ENABLED, false);

        if (!isEnabled) {
            mToast = mToast.makeText(this, R.string.toast_service_disabled, Toast.LENGTH_SHORT);
            mToast.show();
        } else if (!mHasDeviceAdminIntent && !ComponentHelper.isDeviceAdmin(this)){
            mToast = mToast.makeText(this, R.string.toast_running_no_admin, Toast.LENGTH_LONG);
            mToast.show();
        }
    }

    private void updateNotificationCount(Intent intent) {
        if (intent != null) {
            final SharedPreferences.Editor prefsEditor = mSharedPrefs.edit();

            final boolean hasResetNotifyNumber = intent
                    .getBooleanExtra(Const.IntentExtras.RESET_NOTIFY_NUMBER, false);
            final boolean hasIncrementNotifyGroup = intent
                    .getBooleanExtra(Const.IntentExtras.INCREMENT_NOTIFY_GROUP, false);

            boolean hasChanges = false;
            if (hasResetNotifyNumber) {
                hasChanges = true;
                prefsEditor.putInt(Const.PrefsNames.NOTIFY_COUNT, 1);
            }

            if (hasIncrementNotifyGroup) {
                hasChanges = true;
                final int notifyGroup = mSharedPrefs.getInt(Const.PrefsNames.NOTIFY_GROUP, 1);
                prefsEditor.putInt(Const.PrefsNames.NOTIFY_GROUP, notifyGroup + 1);
            }

            if (hasChanges) {
                prefsEditor.apply();
            }
        }
    }
}

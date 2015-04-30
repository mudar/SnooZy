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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;

import ca.mudar.snoozy.Const;
import ca.mudar.snoozy.R;
import ca.mudar.snoozy.ui.fragment.SettingsFragment;
import ca.mudar.snoozy.util.ComponentHelper;

import static ca.mudar.snoozy.util.LogUtils.makeLogTag;

public class SettingsActivity extends BaseActivity implements
        CompoundButton.OnCheckedChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = makeLogTag(SettingsActivity.class);

    private SwitchCompat mSwitchPref;
    private SharedPreferences mSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Layout elevation
        ViewCompat.setElevation(findViewById(R.id.master_switch_wrapper),
                getResources().getDimensionPixelSize(R.dimen.headerbar_elevation));

        if (savedInstanceState == null) {
            final SettingsFragment fragment = new SettingsFragment();
            getFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_frame, fragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        mSharedPrefs = getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
        mSharedPrefs.registerOnSharedPreferenceChangeListener(this);

        setupSwitchPreference();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mSharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (Const.PrefsNames.IS_ENABLED.equals(key)) {
            Log.v(TAG, "onSharedPreferenceChanged @ IS_ENABLED");
            final boolean isEnabled = sharedPreferences.getBoolean(Const.PrefsNames.IS_ENABLED, false);
            mSwitchPref.setChecked(isEnabled);
            ComponentHelper.togglePowerConnectionReceiver(getApplicationContext(),
                    isEnabled);
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.v(TAG, "onCheckedChanged "
                + String.format("isChecked = %s", isChecked));
        toggleSwitchText(isChecked);

        mSharedPrefs.edit()
                .putBoolean(Const.PrefsNames.IS_ENABLED, isChecked)
                .apply();

        if (isChecked && !ComponentHelper.isDeviceAdmin(this)) {
            startActivity(ComponentHelper.getDeviceAdminAddIntent(this));
        }
    }

    private void setupSwitchPreference() {
        mSwitchPref = (SwitchCompat) findViewById(R.id.master_switch);

        final boolean isEnabled = mSharedPrefs.getBoolean(Const.PrefsNames.IS_ENABLED, false);
        mSwitchPref.setChecked(isEnabled);
        toggleSwitchText(isEnabled);

        mSwitchPref.setOnCheckedChangeListener(this);
    }

    private void toggleSwitchText(boolean isChecked) {
        mSwitchPref.setText(isChecked ?
                R.string.prefs_is_enabled : R.string.prefs_is_disabled);
    }

}

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

package ca.mudar.snoozy.util;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.provider.Settings;

import java.util.List;

import ca.mudar.snoozy.Const;
import ca.mudar.snoozy.R;
import ca.mudar.snoozy.receiver.ForceLockDeviceAdminReceiver;
import ca.mudar.snoozy.receiver.PowerConnectionReceiver;


public class ComponentHelper {
    private final static String TAG = LogUtils.makeLogTag(ComponentHelper.class);

    public synchronized static void disableDeviceAdmin(Context context) {
        try {
            final DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            final String packageName = context.getPackageName();

            final List<ComponentName> activeAdmins = mDPM.getActiveAdmins();
            for (ComponentName admin : activeAdmins) {
                if (packageName.equals(admin.getPackageName())) {
                    mDPM.removeActiveAdmin(admin);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void togglePowerConnectionReceiver(Context context, boolean enabled) {
        final int newState = (enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED :
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED);

        ComponentName component = new ComponentName(context, PowerConnectionReceiver.class);
        context.getPackageManager()
                .setComponentEnabledSetting(component, newState,
                        PackageManager.DONT_KILL_APP);

        if (newState == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            final SharedPreferences sharedPrefs = context.getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
            final SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
            final int notifyGroup = sharedPrefs.getInt(Const.PrefsNames.NOTIFY_GROUP, 1);

            sharedPrefsEditor.putInt(Const.PrefsNames.NOTIFY_COUNT, 1);
            sharedPrefsEditor.putInt(Const.PrefsNames.NOTIFY_GROUP, notifyGroup + 1);
            sharedPrefsEditor.apply();
        }
    }

    /**
     * Determine if the app is an active Device Admin
     */
    public static boolean isDeviceAdmin(Context context) {
        final DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        final ComponentName componentName = new ComponentName(context, ForceLockDeviceAdminReceiver.class);

        return devicePolicyManager.isAdminActive(componentName);
    }

    public static Intent getDeviceAdminAddIntent(Context context) {
        final ComponentName componentName = new ComponentName(context, ForceLockDeviceAdminReceiver.class);

        // Launch the activity to have the user enable our admin.
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                context.getString(R.string.admin_device_extra_desc_text));

        return intent;
    }

    public static void launchDeviceAdminSettings(Context context) {
        try {
            Intent intentDeviceAdmin = new Intent(Settings.ACTION_SETTINGS);
            intentDeviceAdmin.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intentDeviceAdmin.setClassName(Const.IntentActions.ANDROID_SETTINGS, Const.IntentActions.ANDROID_DEVICE_ADMIN);
            context.startActivity(intentDeviceAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            Intent intentSecuritySettings = new Intent(Settings.ACTION_SECURITY_SETTINGS);
            intentSecuritySettings.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intentSecuritySettings);
        }
    }

}

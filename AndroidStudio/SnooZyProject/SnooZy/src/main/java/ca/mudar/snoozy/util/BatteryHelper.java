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

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;

import ca.mudar.snoozy.Const;


public class BatteryHelper {

    public static float getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed, just in case.
        if (level == -1 || scale == -1) {
            return 0.50f;
        }

        return ((float) level / (float) scale);
    }

    public static boolean isPowerConnected(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent == null) {
            return false;
        }

        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        return ((status == BatteryManager.BATTERY_STATUS_CHARGING) ||
                (status == BatteryManager.BATTERY_STATUS_FULL));
    }

    public static int getPowerConnectionType(Context context) {
        final Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent == null) {
            return -1;
        }

        final int pluggedState = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);

        switch (pluggedState) {
            case BatteryManager.BATTERY_PLUGGED_AC:
            case BatteryManager.BATTERY_PLUGGED_USB:
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                setLastConnectionType(context, pluggedState);
                return pluggedState;
            case 0:
                // Disconnection, return last connection type
                return getLastConnectionType(context);
        }

        return -1;
    }

    private static void setLastConnectionType(Context context, int state) {
        final SharedPreferences.Editor editor = context.getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(Const.PrefsValues.LAST_CONNECTION_TYPE, state);
        editor.apply();
    }

    private static int getLastConnectionType(Context context) {
        final SharedPreferences prefs = context.getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getInt(Const.PrefsValues.LAST_CONNECTION_TYPE, -1);
    }
}


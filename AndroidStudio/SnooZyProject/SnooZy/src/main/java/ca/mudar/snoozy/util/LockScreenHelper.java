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

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;

import ca.mudar.snoozy.Const;

public class LockScreenHelper {

    public static void lockScreen(Context context, String screenLockStatus, String powerConnectionStatus,
                                  String powerConnectionType, boolean isConnectedPower) {

        // Do nothing if ringing
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager.getCallState() == TelephonyManager.CALL_STATE_RINGING) {
            return;
        }

        // Skip if not a device admin
        if (!ComponentHelper.isDeviceAdmin(context.getApplicationContext())) {
            return;
        }

        /**
         * Check powerConnectionType filters
         */
        if (!Const.PrefsValues.IGNORE.equals(powerConnectionType)) {
            final int connectionType = BatteryHelper.getPowerConnectionType(context);
            switch (connectionType) {
                case BatteryManager.BATTERY_PLUGGED_AC:
                    if (Const.PrefsValues.CONNECTION_USB.equals(powerConnectionType)
                            || Const.PrefsValues.CONNECTION_WIRELESS.equals(powerConnectionType)) {
                        return;
                    }
                    break;
                case BatteryManager.BATTERY_PLUGGED_USB:
                    if (Const.PrefsValues.CONNECTION_AC.equals(powerConnectionType)
                            || Const.PrefsValues.CONNECTION_WIRELESS.equals(powerConnectionType)) {
                        return;
                    }
                    break;
                case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                    if (Const.PrefsValues.CONNECTION_AC.equals(powerConnectionType)
                            || Const.PrefsValues.CONNECTION_USB.equals(powerConnectionType)) {
                        return;
                    }
                    break;
                default:
                    break;
            }
        }

        /**
         * Check powerConnectionStatus filters
         */
        if (!Const.PrefsValues.IGNORE.equals(powerConnectionStatus)) {
            if (isConnectedPower && Const.PrefsValues.CONNECTION_OFF.equals(powerConnectionStatus)) {
                return;
            }
            if (!isConnectedPower && Const.PrefsValues.CONNECTION_ON.equals(powerConnectionStatus)) {
                return;
            }
        }

        /**
         * Check screenLockStatus filters
         */
        if (!Const.PrefsValues.IGNORE.equals(screenLockStatus)) {
            final boolean isLocked = ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE))
                    .inKeyguardRestrictedInputMode();

            if (isLocked && Const.PrefsValues.SCREEN_UNLOCKED.equals(screenLockStatus)) {
                return;
            }
            if (!isLocked && Const.PrefsValues.SCREEN_LOCKED.equals(screenLockStatus)) {
                return;
            }
        }

        /**
         * Didn't skip on filters: lock screen now
         */
        final DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        mDPM.lockNow();
    }
}

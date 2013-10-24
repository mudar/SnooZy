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

public class LockScreenHelper {
    public  static void lockScreen(Context context, boolean onScreenLock, boolean onPowerLoss, boolean isConnectedPower) {

        if (!ComponentHelper.isDeviceAdmin(context.getApplicationContext())) {
            return;
        }

        final boolean isLocked = ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE))
                .inKeyguardRestrictedInputMode();

        if (onScreenLock && onPowerLoss) {
            if (isLocked && !isConnectedPower) {
                DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDPM.lockNow();
            }
        } else if (onScreenLock) {
            if (isLocked) {
                DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDPM.lockNow();
            }

        } else if (onPowerLoss) {
            if (!isConnectedPower) {
                DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDPM.lockNow();
            }
        } else {
            DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            mDPM.lockNow();
        }
    }
}

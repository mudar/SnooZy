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

package ca.mudar.snoozy.receiver;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

import ca.mudar.snoozy.Const;
import ca.mudar.snoozy.util.ComponentHelper;

/**
 * force-lock policy is defined in res/xml/device_admin.xml
 */
public class ForceLockDeviceAdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onDisabled(Context context, Intent intent) {
        try {
            // Update preferences
            context.getApplicationContext()
                    .getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(Const.PrefsNames.IS_ENABLED, false)
                    .apply();

            // Disable receiver
            ComponentHelper.togglePowerConnectionReceiver(
                    context.getApplicationContext(),
                    false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDisabled(context, intent);
    }

}

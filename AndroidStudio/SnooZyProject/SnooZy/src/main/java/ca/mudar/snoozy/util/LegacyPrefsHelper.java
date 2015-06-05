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

import android.content.SharedPreferences;
import android.media.RingtoneManager;

import ca.mudar.snoozy.Const;

public class LegacyPrefsHelper {

    private static final String HAS_SOUND = "prefs_has_sound";
    private static final String ON_POWER_LOSS = "prefs_on_power_loss";
    private static final String ON_SCREEN_LOCK = "prefs_on_screen_lock";

    /**
     * Merge legacy preferences. On UI update, 3 CheckBoxPreference where replaced
     * by ListPreference. We set default value to the equivalent on/off value.
     *
     * @param prefs
     * @return true if merged any legacy preferences
     */
    public static boolean mergeLegacyPrefs(SharedPreferences prefs) {
        if (prefs.contains(ON_SCREEN_LOCK)
                || prefs.contains(ON_POWER_LOSS)
                || prefs.contains(HAS_SOUND)) {
            final SharedPreferences.Editor editor = prefs.edit();

            boolean hasChanges = false;
            if (prefs.contains(ON_SCREEN_LOCK)) {
                hasChanges = true;
                final boolean onScreenLock = prefs.getBoolean(ON_SCREEN_LOCK, true);
                editor.remove(ON_SCREEN_LOCK);

                editor.putString(Const.PrefsNames.SCREEN_LOCK_STATUS,
                        onScreenLock ? Const.PrefsValues.SCREEN_LOCKED : Const.PrefsValues.IGNORE);
            }
            if (prefs.contains(ON_POWER_LOSS)) {
                hasChanges = true;
                final boolean onPowerLoss = prefs.getBoolean(ON_POWER_LOSS, false);
                editor.remove(ON_POWER_LOSS);

                editor.putString(Const.PrefsNames.POWER_CONNECTION_STATUS,
                        onPowerLoss ? Const.PrefsValues.CONNECTION_OFF : Const.PrefsValues.IGNORE);
            }
            if (prefs.contains(HAS_SOUND)) {
                hasChanges = true;
                final boolean hasSound = prefs.getBoolean(HAS_SOUND, false);
                editor.remove(HAS_SOUND);

                String ringtone = Const.PrefsValues.RINGTONE_SILENT;
                if (hasSound) {
                    ringtone = RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                            .toString();
                }
                editor.putString(Const.PrefsNames.RINGTONE, ringtone);
            }

            if (hasChanges) {
                // Remove legacy and save new prefs
                editor.apply();
            }

            return hasChanges;
        }

        return false;
    }
}

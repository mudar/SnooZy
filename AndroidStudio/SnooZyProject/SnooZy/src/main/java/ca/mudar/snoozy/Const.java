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

package ca.mudar.snoozy;


import android.text.format.DateUtils;

public class Const {

    public static final String APP_PREFS_NAME = "snoozy_prefs";
    public static final String DATABASE_NAME = "snoozy.db";
    public static final int DATABASE_VERSION = 1;
    public static final long VIBRATION_DURATION = 300;
    public static final int NOTIFY_ID = 0x1;
    public static final String URL_PLAYSTORE = "http://play.google.com/store/apps/details?id=ca.mudar.snoozy";

    // Preferences
    public interface PrefsNames {
        String IS_ENABLED = "prefs_is_enabled";
        String HAS_NOTIFICATIONS = "prefs_has_notifications";
        String HAS_VIBRATION = "prefs_has_vibration";
        String HAS_SOUND = "prefs_has_sound";
        String NOTIFY_COUNT = "prefs_notify_count";
        String NOTIFY_GROUP = "prefs_notify_group";
        String ON_SCREEN_LOCK = "prefs_on_screen_lock";
        String ON_POWER_LOSS = "prefs_on_power_loss";
        String DELAY_TO_LOCK = "prefs_delay_to_lock";
        String DEVICE_ADMIN = "prefs_device_admin";
//        String IS_BETA_USER = "prefs_is_beta_user";
        String CACHE_AGE = "prefs_cache_age";
        String LAST_CACHE_CLEAR = "prefs_last_cache_clear";
    }

    public interface PrefsValues {
        String DELAY_FAST = "0";
        String DELAY_MODERATE = "3";
        String DELAY_SLOW = "5";
        String CACHE_NONE = "0";
        String CACHE_SMALL = "7";
        String CACHE_MEDIUM = "14";
        String CACHE_LARGE = "30";
        String CACHE_ALL = "-1";  // Keep all history
    }

    public interface CacheAgeValues {
        long SMALL = DateUtils.WEEK_IN_MILLIS;
        long MEDIUM = 2l * DateUtils.WEEK_IN_MILLIS;
        long LARGE = 30l * DateUtils.DAY_IN_MILLIS;
    }


    // Intents
    public interface IntentExtras {
        String RESET_NOTIFY_NUMBER = "reset_notify_number";
        String INCREMENT_NOTIFY_GROUP = "inc_notify_group";
        String ON_SCREEN_LOCK = "on_screen_lock";
        String ON_POWER_LOSS = "on_power_loss";
        String IS_CONNECTED = "is_connected";
        String DELAY_TO_LOCK = "delay_to_lock";
    }

    // Actions
    public interface IntentActions {
        String NOTIFY_DELETE = "ca.mudar.snoozy.notify_deleted";
        String ANDROID_SETTINGS = "com.android.settings";
        String ANDROID_DEVICE_ADMIN = "com.android.settings.DeviceAdminSettings";
    }

    // Request Codes
    public interface RequestCodes {
        int ENABLE_ADMIN = 0x1;
        int RESET_NOTIFY_NUMBER = 0x2;
    }

    // Assets
    public interface LocalAssets {
        String LICENSE = "gpl-3.0-standalone.html";
    }

}

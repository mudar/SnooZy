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
    public static interface PrefsNames {
        final String IS_ENABLED = "prefs_is_enabled";
        final String HAS_NOTIFICATIONS = "prefs_has_notifications";
        final String HAS_VIBRATION = "prefs_has_vibration";
        final String HAS_SOUND = "prefs_has_sound";
        final String NOTIFY_COUNT = "prefs_notify_count";
        final String NOTIFY_GROUP = "prefs_notify_group";
        final String ON_SCREEN_LOCK = "prefs_on_screen_lock";
        final String ON_POWER_LOSS = "prefs_on_power_loss";
        final String DELAY_TO_LOCK = "prefs_delay_to_lock";
        final String DEVICE_ADMIN = "prefs_device_admin";
        final String IS_BETA_USER = "prefs_is_beta_user";
        final String CACHE_AGE = "prefs_cache_age";
        final String LAST_CACHE_CLEAR = "prefs_last_cache_clear";
    }

    public static interface PrefsValues {
        final String DELAY_FAST = "0";
        final String DELAY_MODERATE = "3";
        final String DELAY_SLOW = "5";
        final String CACHE_SMALL = "7";
        final String CACHE_MEDIUM = "14";
        final String CACHE_LARGE = "30";
        final String CACHE_ALL = "-1";  // Keep all history
    }

    public static interface CacheAgeValues {
        final long SMALL = DateUtils.WEEK_IN_MILLIS;
        final long MEDIUM = 2l * DateUtils.WEEK_IN_MILLIS;
        final long LARGE = 30l * DateUtils.DAY_IN_MILLIS;
        final long CACHE_ALL = -1l;
    }


    // Intents
    public static interface IntentExtras {
        final String RESET_NOTIFY_NUMBER = "reset_notify_number";
        final String INCREMENT_NOTIFY_GROUP = "inc_notify_group";
        final String ON_SCREEN_LOCK = "on_screen_lock";
        final String ON_POWER_LOSS = "on_power_loss";
        final String IS_CONNECTED = "is_connected";
        final String DELAY_TO_LOCK = "delay_to_lock";
    }

    // Actions
    public static interface IntentActions {
        final String NOTIFY_DELETE = "ca.mudar.snoozy.notify_deleted";
        final String ANDROID_SETTINGS = "com.android.settings";
        final String ANDROID_DEVICE_ADMIN = "com.android.settings.DeviceAdminSettings";
    }

    // Request Codes
    public static interface RequestCodes {
        final int ENABLE_ADMIN = 0x1;
        final int RESET_NOTIFY_NUMBER = 0x2;
    }

    // Assets
    public static interface LocalAssets {
        final String LICENSE = "gpl-3.0-standalone.html";
    }

}

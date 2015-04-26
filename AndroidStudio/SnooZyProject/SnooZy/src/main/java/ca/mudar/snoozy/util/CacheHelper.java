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
import android.content.SharedPreferences;
import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ca.mudar.snoozy.Const;
import ca.mudar.snoozy.provider.ChargerContract;

import static ca.mudar.snoozy.util.LogUtils.makeLogTag;

public class CacheHelper {
    private static final String TAG = makeLogTag(CacheHelper.class);

    public static void clearHistory(Context context) {
        final SharedPreferences sharedPrefs = context.getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);

        final String cacheAge = sharedPrefs.getString(Const.PrefsNames.CACHE_AGE, Const.PrefsValues.CACHE_ALL);

        final Calendar midnight = new GregorianCalendar();
        // Reset hour, minutes, seconds and millis
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);

        final long midnightInMillis = midnight.getTimeInMillis();

        // Do the database cleanup
        if (!Const.PrefsValues.CACHE_ALL.equals(cacheAge)) {
            if (Const.PrefsValues.CACHE_NONE.equals(cacheAge)) {
                // No filter, delete all cache content
                context.getContentResolver().delete(
                        ChargerContract.History.CONTENT_URI,
                        null,
                        null
                );
            } else {
                // Delete older entries
                final long clearTime = midnightInMillis - getCacheAgeInMillis(cacheAge);
                context.getContentResolver().delete(
                        ChargerContract.History.CONTENT_URI,
                        ChargerContract.History.TIME_STAMP + " < ? ",
                        new String[] {String.valueOf(clearTime)}
                );
            }
        }

        // Set lastClear timestamp to midnight.
        // This allows one cleanup per day instead of every 24 hours.
        final SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putLong(Const.PrefsNames.LAST_CACHE_CLEAR, midnightInMillis);
        editor.apply();
    }

    public static boolean isCacheClearRequired(Context context) {
        final SharedPreferences sharedPrefs = context.getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);

        final String cacheAge = sharedPrefs.getString(Const.PrefsNames.CACHE_AGE, Const.PrefsValues.CACHE_ALL);
        if (Const.PrefsValues.CACHE_ALL.equals(cacheAge)) {
            return false;
        }

        final long lastCleared = sharedPrefs.getLong(Const.PrefsNames.LAST_CACHE_CLEAR, -1l);
        if (lastCleared == -1l) {
            // History has never been cleared, probably first run after install or update.
            return true;
        }
        final long now = System.currentTimeMillis();

        // Do the check once per day
        return (lastCleared + DateUtils.DAY_IN_MILLIS < now);
    }

    public static long getCacheAgeInMillis(String age) {
        if (age.equals(Const.PrefsValues.CACHE_SMALL)) {
            return Const.CacheAgeValues.SMALL;
        } else if (age.equals(Const.PrefsValues.CACHE_MEDIUM)) {
            return Const.CacheAgeValues.MEDIUM;
        } else if (age.equals(Const.PrefsValues.CACHE_LARGE)) {
            return Const.CacheAgeValues.LARGE;
        }

        return 0;
    }
}

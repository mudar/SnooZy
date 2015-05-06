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

package ca.mudar.snoozy.provider;

import android.net.Uri;
import android.provider.BaseColumns;

import static ca.mudar.snoozy.util.LogUtils.makeLogTag;

public class ChargerContract {
    public static final String CONTENT_AUTHORITY = "ca.mudar.snoozy.provider";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String TAG = makeLogTag(ChargerContract.class);
    private static final String PATH_HISTORY = "history";
    private static final String PATH_DAILY_HISTORY = "daily_history";
    private static final String PATH_DAY = "day";

    private ChargerContract() {
    }

    interface HistoryColumns {
        String IS_POWER_ON = "h_is_power_on";
        String BATTERY_LEVEL = "h_battery_level";
        String NOTIFY_GROUP = "h_notify_group";
        String TIME_STAMP = "h_time_stamp";
        String ORDINAL_DAY = "h_julian_day"; // Renamed in Java but not for sqlite
    }

    interface DailyHistoryColumns {
        String JULIAN_DAY = "d_julian_day";
        String MIN = "d_min";
        String MAX = "d_max";
        String TOTAL = "d_total";
    }

    public static class History implements HistoryColumns, BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORY).build();
        public static final Uri CONTENT_URI_PER_DAY =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_HISTORY).appendPath(PATH_DAY).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.snoozy.history";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.snoozy.history";
        public static final String DEFAULT_SORT = HistoryColumns.TIME_STAMP + " DESC ";

        public static final String IS_FIRST = "is_first";
        public static final String IS_LAST = "is_last";

        public static Uri buildHistoryUri(String historyId) {
            return CONTENT_URI.buildUpon().appendPath(historyId).build();
        }

        public static String getHistoryId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static Uri buildHistoryPerDayUri(String historyDay) {
            return CONTENT_URI.buildUpon().appendPath(PATH_DAY).appendPath(historyDay).build();
        }

        public static String getHistoryPerDayDay(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }

    public static class DailyHistory implements DailyHistoryColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DAILY_HISTORY).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.snoozy.dailyhistory";
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/vnd.snoozy.dailyhistory";
        public static final String DEFAULT_SORT = DailyHistoryColumns.JULIAN_DAY + " DESC ";

        public static Uri buildDailyHistoryUri(String dailyHistoryDay) {
            return CONTENT_URI.buildUpon().appendPath(dailyHistoryDay).build();
        }

        public static String getDailyHistoryDay(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

}

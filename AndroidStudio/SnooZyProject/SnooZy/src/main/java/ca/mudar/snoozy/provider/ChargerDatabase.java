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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import ca.mudar.snoozy.Const;

import static ca.mudar.snoozy.provider.ChargerContract.DailyHistoryColumns;
import static ca.mudar.snoozy.provider.ChargerContract.HistoryColumns;
import static ca.mudar.snoozy.util.LogUtils.LOGD;
import static ca.mudar.snoozy.util.LogUtils.makeLogTag;


public class ChargerDatabase extends SQLiteOpenHelper {
    private static final String TAG = makeLogTag(ChargerDatabase.class);
    private final Context mContext;

    public ChargerDatabase(Context context) {
        super(context, Const.DATABASE_NAME, null, Const.DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.HISTORY + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                HistoryColumns.IS_POWER_ON + " INTEGER NOT NULL," +
                HistoryColumns.BATTERY_LEVEL + " INTEGER NOT NULL," +
                HistoryColumns.NOTIFY_GROUP + " INTEGER NOT NULL," +
                HistoryColumns.TIME_STAMP + " INTEGER NOT NULL," +
                HistoryColumns.JULIAN_DAY + " DATE NOT NULL )"
        );

        db.execSQL("CREATE INDEX " + Indexes.HISTORY_DAY +
                " ON " + Tables.HISTORY +
                " (" + HistoryColumns.JULIAN_DAY + ")"
        );

        db.execSQL("CREATE VIEW " + Tables.DAILY_HISTORY +
                " AS SELECT " + HistoryColumns.JULIAN_DAY + " AS " + DailyHistoryColumns.JULIAN_DAY + "," +
                " MIN(" + HistoryColumns.TIME_STAMP + ") AS " + DailyHistoryColumns.MIN + "," +
                " MAX(" + HistoryColumns.TIME_STAMP + ") AS " + DailyHistoryColumns.MAX + "," +
                " COUNT(" + BaseColumns._ID + ") AS " + DailyHistoryColumns.TOTAL +
                " FROM " + Tables.HISTORY +
                " GROUP BY " + HistoryColumns.JULIAN_DAY
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        LOGD(TAG, "onUpgrade() from " + oldVersion + " to " + newVersion);
    }

    interface Tables {
        String HISTORY = "history";
        String DAILY_HISTORY = "daily_history";
        String HISTORY_JOIN_DAILY = HISTORY + " INNER JOIN " + DAILY_HISTORY +
                " ON " + HistoryColumns.JULIAN_DAY + " = " + DailyHistoryColumns.JULIAN_DAY;
    }

    private interface Indexes {
        final String HISTORY_DAY = "h_day_index";
    }

}
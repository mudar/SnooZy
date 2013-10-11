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

package ca.mudar.snoozy.ui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import ca.mudar.snoozy.R;

import static ca.mudar.snoozy.ui.fragment.HistoryFragment.HistoryQuery;
import static ca.mudar.snoozy.util.LogUtils.makeLogTag;


public class HistoryCursorAdapter extends SimpleCursorAdapter {
    private static final String TAG = makeLogTag(HistoryCursorAdapter.class);
    private final DateFormat mTimeFormat;
    private final Resources mResources;
    private long mMillis;

    public HistoryCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);

        this.mTimeFormat = android.text.format.DateFormat.getTimeFormat(context);
        this.mResources = context.getResources();
        this.mMillis = System.currentTimeMillis();
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        /*
         Get current cursor values
          */
        final boolean isPowerOn = (1 == cursor.getInt(HistoryQuery.IS_POWER_ON));
        final long timestamp = cursor.getLong(HistoryQuery.TIME_STAMP);
        final boolean isFirst = (1 == cursor.getInt(HistoryQuery.IS_FIRST));
        final boolean isLast = (1 == cursor.getInt(HistoryQuery.IS_LAST));
        final int batteryLevel = cursor.getInt(HistoryQuery.BATTERY_LEVEL);

        /*
         Prepare the data
          */
        final String sPowerStatus = mResources.getString(isPowerOn ?
                R.string.history_item_power_connected : R.string.history_item_power_disconnected);
        final String sBatteryLevel = String.format(mResources.getString(R.string.history_item_battery_level), batteryLevel);
        final int resPowerStatusColor = mResources.getColor(isPowerOn ?
                R.color.card_row_highlight_color : R.color.card_row_color);

        String sTimestamp;
        String sDay;


        if (DateUtils.isToday(timestamp)) {
            sDay = mResources.getString(R.string.history_item_day_today);
            sTimestamp = (String) DateUtils.formatSameDayTime(timestamp, mMillis, 0, DateFormat.SHORT);
//            sTimestamp = (String) DateUtils.getRelativeTimeSpanString(timestamp,now, 0, 0);
        } else {
            sDay = (String) DateUtils.getRelativeTimeSpanString(timestamp, mMillis, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
//            sDay = sDay.substring(0,1).toUpperCase() + sDay.substring(1);
            sTimestamp = mTimeFormat.format(new Date(timestamp));
        }
        sDay = sDay.substring(0, 1).toUpperCase() + sDay.substring(1);

        /*
         Set UI values
         */
        ((TextView) view.findViewById(R.id.history_is_power_on)).setText(sPowerStatus);
        ((TextView) view.findViewById(R.id.history_is_power_on)).setTextColor(resPowerStatusColor);
        ((TextView) view.findViewById(R.id.history_timestamp)).setText(sTimestamp);
        ((TextView) view.findViewById(R.id.history_battery_level)).setText(sBatteryLevel);

        if (isFirst && isLast) {
            ((TextView) view.findViewById(R.id.history_day)).setText(sDay);

            view.findViewById(R.id.history_header).setVisibility(View.VISIBLE);

            view.setBackgroundResource(R.drawable.bg_cards_top_bottom);
        } else if (isLast) {
            ((TextView) view.findViewById(R.id.history_day)).setText(sDay);

            view.findViewById(R.id.history_header).setVisibility(View.VISIBLE);

            view.setBackgroundResource(R.drawable.bg_cards_top);
        } else if (isFirst) {
            view.findViewById(R.id.history_header).setVisibility(View.GONE);

            view.setBackgroundResource(R.drawable.bg_cards_bottom);
        } else {
            view.findViewById(R.id.history_header).setVisibility(View.GONE);

            view.setBackgroundResource(R.drawable.bg_cards_middle);
        }
    }
}

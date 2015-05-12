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

package ca.mudar.snoozy.ui.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import ca.mudar.snoozy.R;
import ca.mudar.snoozy.ui.fragment.HistoryFragment;
import ca.mudar.snoozy.util.LogUtils;

// https://gist.github.com/gabrielemariotti/4c189fb1124df4556058

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private static final String TAG = LogUtils.makeLogTag(HistoryAdapter.class);

    private final Context context;
    private final int layout;
    private final Resources resources;
    private final DateFormat mTimeFormat;
    private Cursor mCursor;
    private long mMillis;
    private int mLastPosition = -1;

    public HistoryAdapter(Context context, int layout, Cursor mCursor) {
        this.context = context;
        this.layout = layout;
        this.mCursor = mCursor;

        this.resources = context.getResources();
        this.mTimeFormat = android.text.format.DateFormat.getTimeFormat(context);
        this.mMillis = System.currentTimeMillis();
    }


    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        if (mCursor == null || !mCursor.moveToPosition(position)) {
            return;
        }

        /**
         * Get current values
         */
        final int total = mCursor.getInt(HistoryFragment.HistoryQuery.TOTAL);
        final boolean isPowerOn = (1 == mCursor.getInt(HistoryFragment.HistoryQuery.IS_POWER_ON));
        final long timestamp = mCursor.getLong(HistoryFragment.HistoryQuery.TIME_STAMP);
        final boolean isFirst = (1 == mCursor.getInt(HistoryFragment.HistoryQuery.IS_FIRST));
        final boolean isLast = (1 == mCursor.getInt(HistoryFragment.HistoryQuery.IS_LAST));
        final int batteryLevel = mCursor.getInt(HistoryFragment.HistoryQuery.BATTERY_LEVEL);

        /**
         * Prepare the data
         */
        final String sPowerStatus = resources.getString(isPowerOn ?
                R.string.history_item_power_connected : R.string.history_item_power_disconnected);
        final String sBatteryLevel = String.format(resources.getString(R.string.history_item_battery_level), batteryLevel);
        final int resPowerStatusColor = resources.getColor(isPowerOn ?
                R.color.card_row_highlight_color : R.color.card_row_color);

        String sTimestamp;
        String sDay;

//        Log.v(TAG, position + ": sPowerStatus = " + sPowerStatus);

        if (DateUtils.isToday(timestamp)) {
            sDay = resources.getString(R.string.history_item_day_today);
            sTimestamp = (String) DateUtils.formatSameDayTime(timestamp, mMillis, 0, DateFormat.SHORT);
//            sTimestamp = (String) DateUtils.getRelativeTimeSpanString(timestamp,now, 0, 0);
        } else {
            sDay = (String) DateUtils.getRelativeTimeSpanString(timestamp, mMillis, DateUtils.DAY_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE);
//            sDay = sDay.substring(0,1).toUpperCase() + sDay.substring(1);
            sTimestamp = mTimeFormat.format(new Date(timestamp));
        }
        sDay = sDay.substring(0, 1).toUpperCase() + sDay.substring(1);

        /**
         * Fill data
         */
        holder.isPowerOn.setText(sPowerStatus);
        holder.isPowerOn.setTextColor(resPowerStatusColor);
        holder.timestamp.setText(sTimestamp);
        holder.batteryLevel.setText(sBatteryLevel);

        if (isFirst && isLast) {
            holder.headerDay.setText(sDay);
            holder.headerTotal.setText(String.valueOf(total));
            holder.header.setVisibility(View.VISIBLE);
//            holder.container.setBackgroundResource(R.drawable.bg_cards_top_bottom);
        } else if (isLast) {
            holder.headerDay.setText(sDay);
            holder.headerTotal.setText(String.valueOf(total));
            holder.header.setVisibility(View.VISIBLE);
//            holder.container.setBackgroundResource(R.drawable.bg_cards_top);
        } else if (isFirst) {
            holder.header.setVisibility(View.GONE);
//            holder.container.setBackgroundResource(R.drawable.bg_cards_bottom);
        } else {
            holder.header.setVisibility(View.GONE);
//            holder.container.setBackgroundResource(R.drawable.bg_cards_middle);
        }

//        setAnimation(holder.container, position);
    }

    /**
     * Animate items on display
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            mLastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return (mCursor == null) ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {

        private View container;
        private View header;
        private TextView headerDay;
        private TextView headerTotal;
        private TextView timestamp;
        private TextView isPowerOn;
        private TextView batteryLevel;

        public HistoryViewHolder(View itemView) {
            super(itemView);

            this.container = itemView;
            this.header = itemView.findViewById(R.id.history_header);
            this.headerDay = (TextView) itemView.findViewById(R.id.history_day);
            this.headerTotal = (TextView) itemView.findViewById(R.id.history_total);
            this.timestamp = (TextView) itemView.findViewById(R.id.history_timestamp);
            this.isPowerOn = (TextView) itemView.findViewById(R.id.history_is_power_on);
            this.batteryLevel = (TextView) itemView.findViewById(R.id.history_battery_level);
        }


    }
}

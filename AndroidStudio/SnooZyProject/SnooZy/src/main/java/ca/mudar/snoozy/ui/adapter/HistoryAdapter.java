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
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.tonicartos.superslim.LayoutManager;
import com.tonicartos.superslim.LinearSLM;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.mudar.snoozy.Const;
import ca.mudar.snoozy.R;
import ca.mudar.snoozy.model.HistorySection;
import ca.mudar.snoozy.model.Queries;
import ca.mudar.snoozy.model.SectionsArray;
import ca.mudar.snoozy.util.LogUtils;

// https://gist.github.com/gabrielemariotti/4c189fb1124df4556058

public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = LogUtils.makeLogTag(HistoryAdapter.class);

    private static final int VIEW_TYPE_HEADER = 0x01;
    private static final int VIEW_TYPE_CONTENT = 0x00;

    private final Context context;
    private final int layout;
    private final Resources resources;
    private final SimpleDateFormat dateFormat;
    private final DateFormat timeFormat;
    private final int headerElevation;
    private final int vPadding;
    private Cursor mCursor;
    private SectionsArray mSections;
    private long mMillis;
    private int mLastPosition = -1;

    public HistoryAdapter(Context context, int layout, Cursor mCursor) {
        this.context = context;
        this.layout = layout;
        this.mCursor = mCursor;

        this.resources = context.getResources();
        this.timeFormat = android.text.format.DateFormat.getTimeFormat(context);
        this.headerElevation = resources.getDimensionPixelSize(R.dimen.elevation_high);
        this.vPadding = resources.getDimensionPixelSize(R.dimen.card_vertical_padding);
        this.mMillis = System.currentTimeMillis();

        this.mSections = new SectionsArray();
        this.dateFormat = new SimpleDateFormat(Const.FORMAT_ORDINAL_DAY);
    }

    @Override
    public int getItemViewType(int position) {
        return mSections.isHeader(position) ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_header, parent, false);

            return new HeaderViewHolder(v);
        } else {

            final View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

            return new HistoryViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final View itemView = holder.itemView;

        final LayoutManager.LayoutParams params = LayoutManager.LayoutParams.from(itemView.getLayoutParams());


        if (getItemViewType(position) == VIEW_TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.bindRow(position);

            ViewCompat.setElevation(itemView, headerElevation);

            setAnimation(holder.itemView, position);
        } else {
            if (mCursor == null || !mCursor.moveToPosition(mSections.getRawPosition(position))) {
                return;
            }

            HistoryViewHolder historyHolder = (HistoryViewHolder) holder;
            historyHolder.bindRow();

//            final int julianDay = mCursor.getInt(Queries.HistoryDetailsQuery.JULIAN_DAY);
//            final int header = mCursor.getInt(Queries.HistoryDetailsQuery.TOTAL);

        }

        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        params.headerDisplay = LayoutManager.LayoutParams.HEADER_STICKY;

        params.setSlm(LinearSLM.ID);
        params.setFirstPosition(mSections.getHeaderPosition(position));

        itemView.setLayoutParams(params);
    }

    /**
     * Animate items on display
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > mLastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            mLastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return (mCursor == null ? 0 : mCursor.getCount())
                + (mSections == null ? 0 : mSections.size());
    }

    public void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public void setHeaders(SectionsArray headers) {
        Log.v(TAG, "setHeaders "
                + String.format("headers = %s", headers.size()));

        this.mSections = headers;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView vHeaderDay;
        private TextView vHeaderTotal;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            vHeaderDay = (TextView) itemView.findViewById(R.id.history_day);
            vHeaderTotal = (TextView) itemView.findViewById(R.id.history_total);
        }

        private void bindRow(int position) {
            final HistorySection section = mSections.get(position);
            if (section == null) {
                return;
            }

            // Set total
            final int total = section.getTotal();
            vHeaderTotal.setText(String.valueOf(total));
            vHeaderTotal.setVisibility(total >= Const.TOTAL_THRESHOLD ? View.VISIBLE : View.INVISIBLE);

            // Set Day
            try {
                String sDay;
                Date date = dateFormat.parse(String.valueOf(section.getOrdinalDay()));
                final long timestamp = date.getTime();

                if (DateUtils.isToday(timestamp)) {
                    sDay = resources.getString(R.string.history_item_day_today);
                } else if (DateUtils.isToday(timestamp + DateUtils.DAY_IN_MILLIS)) {
                    sDay = resources.getString(R.string.history_item_day_yesterday);
                } else {
                    sDay = (String) DateUtils.getRelativeTimeSpanString(context, timestamp, false);
                }
                sDay = sDay.substring(0, 1).toUpperCase() + sDay.substring(1);

                vHeaderDay.setText(sDay);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public class HistoryViewHolder extends RecyclerView.ViewHolder {

        private TextView vTimestamp;
        private TextView vIsPowerOn;
        private TextView vBatteryLevel;

        public HistoryViewHolder(View itemView) {
            super(itemView);

            vTimestamp = (TextView) itemView.findViewById(R.id.history_timestamp);
            vIsPowerOn = (TextView) itemView.findViewById(R.id.history_is_power_on);
            vBatteryLevel = (TextView) itemView.findViewById(R.id.history_battery_level);
        }

        private void bindRow() {

            /**
             * Get current values
             */
            final boolean isPowerOn = (1 == mCursor.getInt(Queries.HistoryDetailsQuery.IS_POWER_ON));
            final long timestamp = mCursor.getLong(Queries.HistoryDetailsQuery.TIME_STAMP);
            final boolean isFirst = (1 == mCursor.getInt(Queries.HistoryDetailsQuery.IS_FIRST));
            final boolean isLast = (1 == mCursor.getInt(Queries.HistoryDetailsQuery.IS_LAST));
            final int batteryLevel = mCursor.getInt(Queries.HistoryDetailsQuery.BATTERY_LEVEL);

            /**
             * Prepare the data
             */
            final String sPowerStatus = resources.getString(isPowerOn ?
                    R.string.history_item_power_connected : R.string.history_item_power_disconnected);
            final String sBatteryLevel = String.format(resources.getString(R.string.history_item_battery_level), batteryLevel);
            final int resPowerStatusColor = resources.getColor(isPowerOn ?
                    R.color.card_row_highlight_color : R.color.card_row_color);

            String sTimestamp;

            if (DateUtils.isToday(timestamp)) {
                sTimestamp = (String) DateUtils.formatSameDayTime(timestamp, mMillis, 0, DateFormat.SHORT);
//            sTimestamp = (String) DateUtils.getRelativeTimeSpanString(vTimestamp,now, 0, 0);
            } else {
                sTimestamp = timeFormat.format(new Date(timestamp));
            }

            itemView.setPadding(0,
                    isLast ? vPadding : 0, // Last history item is at top
                    0,
                    isFirst ? vPadding : 0 // Fist history item is at bottom
            );

            /**
             * Fill data
             */
            vIsPowerOn.setText(sPowerStatus);
            vIsPowerOn.setTextColor(resPowerStatusColor);
            vTimestamp.setText(sTimestamp);
            vBatteryLevel.setText(sBatteryLevel);
        }

    }
}

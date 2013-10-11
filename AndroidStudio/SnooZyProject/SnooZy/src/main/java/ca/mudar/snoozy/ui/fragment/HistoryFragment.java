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

package ca.mudar.snoozy.ui.fragment;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import ca.mudar.snoozy.R;
import ca.mudar.snoozy.ui.widget.HistoryCursorAdapter;
import ca.mudar.snoozy.util.BatteryHelper;

import static ca.mudar.snoozy.provider.ChargerContract.DailyHistory;
import static ca.mudar.snoozy.provider.ChargerContract.History;
import static ca.mudar.snoozy.util.LogUtils.makeLogTag;

public class HistoryFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = makeLogTag(HistoryFragment.class);
    protected Cursor mCursor = null;
    protected HistoryCursorAdapter mAdapter;
    private View mRootView;
    private View mHeaderView;
    private View mFooterView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mRootView = inflater.inflate(R.layout.fragment_list_history, null);
        mHeaderView = inflater.inflate(R.layout.fragment_list_history_header, null);
        mFooterView = inflater.inflate(R.layout.fragment_list_history_footer, null);

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mHeaderView != null) {
            try {
                getListView().addHeaderView(mHeaderView, null, false);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        if (mFooterView != null) {
            try {
                getListView().addFooterView(mFooterView, null, false);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

        setListAdapter(null);

        mAdapter = new HistoryCursorAdapter(getActivity(),
                R.layout.fragment_list_item_history,
                mCursor,
                new String[]{
                        History.IS_POWER_ON,
                        History.BATTERY_LEVEL,
                        History.TIME_STAMP,
                        DailyHistory.TOTAL
                },
                new int[]{
                        R.id.history_is_power_on,
                        R.id.history_battery_level,
                        R.id.history_timestamp,
                        R.id.history_total
                },
                0);

        setListAdapter(mAdapter);

        getLoaderManager().initLoader(HistoryQuery._TOKEN, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity().getBaseContext(),
                History.CONTENT_URI_PER_DAY,
                HistoryQuery.HISTORY_SUMMARY_PROJECTION,
                null,
                null,
                History.DEFAULT_SORT);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mAdapter.swapCursor(data);

        if ((data == null) || (data.getCount() == 0)) {

            setIntroTitle();

            mRootView.findViewById(android.R.id.empty).setVisibility(View.GONE);
            mRootView.findViewById(R.id.history_empty_list).setVisibility(View.VISIBLE);
        } else {
            mRootView.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.history_empty_list).setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private void setIntroTitle() {
        final boolean isPowerConnected = BatteryHelper.isPowerConnected(getActivity().getApplicationContext());
        final TextView vIntro = (TextView) mRootView.findViewById(R.id.history_empty_list_title);
        final Resources res = getResources();
        final String powerAction = res.getString(isPowerConnected ? R.string.history_empty_list_title_disconnect : R.string.history_empty_list_title_connect);
        vIntro.setText(String.format(res.getString(R.string.history_empty_list_title), powerAction));
    }


    public static interface HistoryQuery {
        int _TOKEN = 0x10;
        final String[] HISTORY_SUMMARY_PROJECTION = new String[]{
                History._ID,
                History.IS_POWER_ON,
                History.BATTERY_LEVEL,
                History.TIME_STAMP,
                History.IS_FIRST,
                History.IS_LAST,
                DailyHistory.TOTAL,
//                History.NOTIFY_GROUP,
//                History.JULIAN_DAY
        };
        final int _ID = 0;
        final int IS_POWER_ON = 1;
        final int BATTERY_LEVEL = 2;
        final int TIME_STAMP = 3;
        final int IS_FIRST = 4;
        final int IS_LAST = 5;
        final int TOTAL = 6;
//        final int NOTIFY_GROUP = 6;
//        final int JULIAN_DAY = 7;
    }
}

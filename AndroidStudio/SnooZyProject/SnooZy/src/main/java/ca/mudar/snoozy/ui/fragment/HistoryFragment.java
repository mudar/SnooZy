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


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tonicartos.superslim.LayoutManager;

import ca.mudar.snoozy.Const;
import ca.mudar.snoozy.R;
import ca.mudar.snoozy.model.HistorySection;
import ca.mudar.snoozy.model.Queries;
import ca.mudar.snoozy.model.SectionsArray;
import ca.mudar.snoozy.provider.ChargerContract;
import ca.mudar.snoozy.ui.adapter.HistoryAdapter;
import ca.mudar.snoozy.util.BatteryHelper;

import static ca.mudar.snoozy.provider.ChargerContract.History;
import static ca.mudar.snoozy.util.LogUtils.makeLogTag;

public class HistoryFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = makeLogTag(HistoryFragment.class);
    //    private Cursor mCursor = null;
    private HistoryAdapter mAdapter;
    private View mRootView;
//    private View mHeaderView;
//    private View mFooterView;
//    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mRootView = inflater.inflate(R.layout.fragment_list_history, container, false);

//        mHeaderView = inflater.inflate(R.layout.fragment_list_history_header, null);
//        mFooterView = inflater.inflate(R.layout.fragment_list_history_footer, null);

        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        final RecyclerView recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);

//        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
//        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(layoutManager);

//        recyclerView.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));

//        recyclerView.setHasFixedSize(false);

//        if (mHeaderView != null) {
//            try {
//                getRecyclerView().addHeaderView(mHeaderView, null, false);
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            }
//        }
//        if (mFooterView != null) {
//            try {
//                getRecyclerView().addFooterView(mFooterView, null, false);
//            } catch (IllegalStateException e) {
//                e.printStackTrace();
//            }
//        }

        LayoutManager layoutManager = new LayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.setAdapter(null);

        mAdapter = new HistoryAdapter(getActivity(),
                R.layout.list_item_content,
                null);
        recyclerView.setAdapter(mAdapter);

//        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(Queries.HistorySummaryQuery._TOKEN, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == Queries.HistorySummaryQuery._TOKEN) {
            return new CursorLoader(getActivity().getBaseContext(),
                    ChargerContract.DailyHistory.CONTENT_URI,
                    Queries.HistorySummaryQuery.PROJECTION,
                    null,
                    null,
                    ChargerContract.DailyHistory.DEFAULT_SORT);
        } else if (id == Queries.HistoryDetailsQuery._TOKEN) {
            return new CursorLoader(getActivity().getBaseContext(),
                    ChargerContract.History.CONTENT_URI_PER_DAY,
                    Queries.HistoryDetailsQuery.PROJECTION,
                    null,
                    null,
                    History.DEFAULT_SORT);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        final int id = loader.getId();

        if (id == Queries.HistorySummaryQuery._TOKEN) {
            SectionsArray headers = new SectionsArray();
            int section = 0;
            int offset = 0;
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    final int julianDay = cursor.getInt(Queries.HistorySummaryQuery.JULIAN_DAY);
                    final int total = cursor.getInt(Queries.HistorySummaryQuery.TOTAL);

                    final HistorySection header = new HistorySection(section, julianDay, total, offset);
                    headers.append(offset, header);

                    section++;
                    offset += total + 1;
                } while (cursor.moveToNext());
            }

            mAdapter.setHeaders(headers);
            getLoaderManager().initLoader(Queries.HistoryDetailsQuery._TOKEN, null, this);
        } else if (id == Queries.HistoryDetailsQuery._TOKEN) {

            toggleVisibility(cursor == null || cursor.getCount() == 0);

            mAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);

    }

    private void setIntroTitle() {
        final SharedPreferences sharedPrefs = getActivity().getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
        final String cacheAge = sharedPrefs.getString(Const.PrefsNames.CACHE_AGE, Const.PrefsValues.CACHE_ALL);

        final TextView vIntro = (TextView) mRootView.findViewById(R.id.history_empty_list_title);

        if (Const.PrefsValues.CACHE_NONE.equals(cacheAge)) {
            vIntro.setVisibility(View.GONE);
        } else {
            final boolean isPowerConnected = BatteryHelper.isPowerConnected(getActivity().getApplicationContext());
            final Resources res = getResources();
            final String powerAction = res.getString(isPowerConnected ? R.string.history_empty_list_title_disconnect : R.string.history_empty_list_title_connect);
            vIntro.setText(String.format(res.getString(R.string.history_empty_list_title), powerAction));
        }
    }

    private void toggleVisibility(boolean isEmpty) {

        if (isEmpty) {
            getActivity().setTitle(R.string.app_name);
            setIntroTitle();

            getActivity().findViewById(R.id.header_legend).setVisibility(View.GONE);
            mRootView.findViewById(R.id.recycler_view).setVisibility(View.GONE);
            mRootView.findViewById(android.R.id.empty).setVisibility(View.GONE);
            mRootView.findViewById(R.id.history_empty_list).setVisibility(View.VISIBLE);
        } else {
            getActivity().setTitle(R.string.activity_main);

            getActivity().findViewById(R.id.header_legend).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.recycler_view).setVisibility(View.VISIBLE);
            mRootView.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
            mRootView.findViewById(R.id.history_empty_list).setVisibility(View.GONE);
        }
    }

}

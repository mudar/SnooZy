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

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.ArrayList;

import ca.mudar.snoozy.util.SelectionBuilder;

import static ca.mudar.snoozy.provider.ChargerContract.DailyHistory;
import static ca.mudar.snoozy.provider.ChargerContract.History;
import static ca.mudar.snoozy.provider.ChargerDatabase.Tables;
import static ca.mudar.snoozy.util.LogUtils.makeLogTag;

public class ChargerProvider extends ContentProvider {
    private static final String TAG = makeLogTag(ChargerProvider.class);
    private static final int HISTORY = 100;
    private static final int HISTORY_PER_DAY = 101;
    private static final int HISTORY_ID = 102;
    private static final int DAILY_HISTORY = 200;
    private static final int DAILY_HISTORY_DAY = 201;
    private static final UriMatcher URI_MATCHER = buildUriMatcher();
    private ChargerDatabase mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ChargerContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "history", HISTORY);
        matcher.addURI(authority, "history/day", HISTORY_PER_DAY);
        matcher.addURI(authority, "history/*", HISTORY_ID);
        matcher.addURI(authority, "daily_history", DAILY_HISTORY);
        matcher.addURI(authority, "daily_history/*", DAILY_HISTORY_DAY);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        final Context context = getContext();
        mOpenHelper = new ChargerDatabase(context);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        final int match = URI_MATCHER.match(uri);
        final SelectionBuilder builder = buildExpandedSelection(uri, match);

        Cursor c = builder.where(selection, selectionArgs).query(db, projection, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public String getType(Uri uri) {
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case HISTORY:
            case HISTORY_PER_DAY:
                return History.CONTENT_TYPE;
            case HISTORY_ID:
                return History.CONTENT_ITEM_TYPE;
            case DAILY_HISTORY:
                return DailyHistory.CONTENT_TYPE;
            case DAILY_HISTORY_DAY:
                return DailyHistory.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = URI_MATCHER.match(uri);

        switch (match) {
            case HISTORY: {
                db.insertOrThrow(Tables.HISTORY, null, values);
                getContext().getContentResolver().notifyChange(ChargerContract.DailyHistory.CONTENT_URI, null);
                getContext().getContentResolver().notifyChange(uri, null);
                return History.buildHistoryUri(values.getAsString(BaseColumns._ID));
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).delete(db);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSimpleSelection(uri);
        int retVal = builder.where(selection, selectionArgs).update(db, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    private SelectionBuilder buildSimpleSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = URI_MATCHER.match(uri);
        switch (match) {
            case HISTORY: {
                return builder.table(Tables.HISTORY);
            }
            case HISTORY_ID: {
                final String historyId = History.getHistoryId(uri);
                return builder.table(Tables.HISTORY)
                        .where(BaseColumns._ID + "=?", historyId);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri for " + match + ": " + uri);
            }
        }
    }

    private SelectionBuilder buildExpandedSelection(Uri uri, int match) {
        final SelectionBuilder builder = new SelectionBuilder();
        switch (match) {
            case HISTORY_PER_DAY: {
                return builder
                        .table(Tables.HISTORY_JOIN_DAILY)
                        .map(History.IS_FIRST, History.TIME_STAMP + " = " + DailyHistory.MIN)
                        .map(History.IS_LAST, History.TIME_STAMP + " = " + DailyHistory.MAX);

            }
            case DAILY_HISTORY: {
                return builder.table(Tables.DAILY_HISTORY);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

}

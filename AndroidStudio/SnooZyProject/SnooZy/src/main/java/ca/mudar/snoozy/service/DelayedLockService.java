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

package ca.mudar.snoozy.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;

import ca.mudar.snoozy.Const;
import ca.mudar.snoozy.util.LockScreenHelper;

import static ca.mudar.snoozy.util.LogUtils.makeLogTag;

public class DelayedLockService extends IntentService {
    private static final String TAG = makeLogTag(DelayedLockService.class);
    private boolean mIsRunning = false;

    public DelayedLockService() {
        super(TAG);
    }

    public static Intent newIntent(Context context,
                                   String screenLockStatus,
                                   String powerConnectionStatus,
                                   String powerConnectionType,
                                   boolean isConnectedPower,
                                   int delayToLock) {
        final Intent intent = new Intent(Intent.ACTION_SYNC, null, context, DelayedLockService.class);

        final Bundle extras = new Bundle();
        extras.putString(Const.IntentExtras.SCREEN_LOCK_STATUS, screenLockStatus);
        extras.putString(Const.IntentExtras.POWER_CONNECTION_STATUS, powerConnectionStatus);
        extras.putString(Const.IntentExtras.POWER_CONNECTION_TYPE, powerConnectionType);
        extras.putBoolean(Const.IntentExtras.IS_CONNECTED, isConnectedPower);
        extras.putInt(Const.IntentExtras.DELAY_TO_LOCK, delayToLock);
        intent.putExtras(extras);

        return intent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mIsRunning = false;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (mIsRunning) return;

        final String screenLockStatus = intent.getStringExtra(Const.IntentExtras.SCREEN_LOCK_STATUS);
        final String powerConnectionStatus = intent.getStringExtra(Const.IntentExtras.POWER_CONNECTION_STATUS);
        final String powerConnectionType = intent.getStringExtra(Const.IntentExtras.POWER_CONNECTION_TYPE);
        final boolean isConnectedPower = intent.getBooleanExtra(Const.IntentExtras.IS_CONNECTED, true);
        final int delayToLock = intent.getIntExtra(Const.IntentExtras.DELAY_TO_LOCK, 0);

        mIsRunning = true;
        SystemClock.sleep((long) delayToLock);

        LockScreenHelper.lockScreen(getApplicationContext(),
                screenLockStatus,
                powerConnectionStatus,
                powerConnectionType,
                isConnectedPower);
    }

    @Override
    public void onDestroy() {
        mIsRunning = false;

        super.onDestroy();
    }

}

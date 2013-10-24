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

package ca.mudar.snoozy.receiver;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

import ca.mudar.snoozy.Const;
import ca.mudar.snoozy.R;
import ca.mudar.snoozy.ui.activity.MainActivity;
import ca.mudar.snoozy.util.BatteryHelper;
import ca.mudar.snoozy.util.ComponentHelper;

import static ca.mudar.snoozy.provider.ChargerContract.History;
import static ca.mudar.snoozy.util.LogUtils.makeLogTag;

public class PowerConnectionReceiver extends BroadcastReceiver
        implements AudioManager.OnAudioFocusChangeListener {
    private static final String TAG = makeLogTag(PowerConnectionReceiver.class);
    private static final int AUDIO_FOCUS_DURATION = 1000;
    private static final String FORMAT_ORDINAL_DAY = "yyyyDDDD"; // Ensure that ordinalDate is greater than previous julianDay values
    private Ringtone mRingtone;

    @Override
    public void onReceive(Context context, Intent intent) {

        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        // Get user preferences
        final SharedPreferences sharedPrefs = context.getSharedPreferences(Const.APP_PREFS_NAME, Context.MODE_PRIVATE);
        final SharedPreferences.Editor sharedPrefsEditor = sharedPrefs.edit();
        final boolean hasNotifications = sharedPrefs.getBoolean(Const.PrefsNames.HAS_NOTIFICATIONS, false);
        final boolean hasVibration = (sharedPrefs.getBoolean(Const.PrefsNames.HAS_VIBRATION, false) && vibrator.hasVibrator());
        final boolean hasSound = sharedPrefs.getBoolean(Const.PrefsNames.HAS_SOUND, false);
        final boolean onScreenLock = sharedPrefs.getBoolean(Const.PrefsNames.ON_SCREEN_LOCK, true);
        final boolean onPowerLoss = sharedPrefs.getBoolean(Const.PrefsNames.ON_POWER_LOSS, false);
        final int notifyCount = sharedPrefs.getInt(Const.PrefsNames.NOTIFY_COUNT, 1);
        final int notifyGroup = sharedPrefs.getInt(Const.PrefsNames.NOTIFY_GROUP, 1);

        final String action = intent.getAction();
        if (action == null) return;

        if (action.equals(Const.IntentActions.NOTIFY_DELETE)) {
            if (hasNotifications) {
                // Reset the notification counter (and group) on NOTIFY_DELETE
                sharedPrefsEditor.putInt(Const.PrefsNames.NOTIFY_COUNT, 1);
                sharedPrefsEditor.putInt(Const.PrefsNames.NOTIFY_GROUP, notifyGroup + 1);
                sharedPrefsEditor.apply();
            }
        } else if (action.equals(Intent.ACTION_POWER_CONNECTED) || action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
            final boolean isConnectedPower = action.equals(Intent.ACTION_POWER_CONNECTED);

            // Lock the screen, following the user preferences
            lockScreen(context, onScreenLock, onPowerLoss, isConnectedPower);

            // Save in database
            saveHistoryItem(context.getApplicationContext(), isConnectedPower, notifyGroup);

            if (hasNotifications) {
                // Send notification, with sound and vibration
                notify(context, isConnectedPower, hasVibration, hasSound, notifyCount);

                // Increment the notification counter
                sharedPrefsEditor.putInt(Const.PrefsNames.NOTIFY_COUNT, notifyCount + 1);
                sharedPrefsEditor.apply();
            } else {
                // Native Vibration or Sound, without Notifications
                nativeVibrate(context, hasVibration);

                nativeRingtone(context, hasSound);
            }
        }
    }

    private void lockScreen(Context context, boolean onScreenLock, boolean onPowerLoss, boolean isConnectedPower) {

        if (!ComponentHelper.isDeviceAdmin(context.getApplicationContext())) {
            return;
        }

        final boolean isLocked = ((KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE))
                .inKeyguardRestrictedInputMode();

        if (onScreenLock && onPowerLoss) {
            if (isLocked && !isConnectedPower) {
                DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDPM.lockNow();
            }
        } else if (onScreenLock) {
            if (isLocked) {
                DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDPM.lockNow();
            }

        } else if (onPowerLoss) {
            if (!isConnectedPower) {
                DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
                mDPM.lockNow();
            }
        } else {
            DevicePolicyManager mDPM = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
            mDPM.lockNow();
        }
    }

    private void notify(Context context, boolean isConnectedPower, boolean hasVibration, boolean hasSound, int notifyCount) {
        final Resources res = context.getResources();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notify)
                        .setContentTitle(res.getString(R.string.notify_content_title))
                        .setAutoCancel(true);

        if (notifyCount == 1) {
            final int resContentTextSingle = (isConnectedPower ?
                    R.string.notify_content_text_single_on : R.string.notify_content_text_single_off);
            mBuilder.setContentText(res.getString(resContentTextSingle));
        } else {
            mBuilder.setNumber(notifyCount)
                    .setContentText(res.getString(R.string.notify_content_text_multi));
        }


        if (hasVibration && hasSound) {
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
        } else if (hasVibration) {
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        } else if (hasSound) {
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        }

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MainActivity.class);
        final Bundle extras = new Bundle();
        extras.putBoolean(Const.IntentExtras.INCREMENT_NOTIFY_GROUP, true);
        extras.putBoolean(Const.IntentExtras.RESET_NOTIFY_NUMBER, true);
        resultIntent.putExtras(extras);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        mBuilder.setContentIntent(PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT));


        Intent receiverIntent = new Intent(context, PowerConnectionReceiver.class);
        receiverIntent.setAction(Const.IntentActions.NOTIFY_DELETE);
        PendingIntent deleteIntent = PendingIntent.getBroadcast(context, Const.RequestCodes.RESET_NOTIFY_NUMBER, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setDeleteIntent(deleteIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // NOTIFY_ID allows updates to the notification later on.
        mNotificationManager.notify(Const.NOTIFY_ID, mBuilder.build());
    }

    private void nativeVibrate(Context context, boolean hasVibration) {
        if (hasVibration) {
            final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            vibrator.vibrate(Const.VIBRATION_DURATION);
        }
    }

    private void nativeRingtone(Context context, boolean hasSound) {
        if (hasSound) {
            final AudioManager.OnAudioFocusChangeListener listener = this;
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            int result = audioManager.requestAudioFocus(listener,
                    AudioManager.STREAM_NOTIFICATION,
                    AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mRingtone = RingtoneManager.getRingtone(context, notification);
                mRingtone.setStreamType(AudioManager.STREAM_NOTIFICATION);
                mRingtone.play();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        audioManager.abandonAudioFocus(listener);
                    }
                }, AUDIO_FOCUS_DURATION);
            }
        }
    }

    private void saveHistoryItem(Context context, boolean isPowerConnected, int notifyGroup) {

        final float batteryLevel = BatteryHelper.getBatteryLevel(context);
        final long millis = System.currentTimeMillis();
        final int ordinalDay = Integer.valueOf(new SimpleDateFormat(FORMAT_ORDINAL_DAY).format(new Date(millis)));

        ContentValues newItem = new ContentValues();
        newItem.put(History.IS_POWER_ON, (isPowerConnected ? 1 : 0));
        newItem.put(History.BATTERY_LEVEL, Math.round(batteryLevel * 100));
        newItem.put(History.NOTIFY_GROUP, notifyGroup);
        newItem.put(History.TIME_STAMP, millis);
        newItem.put(History.ORDINAL_DAY, ordinalDay);
        context.getContentResolver().insert(
                History.CONTENT_URI,
                newItem
        );
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mRingtone.isPlaying())
                    mRingtone.stop();
                break;
        }
    }
}
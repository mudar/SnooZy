package ca.mudar.snoozy.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import ca.mudar.snoozy.Const;

public class RingtoneHelper {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Ringtone getRingtoneCompat(Context context, Uri ringtoneUri) {
        final Ringtone ringtone = RingtoneManager.getRingtone(context, ringtoneUri);

        if (Const.SUPPORTS_LOLLIPOP) {
            ringtone.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build());
        } else {
            ringtone.setStreamType(AudioManager.STREAM_NOTIFICATION);
        }

        return ringtone;
    }

}

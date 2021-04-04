package com.dotpot.app.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.ui.messaging.InAppMessage;
import com.dotpot.app.utl;


public class FCMNotificationUtils {
    private static final String TAG = FCMNotificationUtils.class.getCanonicalName();
    private static Uri defaultSoundUri;
    private static Bitmap icLauncher;
    private static int notificationColor;
    SoundPool soundPool;
    private String channelId;
    private Context ctx;

    public FCMNotificationUtils(Context context) {
        ctx = context;
    }

    public static boolean checkRingerIsOn(Context context) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    public void sendNotification(Context context, String type, int NOTIFICATION_ID, String title,
                                 String message, String imageURL, String channelId,
                                 Intent intent, int pendingIntentFlag, InAppMessage chatMessage) {


        if (message.contains(Constants.C2C_DELETE))
            return;
        int icon = R.drawable.ic_notifications_black_24dp;

        utl.NotificationMessage notificationMessage = new utl.NotificationMessage(
                type, title,
                message, System.currentTimeMillis(), icon,
                intent);


        if (chatMessage.getQuotedTextId() == null || !chatMessage.getQuotedTextId().equals("skip"))
            utl.NotificationMessage.saveNotification(notificationMessage, context);


        if (chatMessage.getQuotedTextId() != null
                && chatMessage.getQuotedTextId().equals("skip")) {

            if (checkRingerIsOn(context)) {
                try {

                    if (soundPool == null)
                        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
                    int soundId = soundPool.load(context, R.raw.notif_tone, 1);

                    soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                        @Override
                        public void onLoadComplete(SoundPool soundPool, int i, int i1) {

                            soundPool.play(soundId, 1, 1, 0, 0, 1);
                        }
                    });


                    new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                soundPool.release();
                            } catch (Exception e) {
                                if (utl.DEBUG_ENABLED) e.printStackTrace();
                            }


                        }
                    }, 1000);
                } catch (Exception e) {
                    utl.e("fcm", "Tolerable err at err194");
                }


            }
            showNotification(context, title, message, intent);
        }
    }

    public void showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = utl.randomInt(3);
        String channelId = "notifications";
        String channelName = "Notifications from " + ResourceUtils.getString(R.string.app_name);
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setColor(ResourceUtils.getColor(R.color.colorAccent));

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_ONE_SHOT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }
}
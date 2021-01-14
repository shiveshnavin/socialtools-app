package com.dotpot.app.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.utl;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public  class FCMNotificationUtils {
    private static final String TAG = FCMNotificationUtils.class.getCanonicalName();
    private static Uri defaultSoundUri;
    private static Bitmap icLauncher;
    private static int notificationColor;
    private String channelId;

    private static NotificationCompat.Builder setNotificationStyle(NotificationCompat.Builder builder,
                                                                   String imageURL,
                                                                   String title,
                                                                   String message) {
        if (!TextUtils.isEmpty(imageURL)) {
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle()
                    .bigLargeIcon(icLauncher)
                    .setSummaryText(message)
                    .setBigContentTitle(title);
            Bitmap bitmapFromUrl = getBitmapFromUrl(imageURL);
            if (bitmapFromUrl != null) {
                bigPictureStyle = bigPictureStyle
                        .bigPicture(bitmapFromUrl);
            }
            builder.setStyle(bigPictureStyle);
        } else {
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
        }
        return builder;
    }

    private static Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (IOException e) {
            Log.e(TAG, "getBitmapFromUrl: ", e);
            return null;
        }
    }

    private static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public int getSmallIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT);
        return useWhiteIcon ? R.drawable.logo :  R.drawable.logo;
    }

    public int getBigIcon() {
        return  R.drawable.logo;
    }
    public NotificationCompat.Builder getNotificationBuilder(Context context,
                                                             String title,
                                                             String message,
                                                             String imageURL,
                                                             String channelId,
                                                             PendingIntent pendingIntent) {

        if (this.channelId == null)
            this.channelId = ResourceUtils.getString(R.string.app_name);
        if (defaultSoundUri == null)
            defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (icLauncher == null) {
            icLauncher = getBitmapFromDrawable(ResourceUtils.getDrawable(getBigIcon()));
        }
        if (notificationColor == 0) {
            notificationColor = ResourceUtils.getColor(R.color.colorAccent);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, !TextUtils.isEmpty(channelId) ? channelId : this.channelId)
                .setAutoCancel(true)
                .setSmallIcon(getSmallIcon())
                .setLargeIcon(icLauncher)
                .setSound(defaultSoundUri)
                .setColor(notificationColor)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setTicker(message);

        return setNotificationStyle(builder, imageURL, title, message);
    }

    public static boolean checkRingerIsOn(Context context){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        return am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    @NonNull
    private Bitmap getBitmapFromDrawable(@NonNull Drawable drawable) {
        final Bitmap bmp = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bmp);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bmp;
    }

    public void sendNotification(Context context,String type, int NOTIFICATION_ID, String title,
                                 String message, String imageURL, String channelId,
                                 Intent intent, int pendingIntentFlag) {


        if(message.contains(Constants.C2C_DELETE))
            return;
        int icon=R.drawable.logo;
        if(type.equals(utl.NotificationMessage.TYPE_REQUEST))
        {
            icon=R.drawable.ic_add_friend;
        }
        if(type.equals(utl.NotificationMessage.TYPE_REPLY))
        {
            icon=R.drawable.ic_question_faq;
        }
        if(type.equals(utl.NotificationMessage.TYPE_MESSAGE))
        {
            icon=R.drawable.ic_message_black_24dp;
        }
        utl.NotificationMessage notificationMessage=new utl.NotificationMessage(
                type,title,
                message,System.currentTimeMillis(),icon,
                intent);

        utl.NotificationMessage.saveNotification(notificationMessage,context);


        if(checkRingerIsOn(context))
        {

            try {
                final SoundPool  soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
                int soundId = soundPool.load(context, R.raw.notif_tone, 1);

                soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                    @Override
                    public void onLoadComplete(SoundPool soundPool, int i, int i1) {

                        soundPool.play(soundId, 1, 1, 0, 0, 1);



                    }
                });


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            soundPool.release();
                        } catch (Exception e) {
                            if(utl.DEBUG_ENABLED) e.printStackTrace();
                        }


                    }
                },1000);
            } catch (Exception e) {
                utl.e("fcm","Tolerable err at err194");
                //if(utl.DEBUG_ENABLED) e.printStackTrace();
            }


        }




        NotificationManager notificationManager = getNotificationManager(context);
        String appName = ResourceUtils.getString(R.string.app_name);

       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getNotificationChannel(notificationManager, appName);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            /* Create or update. */
            NotificationChannel channel = new NotificationChannel("messages",
                    "Receive Message Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder;
        builder = getNotificationBuilder(context,
                !TextUtils.isEmpty(title) ? title : appName,
                message,
                imageURL,
                channelId,
                PendingIntent.getActivity(context, NOTIFICATION_ID, intent, pendingIntentFlag));
        if (builder != null) {

            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getNotificationChannel(NotificationManager notificationManager, String appName) {
        String id = ResourceUtils.getString(R.string.app_name);

        // The user-visible name of the channel.
        String channelName = ResourceUtils.getString(R.string.app_name);

        // The user-visible description of the channel.
        String channelDescription = ResourceUtils.getString(R.string.app_name) + appName;

        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel mChannel = new NotificationChannel(id, channelName, importance);

        // Configure the notification channel.
        mChannel.setDescription(channelDescription);
        mChannel.enableLights(true);

        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        notificationManager.createNotificationChannel(mChannel);
    }
}
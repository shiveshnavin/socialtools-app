package com.dotpot.app.services;

/**
 * Created by shivesh on 21/2/17.
 */

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.dotpot.app.R;
import com.dotpot.app.models.InAppMessage;
import com.dotpot.app.utils.FCMNotificationUtils;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utl;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;


public class FirebaseMsgService extends FirebaseMessagingService {

    private static final String TAG = "MyAndroidFCMService";
    Gson js;
    Context ctx;
    FCMNotificationUtils utils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e(TAG, remoteMessage.toString());
        ctx = getApplicationContext();
        utl.init(ctx);

        E2EChatService.CHAT_UPD_DUR = FirebaseRemoteConfig.getInstance().getLong("chat_upd_dur");


        utils = new FCMNotificationUtils(getApplicationContext());


        Map<String, String> data = remoteMessage.getData();
        try {
            utl.e("firebase noti " + Thread.currentThread().getName(), data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (data != null) {
                String json = utl.js.toJson(data);
                InAppMessage chatMessage = utl.js.fromJson(json, InAppMessage.class);

                ArrayList<InAppMessage> notifAgg = new ArrayList<>();
                E2EChatService.saveMessage(getApplicationContext(), chatMessage, notifAgg);

                showNotification(ctx,chatMessage.getMsgTitle(), "" + chatMessage.getRefinedMessage(),chatMessage.getIntent());

//                utils.sendNotification(ctx, utl.NotificationMessage.TYPE_MESSAGE, utl.randomInt(3),
//                        chatMessage.getMsgTitle(),
//                        "" + chatMessage.getRefinedMessage(),
//                        null, "messages",
//                        remoteMessage.toIntent(), PendingIntent.FLAG_ONE_SHOT);

//                for (InAppMessage chatMessage : notifAgg) {
//                    try {
//                        E2EChatService.createNotification(getApplicationContext(),
//                                chatMessage);
//                    } catch (Exception e) {
//                        FirebaseCrashlytics.getInstance().recordException(e);
//                    }
//                }
            }
        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    public void showNotification(Context context, String title, String body, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationId = 1;
        String channelId = "notifications";
        String channelName = "Notifications from "+ ResourceUtils.getString(R.string.app_name);
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(body);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(intent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(notificationId, mBuilder.build());
    }

}

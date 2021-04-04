package com.dotpot.app.services;

/**
 * Created by shivesh on 21/2/17.
 */

import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import com.dotpot.app.ui.messaging.InAppMessage;
import com.dotpot.app.ui.messaging.MessagingService;
import com.dotpot.app.utils.FCMNotificationUtils;
import com.dotpot.app.utl;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
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



        if (utils == null)
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
                MessagingService.saveMessage(getApplicationContext(), chatMessage, notifAgg);


                utils.sendNotification(ctx, utl.NotificationMessage.TYPE_MESSAGE, utl.randomInt(3),
                        chatMessage.getMsgTitle(),
                        "" + chatMessage.getRefinedMessage(),
                        null, "messages",
                        remoteMessage.toIntent(), PendingIntent.FLAG_ONE_SHOT,
                        chatMessage);

            }
        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }


}

package com.dotpot.app.services;

/**
 * Created by shivesh on 21/2/17.
 */

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.models.InAppMessage;
import com.dotpot.app.ui.HomeActivity;
import com.dotpot.app.ui.SplashActivity;
import com.dotpot.app.utils.FCMNotificationUtils;
import com.dotpot.app.utl;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class FirebaseMsgService extends FirebaseMessagingService {

    private static final String TAG = "MyAndroidFCMService";
    Gson js;
    Context ctx;
    FCMNotificationUtils utils;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, remoteMessage.toString());
        ctx = getApplicationContext();
        utl.init(ctx);

        E2EChatService.CHAT_UPD_DUR = FirebaseRemoteConfig.getInstance().getLong("chat_upd_dur");


        utils = new FCMNotificationUtils();


        Map<String, String> data = remoteMessage.getData();
        try {
            utl.e("firebase noti "+Thread.currentThread().getName(), data);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (data != null) {
                if (data.containsKey("groupName")) {
                    String json = utl.js.toJson(data);
                    //  utl.e("FireBaseMessage",json);
                    InAppMessage cm = utl.js.fromJson(json, InAppMessage.class);

                    ArrayList<InAppMessage> notifAgg = new ArrayList<>();
                    E2EChatService.saveMessage(getApplicationContext(), cm, notifAgg);

                    for (InAppMessage chatMessage : notifAgg) {


                        try {
                            E2EChatService.createNotification(getApplicationContext(),
                                    chatMessage);
                        } catch (Exception e) {
                            FirebaseCrashlytics.getInstance().recordException(e);
                        }


                    }


                    // utl.e("FMessage",""+cm.getMessage());

                }
                else if (data.containsKey("title") && data.containsKey("message")) {
                    Intent intent = new Intent(ctx, HomeActivity.class);

                    try {
                        JSONObject jo = new JSONObject(data.get("body"));
                        if (data.get("title")!=null && data.get("title").toLowerCase().contains("request")) {

                            intent = new Intent(ctx, SplashActivity.class);
                            JSONObject body = new JSONObject(data.get("body"));

                            intent.putExtra("type", (Constants.O2O));
                            intent.putExtra("groupId", body.getString("groupId"));

                        }
                        else
                        if (data.get("title")!=null && data.get("title").toLowerCase().contains("answered your query")) {

                            intent = new Intent(ctx, SplashActivity.class);
                            JSONObject body = new JSONObject(data.get("body"));
                            intent.putExtra("groupId", body.getString("groupId"));

                        }
                    } catch (JSONException e) {
                        if (utl.DEBUG_ENABLED) e.printStackTrace();
                    }

                    String type=utl.NotificationMessage.TYPE_REQUEST;
                    if(data.get("title")!=null && data.get("title").contains("answered your query")){
                        type=utl.NotificationMessage.TYPE_MESSAGE;
                    }
                     createNotification(data.get("title"), type,
                            data.get("message"),
                            intent);

                }else if (data.containsKey("title") && data.containsKey("message")
                        && data.containsKey("url")) {
                    Intent intent = new Intent(ctx, HomeActivity.class);

                    try {


                        intent = new Intent(ctx, HomeActivity.class);
                        intent.setData(Uri.parse(data.get("url")));


                    } catch (Exception e) {
                        if (utl.DEBUG_ENABLED) e.printStackTrace();
                    }

                    createNotification(data.get("title"),
                            utl.NotificationMessage.TYPE_PROMO,
                            data.get("message"),
                            intent);

                }

            }
        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }


    }

    private void createNotification(String title, String type, String body, Intent intent) {
        try {
            utils.sendNotification(ctx, type, 100, title, body,
                    null, getString(R.string.app_name), intent,
                    PendingIntent.FLAG_ONE_SHOT);

        } catch (Exception e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        }
    }


}

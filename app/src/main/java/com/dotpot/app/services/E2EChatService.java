package com.dotpot.app.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.dotpot.app.models.GenricUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.concurrent.CountDownLatch;

public class E2EChatService extends Service {
    public static E2EChatService instance;
    public static GenricUser user;
    public static Long CHAT_UPD_DUR = 199l;
    public static Long lastUpdate = 200l;
    public static DBService databaseHelper;
    public String TAG = "E2EChatService";
//    public FirebaseFirestore firestore;
//    public CollectionReference groupsRef;
    public CountDownLatch countDownLatch;
    Context context;

    public E2EChatService() {

        instance = this;
        if (CHAT_UPD_DUR == 199l)
            CHAT_UPD_DUR = FirebaseRemoteConfig.getInstance().getLong("chat_upd_dur");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        if (context == null)
            context = getApplicationContext();
        Log.d("E2EChatService", " Chats onCreate : " + Thread.currentThread().getName());

        databaseHelper = DBService.getInstance(context);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d("E2EChatService", " Chats onStartCommand : " + Thread.currentThread().getName());
        instance = this;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        instance = null;
        sendBroadcast(new Intent("YouWillNeverKillMe"));
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (context == null)
            context = getApplicationContext();

        if (context == null)
            return;
        Intent intent = new Intent(context, E2EChatService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        super.onTaskRemoved(rootIntent);

    }
}

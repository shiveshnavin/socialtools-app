package com.dotpot.app.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.dotpot.app.Constants;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.models.InAppMessage;
import com.dotpot.app.ui.SplashActivity;
import com.dotpot.app.utils.FCMNotificationUtils;
import com.dotpot.app.utl;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
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

    public static void saveMessage(Context ctx, InAppMessage chatMessage, ArrayList<InAppMessage> notifAgg) {

        if (user == null)
            user = utl.readUserData();


        if (chatMessage.getMessage().contains(Constants.C2C_EXIT)) {

            if (chatMessage.getSenderId().equals(user.getId())) {

                String id = chatMessage.getSenderId();
                String groupId = chatMessage.getGroupId();
                utl.e("Chats", "Exit : " + id + " deleting " + groupId);
                databaseHelper.deleteGroup(groupId);
            }
            return;
        }


        boolean isInDb = false;
        if (databaseHelper.getMessageById(chatMessage.getId()).getCount() > 0) {
            isInDb = true;
        }

        if (!isInDb) {
            utl.e("Chats Inserting ", "" + chatMessage.getMessage() + " : " +
                    databaseHelper.insertData(chatMessage));


            if (System.currentTimeMillis() - lastUpdate > CHAT_UPD_DUR) {
                lastUpdate = System.currentTimeMillis();
                Intent intent = new Intent("NEW_MESSAGAGES");
                try {
                    if (!chatMessage.getMessage().contains(Constants.C2C_DELETE)) {

                        intent.putExtra("delete", "1");
                    }
                } catch (Exception e) {
                    FirebaseCrashlytics.getInstance().log(
                            "createNotification : " + e.getMessage() + " --- context is " + (ctx == null));
                }
                LocalBroadcastManager
                        .getInstance(ctx.getApplicationContext()).sendBroadcast(intent);
            }
            if (notifAgg != null)
                notifAgg.add(chatMessage);

        }


    }


    public static Long lastNotif=20l;
    public static void createNotification(Context ctx, InAppMessage chatMessage) {

        if (System.currentTimeMillis() - lastNotif > CHAT_UPD_DUR) {
            lastNotif = System.currentTimeMillis();
        }
        else
        {
            return;
        }
        if (databaseHelper != null) {
            InAppMessage persistentMessage = databaseHelper.findMessageById(chatMessage.getId());
            if (persistentMessage == null)
                return;
        }

        FCMNotificationUtils utils = new FCMNotificationUtils();
        utl.l("Create Notif ");
        // todo process inappnav
        Intent intent = new Intent(ctx, SplashActivity.class);
        intent.putExtra("groupId", chatMessage.getGroupId());


        if(user==null)
            user=utl.readUserData();
//        if (user != null & !chatMessage.getSenderId().equals(user.getId())
//
//                && (ChatActivity.groupId == null ||
//                !ChatActivity.groupId.equals(chatMessage.getGroupId()) ||
//                !ChatActivity.isShowing)
//        ) {
//            String gname = chatMessage.getGroupName();
//
//            GenricUser user=utl.readUserData();
//            try{
//                new JSONObject(gname);
//                gname = Group.getGroupNameFromIGName(gname, user);
//            }catch (Exception e){
//                if (gname.contains(Constants.O2O)) {
//                    gname = Group.getGroupNameFromIGName(gname, user);
//                }
//            }
//
//            String title = "" + gname + " : " + chatMessage.senderNameOnly();
//            if (gname.contains(Constants.O2O)) {
//                gname = Group.getGroupNameFromIGName(gname,user);
//                title = gname;
//            }
//
//
//        }

        utils.sendNotification(ctx, utl.NotificationMessage.TYPE_MESSAGE, 100,
                chatMessage.getGroupName()
                , "" + chatMessage.getRefinedMessage(), null, "messages",
                intent, PendingIntent.FLAG_ONE_SHOT);


    }
//
//    ChatListenerThread thread;
//    public void addChatListeners(){
//
//        if(thread!=null && thread.isAlive())
//        {
//            try {
//                thread.interrupt();
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        thread=new ChatListenerThread();
//        try{
//            thread.instance=this;
//            thread.setName("ChatListenerThread");
//            thread.start();
//
//
//        }catch (Exception e)
//        {
//            Crashlytics.logException(e);
//            e.printStackTrace();
//        }
//
//
//    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (context == null)
            context = getApplicationContext();
        Log.d("E2EChatService", " Chats onCreate : " + Thread.currentThread().getName());

        databaseHelper = DBService.getInstance(context);
//        firestore = FirebaseFirestore.getInstance();
//        groupsRef = firestore.collection(Constants.FIB_GROUPS);
//
//        if (context == null) {
//            Crashlytics.log(Log.ERROR, "e2eservice", "Fatal Error : Context is null in E2EService ");
//            return;
//        }
//        addChatListeners();
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

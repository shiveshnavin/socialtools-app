package com.dotpot.app.ui.messaging;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.androidnetworking.error.ANError;
import com.dotpot.app.Constants;
import com.dotpot.app.binding.NotificationsViewModel;
import com.dotpot.app.interfaces.GenricCallback;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.interfaces.NetworkRequestCallback;
import com.dotpot.app.services.AndroidNetworkService;
import com.dotpot.app.services.DBService;
import com.dotpot.app.utl;
import com.google.common.base.Strings;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessagingService {

    String groupId;
    Context ctx;
    Long pageSize;

    ArrayList<InAppMessage> messages;
    GenricObjectCallback<InAppMessage> onNewMessages;
    GenricObjectCallback<InAppMessage> onOldMessages;
    GenricCallback onResetSignal;
    LifecycleOwner lifecycleOwner;

    static long lastUpdate=0;

    public MessagingService(String groupId, Context ctx,LifecycleOwner lifecycleOwner, GenricObjectCallback<InAppMessage> onNewMessages, GenricObjectCallback<InAppMessage> onOldMessages, GenricCallback onResetSignal) {
        this.groupId = groupId;
        this.ctx = ctx;
        this.lifecycleOwner = lifecycleOwner;
        this.onNewMessages = onNewMessages;
        this.onOldMessages = onOldMessages;
        this.onResetSignal = onResetSignal;
        pageSize = 5000L;//FirebaseRemoteConfig.getInstance().getLong("page_size");
        NotificationsViewModel.getInstance().getNotifications().observe(lifecycleOwner, new Observer<ArrayList<utl.NotificationMessage>>() {
            @Override
            public void onChanged(ArrayList<utl.NotificationMessage> notificationMessages) {
                getMessages();
            }
        });

    }

    public static void saveMessage(Context ctx, InAppMessage chatMessage, ArrayList<InAppMessage> notifAgg) {

        boolean isInDb = false;
        if (DBService.getInstance(ctx).getData(chatMessage.getId()).getCount() > 0) {
            isInDb = true;
        }

        if (!isInDb) {
            utl.e("Chats Inserting ", "" + chatMessage.getMessage() + " : " +
                    DBService.getInstance(ctx).insertData(chatMessage));
            if (System.currentTimeMillis() - lastUpdate > 1000) {
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

    public void getMorePreviousMessages(int offset){
        if(messages.size() <= offset || messages.size() <= offset+pageSize){
            onOldMessages.onEntitySet(new ArrayList<>());
            return;
        }
        onOldMessages.onEntitySet(new ArrayList<>(
                messages.subList(messages.size() - offset -  pageSize.intValue() ,
                messages.size() - offset )));
    }

    public void getMessages(){
        messages = DBService.getInstance(ctx).getAllMessagesForGroup(groupId);
        if(messages==null)
            messages = new ArrayList<>();
        onResetSignal.onStart();
        onNewMessages.onEntitySet(new ArrayList<>( messages.subList(0, Math.min(pageSize.intValue(), messages.size()))));
    }

    public void deleteMessage(String messageId){
        InAppMessage message = DBService.getInstance(ctx).findMessageById(messageId);
        DBService.getInstance(ctx).deleteMessageById(messageId);
        messages.remove(message);
        onResetSignal.onStart();
        getMessages();
    }

    public void sendMessage(String senderId,String recieverId,String message){

        if(Strings.isNullOrEmpty(message))
            return;

        InAppMessage inAppMessage = new InAppMessage();
        inAppMessage.id = ""+System.currentTimeMillis();
        inAppMessage.atachmentType=InAppMessage.attachmentTypes[0];
        inAppMessage.dateTime=System.currentTimeMillis();
        inAppMessage.message=message;
        inAppMessage.recieverId=recieverId;
        inAppMessage.senderId=senderId;
        inAppMessage.groupId = groupId;
        sendMessage(inAppMessage);

    }

    public void sendMessage(InAppMessage message){
        AndroidNetworkService.getInstance(ctx).callPost(Constants.HOST + "/api/messages", message,false, new NetworkRequestCallback() {
            @Override
            public void onSuccessString(String response) {
                messages.add(message);
                onNewMessages.onEntitySet(new ArrayList<>(Arrays.asList(message)));
            }

            @Override
            public void onFail(ANError job) {
                utl.toast(ctx,"Failed to send message ! "+job.getErrorBody());
                onNewMessages.onEntitySet(new ArrayList<>());

            }
        });
    }

    public void fetchMessages(InAppMessage latestMessage){
        if(latestMessage==null){
            latestMessage = new InAppMessage();
            latestMessage.setDateTime(0l);
        }
        AndroidNetworkService.getInstance(ctx).callGet(Constants.HOST+"/api/messages/"+groupId+"?after="+latestMessage.getDateTime(), false, new NetworkRequestCallback() {
            @Override
            public void onSuccessString(String response) {
                utl.JSONParser<InAppMessage> inAppMessageJSONParser = new utl.JSONParser<>();
                List<InAppMessage> messages = inAppMessageJSONParser.parseJSONArray(response,InAppMessage.class);
                if(messages!=null)
                    messages.stream().forEach(m->DBService.getInstance(ctx).insertData(m));
                getMessages();
            }

            @Override
            public void onFail(ANError job) {
                utl.toast(ctx,"Failed to get messages ! "+job.getErrorBody());
                onNewMessages.onEntitySet(new ArrayList<>());

            }
        });
    }


}

package com.igramassistant.app.binding;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.igramassistant.app.App;
import com.igramassistant.app.utl;

import java.util.ArrayList;

public class NotificationsViewModel extends ViewModel {

    private static NotificationsViewModel instance;
    private MutableLiveData<ArrayList<utl.NotificationMessage>> notifications;

    public static NotificationsViewModel getInstance() {
        if (instance == null) {
            instance = new NotificationsViewModel();
            instance.notifications = new MutableLiveData<>();
        }
        return instance;
    }

    public void refresh() {
        ArrayList<utl.NotificationMessage> notifList = utl.NotificationMessage.getAll(App.getAppContext());
        notifications.postValue(notifList);
    }

    public MutableLiveData<ArrayList<utl.NotificationMessage>> getNotifications() {
        return notifications;
    }

    public void updateLocalAndNotify(Context act, ArrayList<utl.NotificationMessage> user) {
        notifications.postValue(user);
    }

}
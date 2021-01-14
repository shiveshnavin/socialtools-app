package com.dotpot.app.services;

import com.dotpot.app.Constants;
import com.dotpot.app.models.ActionItem;
import com.dotpot.app.ui.BaseActivity;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class EventBusService {

    public static EventBusService mIsntance;

    public EventBusService() {
        mIsntance = this;
    }

    public static EventBusService getInstance() {
        if (mIsntance == null) mIsntance = new EventBusService();

        return mIsntance;
    }

    public void doActionItem(ActionItem cm) {

        BaseActivity act = cm.act;
        String actionType = cm.actionType;
        try {
            if (actionType.equals(Constants.ACTION_HOME)) {
                if (act != null) {
                    // todo
                }
            }

            if (cm.doFinish)
                if (act != null) {
                    act.finish();
                }

        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }


    }
}

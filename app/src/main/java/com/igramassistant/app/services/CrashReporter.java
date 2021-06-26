package com.igramassistant.app.services;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class CrashReporter {

    public static void reportMessage(String...msg){
        StringBuilder sb = new StringBuilder();
        for(String m:msg){
            sb.append(m).append(" ");
        }
        FirebaseCrashlytics.getInstance().log(sb.toString());
    }

    public static void reportException(Exception e){
        StringBuilder sb = new StringBuilder();
        FirebaseCrashlytics.getInstance().recordException(e);
    }

}

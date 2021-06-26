package com.igramassistant.app.interfaces;

import com.androidnetworking.error.ANError;

import org.json.JSONObject;

/**
 * Created by shivesh on 29/3/18.
 */

public interface NetworkRequestCallback {

    default void onSuccess(JSONObject response){}  ;
    default void onSuccessString(String response){}  ;
    default void onFail(ANError job){};

}

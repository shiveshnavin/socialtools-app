package com.igramassistant.app.interfaces;


import androidx.annotation.Nullable;

import org.json.JSONObject;

import java.io.File;


public interface NetworkService {
    void callGetString(String url, boolean showLoading, NetworkRequestCallback call);

    void callGet(String url, boolean showLoading, NetworkRequestCallback call);

    void callPost(String url, boolean showLoading, NetworkRequestCallback call);

    void callPost(String url, Object body, boolean showLoading, NetworkRequestCallback call);

    void callPostString(String url, JSONObject body, boolean showLoading, NetworkRequestCallback call);

    void callPost(String url, JSONObject body, boolean showLoading, NetworkRequestCallback call);

    @Nullable
    default void uploadFile(String filename, File file, GenricDataCallback cb){
        throw new RuntimeException("Unimplemented !!!");
    };

    @Nullable
    default void setAccessToken(String token){
        throw new RuntimeException("Unimplemented !!!");
    };

    void updateTokens(String googleToken,String firebaseToken);

    void invalidateAllRuntimeValues();
}

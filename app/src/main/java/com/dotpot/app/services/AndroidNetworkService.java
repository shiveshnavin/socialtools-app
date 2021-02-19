package com.dotpot.app.services;

import android.content.Context;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.dotpot.app.BuildConfig;
import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.binding.GenericUserViewModel;
import com.dotpot.app.interfaces.CacheUtil;
import com.dotpot.app.interfaces.GenricDataCallback;
import com.dotpot.app.interfaces.NetworkRequestCallback;
import com.dotpot.app.interfaces.NetworkService;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class AndroidNetworkService implements NetworkService {


    public static HashMap<String, Integer> apiUsage;
    Context act;
    String accessToken;
    GenricUser user;
    String firebaseAuthToken = "";
    String providerToken = "";
    String appVersionCode = "" + BuildConfig.VERSION_CODE;
    CacheUtil cacheUtil;
    String authHeader = null;

    private static AndroidNetworkService instance;
    public static NetworkService getInstance(Context applicationContext) {
        if(instance == null){
            instance = new AndroidNetworkService(applicationContext);
        }
        return instance;
    }
    
    private AndroidNetworkService(Context act) {
        this.act = act;
        this.accessToken = utl.requireNotNull(BaseActivity.accessToken);
        GenericUserViewModel.getInstance().getUser().observeForever(new Observer<GenricUser>() {
            @Override
            public void onChanged(GenricUser u) {
                user = u;
            }
        });
        cacheUtil = CacheService.getInstance();
        try {
            appVersionCode = "" + BuildConfig.VERSION_CODE;
            firebaseAuthToken = BaseActivity.getFirebaseToken(true);
            if (utl.DEBUG_ENABLED) {
                utl.e("Network", " Firebase token " + firebaseAuthToken);
                utl.e("Network", " ProviderToken token " + providerToken);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    public String getBasicAuthHeader(){
        GenricUser user = GenericUserViewModel.getInstance().getUser().getValue();
        if(user!=null){
            authHeader = ""+user.getId()+":"+user.getPassword();
            authHeader = Base64.encodeToString(authHeader.getBytes(),Base64.DEFAULT).trim();
            authHeader = "Basic "+authHeader;
        }
        return authHeader;
    }

    public static String convertWithIteration(Map<String, Integer> map) {
        StringBuilder mapAsString = new StringBuilder("{");
        for (String key : map.keySet()) {
            mapAsString.append(key + " = " + map.get(key) + ",\n ");
        }
        mapAsString.delete(mapAsString.length() - 2, mapAsString.length()).append("}");
        return mapAsString.toString();
    }

    @Override
    public void updateTokens(String googleToken, String firebaseToken) {
        if (googleToken != null)
            providerToken = googleToken;
        if (firebaseToken != null)
            firebaseAuthToken = firebaseToken;
    }

    private void recordUse(String url, String body) {
        if (apiUsage == null)
            apiUsage = new HashMap<>();
        String data = url + "--";
//        if(body!=null){
//            data+=(utl.js.toJson(body));
//        }
        if (!apiUsage.containsKey(data)) {
            apiUsage.put(data, 1);
            return;
        }
        Integer count = apiUsage.get(data) + 1;
        apiUsage.put(data, count);
    }

    @Override
    public void callGetString(String url, final boolean showLoading, final NetworkRequestCallback call) {
        
        utl.e("CallGET", url);
        if (cacheUtil.getFromCache(url, call)) {
            return;
        }
        recordUse(url, null);
        AndroidNetworking.get(url)
                .addHeaders("accesstoken", accessToken)
                .addHeaders(authHeader==null?"dummy":"Authorization",""+getBasicAuthHeader())
                .addHeaders("firebasetoken", firebaseAuthToken)
                .addHeaders(Constants.KEY_PROVIDERTOKEN, providerToken)
                .addHeaders("version", appVersionCode)
                .addHeaders("userid", (user == null ? "" : user.getId()))
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                if (utl.DEBUG_ENABLED)
                    utl.e("CallGET Resp", response);

                cacheUtil.putIntoCache(url, response);
                call.onSuccessString(response);

                
            }

            @Override
            public void onError(ANError anError) {
                
                utl.e("CallGET", anError.getErrorDetail());
                utl.e("CallGET", anError.getErrorBody());
                call.onFail(anError);
            }
        });
    }

    @Override
    public void callGet(String url, final boolean showLoading, final NetworkRequestCallback call) {
        
        utl.e("CallGET", url);

        if (cacheUtil.getFromCache(url, call)) {
            return;
        }

        recordUse(url, null);
        AndroidNetworking.get(url)
                .addHeaders("accesstoken", accessToken)
                .addHeaders(authHeader==null?"dummy":"Authorization",""+getBasicAuthHeader())
                .addHeaders("firebasetoken", firebaseAuthToken)
                .addHeaders(Constants.KEY_PROVIDERTOKEN, providerToken)
                .addHeaders("version", appVersionCode)
                .addHeaders("userid", (user == null ? "" : user.getId()))
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                if (utl.DEBUG_ENABLED)
                    utl.e("CallGET Resp", response);

                try {
                    cacheUtil.putIntoCache(url, response);
                    call.onSuccess(new JSONObject(response));

                } catch (JSONException e) {
                    //utl.e("CallGet", "Error parsing Jsonobj , found JSOn array");
                    // e.printStackTrace();
                }
                call.onSuccessString(response);
                
            }

            @Override
            public void onError(ANError anError) {
                
                utl.e("CallGET", anError.getErrorDetail());
                utl.e("CallGET", anError.getErrorBody());
                call.onFail(anError);
            }
        });
    }

    @Override
    public void callPost(String url, final boolean showLoading, final NetworkRequestCallback call) {

        callPost(url, new JSONObject(), showLoading, call);

    }

    @Override
    public void callPost(String url, Object body, final boolean showLoading, final NetworkRequestCallback call) {

        try {
            callPost(url, new JSONObject(utl.js.toJson(body)), showLoading, call);
        } catch (JSONException e) {
            if (utl.DEBUG_ENABLED) e.printStackTrace();
        }

    }


    @Override
    public void callPostString(String url, JSONObject body, final boolean showLoading, final NetworkRequestCallback call) {

        


        if (utl.DEBUG_ENABLED) {
            utl.e("CallPost", url);
            utl.e("CallPost", body.toString());
        }

        if (cacheUtil.getFromCache(url, body, call)) {
            return;
        }


        recordUse(url, null);
        AndroidNetworking.post(url)
                .addHeaders("accesstoken", accessToken)
                .addHeaders(authHeader==null?"dummy":"Authorization",""+getBasicAuthHeader())
                .addHeaders("firebasetoken", firebaseAuthToken)
                .addHeaders(Constants.KEY_PROVIDERTOKEN, providerToken)
                .addHeaders("version", appVersionCode)
                .addHeaders("userid", (user == null ? "" : user.getId()))
                .addJSONObjectBody(body).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                if (utl.DEBUG_ENABLED)
                    utl.e("CallPost Resp", response);
                cacheUtil.putIntoCache(url, body, response);
                call.onSuccessString(response);

                
            }

            @Override
            public void onError(ANError anError) {
                
                utl.e("CallGET", anError.getErrorDetail());
                utl.e("CallGET", anError.getErrorBody());
                call.onFail(anError);
            }
        });
    }

    @Override
    public void callPost(String url, @NonNull JSONObject body, final boolean showLoading, final NetworkRequestCallback call) {

        


        if (utl.DEBUG_ENABLED) {
            utl.e("CallPost", url);
            utl.e("CallPost", body.toString());
        }


        if (cacheUtil.getFromCache(url, body, call)) {
            return;
        }

        recordUse(url, body.toString());
        AndroidNetworking.post(url)
                .addHeaders("accesstoken", accessToken)
                .addHeaders(authHeader==null?"dummy":"Authorization",""+getBasicAuthHeader())
                .addHeaders("firebasetoken", firebaseAuthToken)
                .addHeaders(Constants.KEY_PROVIDERTOKEN, providerToken)
                .addHeaders("version", appVersionCode)
                .addHeaders("userid", (user == null ? "" : user.getId()))
                .addJSONObjectBody(body).build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (utl.DEBUG_ENABLED)
                            utl.e("CallPost Resp", response);

                        try {
                            cacheUtil.putIntoCache(url, body, response);
                            call.onSuccess(new JSONObject(response));

                        } catch (JSONException e) {
                            // e.printStackTrace();
                        }
                        call.onSuccessString(response);

                        
                    }

                    @Override
                    public void onError(ANError anError) {
                        
                        utl.e("CallPost", anError.getErrorDetail());
                        utl.e("CallPost", anError.getErrorBody());
                        call.onFail(anError);
                    }
                });
    }


    public void uploadFile(String filename, File file, GenricDataCallback cb) {


        utl.e("CallPost Upload", file.length());
        AndroidNetworking.upload(Constants.HOST + Constants.API_UPLOAD_IMAGE)
                .addHeaders("accesstoken", accessToken)
                .addHeaders(authHeader==null?"dummy":"Authorization",""+getBasicAuthHeader())
                .addHeaders("firebasetoken", firebaseAuthToken)
                .addHeaders(Constants.KEY_PROVIDERTOKEN, providerToken)
                .addHeaders("version", appVersionCode)
                .addHeaders("userid", (user == null ? "" : user.getId()))
                .addMultipartFile("verifdoc", file)
                .addMultipartParameter("userid", user.getId())
                .addMultipartParameter("prefix", "" + filename)
                .addMultipartParameter("filename", "" + filename)
                .setTag("uploadTest")
                .setPriority(Priority.HIGH)
                .build()
                .setUploadProgressListener(new UploadProgressListener() {
                    @Override
                    public void onProgress(long bytesUploaded, long totalBytes) {
                        // do anything with progress
                    }
                })
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        utl.e(response.optString("url"));
                        if (response.optString("url").equals("")) {
                            cb.onStart(null, -1);
                            return;
                        }
                        cb.onStart(response.optString("url"), 1);

                    }

                    @Override
                    public void onError(ANError error) {
                        utl.toast(act, act.getString(R.string.upload_failed));
                        cb.onStart(null, -1);
                    }
                });

    }

    @Nullable
    @Override
    public void setAccessToken(String token) {
        accessToken = token;

    }

}

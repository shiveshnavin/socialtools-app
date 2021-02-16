package com.dotpot.app;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

import com.dotpot.app.services.DBService;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.dotpot.app.models.GenricUser;


/**
 * Created by shivesh on 2/8/18.
 */

public class App extends Application {

    public static GoogleSignInClient mGoogleApiClient;
    public static FirebaseRemoteConfig mFirebaseRemoteConfig;
    private static GenricUser userModel;
    public static Context mContext;

    public static GenricUser getGenricUser() {
        if(userModel==null)
        {
            userModel=utl.readUserData();
        }
        return userModel;
    }

    public static void setGenricUser(GenricUser userModel) {
        App.userModel = userModel;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        mContext=this;
        App.onUpgrade();

        utl.init(this);


        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings =
                new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.default_config);
        mFirebaseRemoteConfig.fetch().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //utl.e("mFirebaseRemoteConfig","ACTIVATED"+mFirebaseRemoteConfig.getAll());
                mFirebaseRemoteConfig.activate();
            }
        });

        Constants.HOST=mFirebaseRemoteConfig.getString("host");
        if(Constants.HOST.length() < 10){
            Constants.HOST="https://dotpot.herokuapp.com";
        }

        switchApp(BuildConfig.DEBUG);
        try {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void switchApp(boolean isDebugApk){

        if (isDebugApk) {

            Constants.HOST="https://dotpot.herokuapp.com";
        }else {
            Constants.HOST="https://dotpot.herokuapp.com";
        }
    }

    public static void onUpgrade()
    {

            utl.e("App","Upgrading to v202");
            DBService helper=DBService.getInstance(getAppContext());
            helper.onUpgrade(helper.getWritableDatabase(),BuildConfig.VERSION_CODE,301);

    }

    public static Context getAppContext()
    {
        return mContext;
    }

}

package com.dotpot.app.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.dotpot.app.R;
import com.dotpot.app.binding.GenericUserViewModel;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.services.LoginService;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigInfo;

public class SplashActivity extends BaseActivity {

    ImageView animLogo;
    private TextView head;
    private TextView subhead;
    private LinearLayout bottomContSplash;
    private VideoView videoView;
    private Button signup;
    private Button login;
    private LoginService loginService;

    private void findViews() {
        head = (TextView) findViewById(R.id.head);
        subhead = (TextView) findViewById(R.id.subhead);
        videoView = (VideoView) findViewById(R.id.videoView1);

        bottomContSplash = (LinearLayout) findViewById(R.id.bottomContSplash);
        signup = (Button) findViewById(R.id.signup);
        login = (Button) findViewById(R.id.request);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        findViews();
//        final MediaPlayer mp = MediaPlayer.create(this, R.raw.notif_tone);
        //mp.start();

        animLogo = findViewById(R.id.animLogo);
        animLogo.setVisibility(View.VISIBLE);
        utl.animate_avd(animLogo);
        updateFcm();
        String accessToken = utl.getKey("access_token", ctx);
        if (accessToken != null) {
            mFirebaseRemoteConfig.ensureInitialized().addOnCompleteListener(new OnCompleteListener<FirebaseRemoteConfigInfo>() {
                @Override
                public void onComplete(@NonNull Task<FirebaseRemoteConfigInfo> task) {
                    refreshAccessToken();
                    go();
                }
            });
        } else
            go();
    }

    private void go() {
        loginService = new LoginService(this);
        loginService.getLoggedInUser(new GenricObjectCallback<GenricUser>() {
            @Override
            public void onEntity(GenricUser data) {
                animateAndHome(data != null && data.validate());
            }

            @Override
            public void onError(String message) {

                if (!isNetworkAvailable() && utl.readUserData() != null) {
                    GenericUserViewModel.getInstance().getUser().postValue(utl.readUserData());
                    animateAndHome(true);
                } else
                  animateAndHome(false);
            }
        });
    }

    private void animateAndHome(boolean navToHomeAuto) {

        if (navToHomeAuto) {
            animLogo.postDelayed(() -> {
                inAppNavService.startHome();
                finish();
            }, 1000);
        } else {
            utl.logout();
            animLogo.postDelayed(this::showButtoms, 1000);
        }
    }

    private void showButtoms() {
        head.animate().setDuration(500).alpha(1.0f);
        subhead.animate().setDuration(500).alpha(1.0f);
        bottomContSplash.animate().setDuration(500).alpha(1.0f);
        signup.setOnClickListener(v -> {
            inAppNavService.startRegister();
            finish();
        });
        login.setOnClickListener(v -> {
            inAppNavService.startLogin();
            finish();
        });
    }


}
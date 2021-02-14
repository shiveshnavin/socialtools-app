package com.dotpot.app.ui.splash;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.dotpot.app.R;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.services.LoginService;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;

public class SplashActivity extends BaseActivity {

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
        login = (Button) findViewById(R.id.login);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        findViews();
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.notif_tone);
        mp.start();
        loginService = new LoginService(this);
        loginService.getLoggedInUser(new GenricObjectCallback<GenricUser>() {
            @Override
            public void onEntity(GenricUser data) {
                if (data != null && data.validate())
                    animateAndHome(true);
                else
                    animateAndHome(false);
            }

            @Override
            public void onError(String message) {
                animateAndHome(false);
            }
        });
    }

    private void animateAndHome(boolean navToHomeAuto) {
        ImageView animLogo = findViewById(R.id.animLogo);
        animLogo.setVisibility(View.VISIBLE);
        utl.animate_avd(animLogo);
        if (navToHomeAuto)
            animLogo.postDelayed(() -> inAppNavService.startHome(), 1000);
        else
        {
            utl.logout();
            animLogo.postDelayed(this::showButtoms, 1000);
        }
    }

    private void splashVideo() {
        //Creating MediaController
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);

        //specify the location of media file

//        Uri uri=Uri.parse(Environment.getExternalStorageDirectory().getPath()+"/raw/splashccr.mp4");
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splashccr);

        //Setting MediaController and URI, then starting the videoView
        mediaController.hide();
        mediaController.setVisibility(View.GONE);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        videoView.requestFocus();
        videoView.start();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                showButtoms();
            }
        });

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
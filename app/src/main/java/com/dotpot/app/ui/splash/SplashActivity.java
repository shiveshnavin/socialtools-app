package com.dotpot.app.ui.splash;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dotpot.app.R;
import com.dotpot.app.services.LoginService;
import com.dotpot.app.ui.BaseActivity;

public class SplashActivity extends BaseActivity {

    private TextView head;
    private TextView subhead;
    private LinearLayout bottomContSplash;
    private Button signup;
    private Button login;
    private LoginService loginService;

    private void findViews() {
        head = (TextView)findViewById( R.id.head );
        subhead = (TextView)findViewById( R.id.subhead );
        bottomContSplash = (LinearLayout)findViewById( R.id.bottomContSplash );
        signup = (Button)findViewById( R.id.signup );
        login = (Button)findViewById( R.id.login );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        findViews();
        loginService = new LoginService(this);
        if(loginService.getLoggedInUser()!=null){
            animateAndHome();
        }
        else {
            head.setVisibility(View.VISIBLE);
            subhead.setVisibility(View.VISIBLE);
            bottomContSplash.setVisibility(View.VISIBLE);
            signup.setOnClickListener(v->{
                inAppNavService.startRegister();
                finish();
            });
            login.setOnClickListener(v->{
                inAppNavService.startLogin();
                finish();
            });
        }
    }

    private void animateAndHome() {
        //todo animate
        inAppNavService.startHome();
        finish();

    }


}
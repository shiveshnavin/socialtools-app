package com.dotpot.app.ui;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.dotpot.app.R;
import com.dotpot.app.services.LoginService;
import com.dotpot.app.ui.login.LoginFragment;
import com.dotpot.app.ui.password.ChangePasswordFragment;
import com.dotpot.app.ui.signup.SignupFragment;
import com.dotpot.app.ui.verifyphone.VerifyPhoneFragment;

import static com.dotpot.app.Constants.ACTION_CHANGE_PASSWORD;
import static com.dotpot.app.Constants.ACTION_LOGIN;
import static com.dotpot.app.Constants.ACTION_SIGNUP;
import static com.dotpot.app.Constants.ACTION_VERIFY_PHONE;

public class AccountActivity extends BaseActivity {

    public LoginService loginService;

    private String action;
    private ImageView headImg;
    private TextView head;
    private TextView subtext;
    private LinearLayout contFooter;
    private TextView gotologin;
    private TextView gotologin2;

    private void findViews() {
        headImg = (ImageView) findViewById(R.id.head_img);
        head = (TextView) findViewById(R.id.head);
        subtext = (TextView) findViewById(R.id.subtext);
        contFooter = (LinearLayout) findViewById(R.id.cont_footer);
        gotologin = (TextView) findViewById(R.id.gotologin);
        gotologin2 = (TextView) findViewById(R.id.gotologin_2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        findViews();
        loginService = new LoginService(this);
        action = getIntent().getStringExtra("action");
        String fgmtName = "androidx.navigation.fragment.NavHostFragment";

        Fragment blank = null;
        if(fragmentManager.getFragments().size()>0)
        {
            blank = fragmentManager.getFragments().get(0);
        }

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();


        if (action == null || action.equals(ACTION_LOGIN)) {
            fgmtName = getString(R.string.login);
            beginLogin(false);
        } else if (action.equals(ACTION_SIGNUP)) {
            fgmtName = getString(R.string.signup);
            beginSignup(false);
        } else if (action.equals(ACTION_VERIFY_PHONE)) {
            fgmtName = getString(R.string.verifyphone);
            beginPhone(false);
        } else if (action.equals(ACTION_CHANGE_PASSWORD)) {
            fgmtName = getString(R.string.changepassword);
            beginChangePassword(false);
        }
        if(blank!=null)
            fragmentManager.beginTransaction().remove(blank).commitNow();

        //        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


    }

    public void beginLogin(boolean addToBackStack) {
        headImg.setImageResource(R.drawable.bg_login);
        inAppNavService.fragmentTransaction(R.id.nav_host_fragment, LoginFragment.class,
                getString(R.string.login),
                null,addToBackStack);
    }

    public void beginSignup(boolean addToBackStack) {
        headImg.setImageResource(R.drawable.bg_signup);
        inAppNavService.fragmentTransaction(R.id.nav_host_fragment, SignupFragment.class,
                getString(R.string.signup),
                null,addToBackStack);
    }

    public void beginPhone(boolean addToBackStack) {
        headImg.setImageResource(R.drawable.bg_signup);
        inAppNavService.fragmentTransaction(R.id.nav_host_fragment, VerifyPhoneFragment.class,
                getString(R.string.verifyphone),
                null,addToBackStack);
    }

    public void beginChangePassword(boolean addToBackStack ){
        inAppNavService.fragmentTransaction(R.id.nav_host_fragment, ChangePasswordFragment.class,
                getString(R.string.changepassword),
                null,addToBackStack);
    }



}
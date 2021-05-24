package com.dotpot.app.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.binding.GenericUserViewModel;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.services.LoginService;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.ui.fragments.ChangePasswordFragment;
import com.dotpot.app.ui.fragments.LoginFragment;
import com.dotpot.app.ui.fragments.SignupFragment;
import com.dotpot.app.ui.fragments.VerifyPhoneFragment;
import com.dotpot.app.utl;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import static com.dotpot.app.Constants.ACTION_ACCOUNT;
import static com.dotpot.app.Constants.ACTION_CHANGE_PASSWORD;
import static com.dotpot.app.Constants.ACTION_LOGIN;
import static com.dotpot.app.Constants.ACTION_SIGNUP;
import static com.dotpot.app.Constants.ACTION_VERIFY_PHONE;
import static com.dotpot.app.services.LoginService.RC_SIGN_IN;

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

//        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
//            }
//        }
//
//        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.REQUEST_INSTALL_PACKAGES) == PackageManager.PERMISSION_DENIED) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                requestPermissions(new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 1);
//            }
//        }
        loginService = new LoginService(this);
        action = getIntent().getStringExtra("action");
        String fgmtName = "androidx.navigation.fragment.NavHostFragment";

        Fragment blank = null;
        if (fragmentManager.getFragments().size() > 0) {
            blank = fragmentManager.getFragments().get(0);
        }

        if (utl.readUserData() != null) {
            loginService.setTempGenricUser(utl.readUserData());
        }
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (fragment != null)
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();


        if (action == null || action.equals(ACTION_LOGIN)) {
            fgmtName = getString(R.string.login);
            beginLogin(false);
        } else if (action.equals(ACTION_SIGNUP)) {

            loginService.googleLogin(LoginService.RC_SIGN_UP);

        } else if (action.equals(ACTION_ACCOUNT)) {
            fgmtName = getString(R.string.signup);
            beginSignup(false);
        } else if (action.equals(ACTION_VERIFY_PHONE)) {
            fgmtName = getString(R.string.verifyphone);
            beginPhone(false);
        } else if (action.equals(ACTION_CHANGE_PASSWORD)) {
            fgmtName = getString(R.string.changepassword);
            beginChangePassword(false);
        }
        if (blank != null)
            fragmentManager.beginTransaction().remove(blank).commitNow();
    }

    private void firebaseAuthWithGoogle(String idToken, GenricObjectCallback<FirebaseUser> fbUserCb) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        fbUserCb.onEntity(user);
                    } else {
                        fbUserCb.onError(task.getException().getMessage());
                        utl.snack(act, getString(R.string.error_msg) + task.getException().getMessage());
                    }
                });
    }


    public void handleSignInResult(GoogleSignInAccount account, int requestCode) {


        ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(getString(R.string.processing));
        progressDialog.show();
        firebaseAuthWithGoogle(account.getIdToken(), new GenricObjectCallback<FirebaseUser>() {
            @Override
            public void onEntity(FirebaseUser firebaseUser) {

                loginService.refreshProviderToken((token, data2) -> {

                    GenericUserViewModel.getInstance().getUser().setValue(null);
                    utl.removeUserData();
                    loginService.networkService.invalidateAllRuntimeValues();

                    loginService.firebaseId(token, new GenricObjectCallback<GenricUser>() {
                        @Override
                        public void onEntity(GenricUser genricUser) {
                            progressDialog.dismiss();
                            loginService.setTempGenricUser(genricUser);
                            GenericUserViewModel.getInstance().updateLocalAndNotify(act, genricUser);
                            if (requestCode == RC_SIGN_IN && genricUser.validate()) {
                                inAppNavService.startHome();
                            } else
                                beginSignup(false);

                        }

                        @Override
                        public void onError(String message) {

                            loginService.setTempGenricUser(new GenricUser());
                            loginService.getTempGenricUser().setType(Constants.userCategories[0]);
                            loginService.getTempGenricUser().setId(firebaseUser.getUid());
                            loginService.getTempGenricUser().setName(account.getDisplayName());
                            loginService.getTempGenricUser().setEmail(account.getEmail());
                            loginService.getTempGenricUser().setImage("" + account.getPhotoUrl());
                            loginService.commitTemporaryUserToServer(new GenricObjectCallback<GenricUser>() {
                                @Override
                                public void onEntity(GenricUser data) {
                                    progressDialog.dismiss();
                                    GenericUserViewModel.getInstance().updateLocalAndNotify(act, loginService.getTempGenricUser());
                                    beginSignup(false);
                                }

                                @Override
                                public void onError(String message) {
                                    progressDialog.dismiss();
                                    utl.snack(act, message);
                                }
                            });


                        }
                    });
                });

            }
        });

    }

    public void beginLogin(boolean addToBackStack) {
        headImg.setImageResource(R.drawable.bg_login);
        inAppNavService.fragmentTransaction(R.id.nav_host_fragment, LoginFragment.getInstance(),
                getString(R.string.login),
                null, addToBackStack);
        setuptxt();
    }

    public void beginSignup(boolean addToBackStack) {
        headImg.setImageResource(R.drawable.bg_signup);
        inAppNavService.fragmentTransaction(R.id.nav_host_fragment, SignupFragment.getInstance(),
                getString(R.string.signup),
                null, addToBackStack);
        setuptxt();
    }

    public void beginPhone(boolean addToBackStack) {

        headImg.setImageResource(R.drawable.bg_signup);
        inAppNavService.fragmentTransaction(R.id.nav_host_fragment, VerifyPhoneFragment.getInstance(),
                getString(R.string.verifyphone),
                null, addToBackStack);
        setuptxt();
    }

    public void beginChangePassword(boolean addToBackStack) {

        inAppNavService.fragmentTransaction(R.id.nav_host_fragment, ChangePasswordFragment.getInstance(),
                getString(R.string.changepassword),
                null, addToBackStack);
        setuptxt();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setuptxt();
    }

    private void setuptxt() {
        new Handler().postDelayed(() -> {
            Fragment f = fragmentManager.findFragmentById(R.id.nav_host_fragment);
            if (f instanceof LoginFragment) {
                head.setText(R.string.login);
                subtext.setText(R.string.please_login_to_continue);
                setupfooter(false);
                return;
            } else if (f instanceof SignupFragment) {
                head.setText(R.string.sign_up);
                subtext.setText(R.string.basic_info);
            } else if (f instanceof ChangePasswordFragment) {
                head.setText(R.string.changepassword);
                subtext.setText(R.string.basic_info);
            } else if (f instanceof VerifyPhoneFragment) {
                head.setText(R.string.verifyphone);
                subtext.setText(R.string.basic_info);
            }
            setupfooter(true);
        }, 500);
    }

    private void setupfooter(boolean isSignUp) {
        if (isSignUp) {
            contFooter.setVisibility(View.VISIBLE);
            gotologin.setText(R.string.already_have_a_account);
            gotologin2.setText(R.string.login);
            contFooter.setOnClickListener(view -> {
                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                utl.logout();
                beginLogin(false);
            });
        } else {
            contFooter.setVisibility(View.VISIBLE);
            gotologin.setText(R.string.don_t_have_a_account);
            gotologin2.setText(R.string.signup);
            contFooter.setOnClickListener(view -> {
                fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
//                beginSignup(false);
                loginService.googleLogin(LoginService.RC_SIGN_UP);
            });
        }
        if (action.equals(ACTION_CHANGE_PASSWORD) ||
                action.equals(ACTION_ACCOUNT) ||
                action.equals(ACTION_VERIFY_PHONE)) {
            contFooter.setVisibility(View.GONE);
            head.setText(R.string.edit_profile);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LoginService.RC_SIGN_UP || requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                handleSignInResult(account, requestCode);
            } catch (ApiException e) {
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
                utl.toast(act, getString(R.string.error_msg) + e.getMessage());
                startActivity(new Intent(ctx, SplashActivity.class));
                finishAffinity();
            }

        }
    }

}
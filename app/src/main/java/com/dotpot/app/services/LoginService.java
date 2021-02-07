package com.dotpot.app.services;

import android.content.Intent;

import com.dotpot.app.R;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.ui.AccountActivity;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utl;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class LoginService {

    private BaseActivity ctx;
    private InAppNavService inAppNavService;
    private GoogleSignInClient mGoogleSignInClient;
    public static int RC_SIGN_IN = 1001;
    private GenricUser tempGenricUser ;

    public LoginService(BaseActivity ctx) {
        this.ctx = ctx;
        inAppNavService = ctx.inAppNavService;
    }

    private GoogleSignInAccount getSignedInAccount(){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(ctx);
        return account;
    }

    public GenricUser getTempGenricUser() {
        return tempGenricUser;
    }

    public void setTempGenricUser(GenricUser tempGenricUser) {
        this.tempGenricUser = tempGenricUser;
    }

    public void googleLogin(){

        GoogleSignInAccount account = getSignedInAccount();

        if(account!=null){

            if(ctx instanceof AccountActivity){
                ((AccountActivity)ctx).handleSignInResult(account);
            }
            else {
                utl.toast(ctx, ResourceUtils.getString(R.string.error_msg));
            }
        }else {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestProfile()
                    .requestIdToken(ctx.mFirebaseRemoteConfig.getString("google_web_client_id"))
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(ctx, gso);
            mGoogleSignInClient = GoogleSignIn.getClient(ctx, gso);
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            ctx.startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }


    public void updatePassword(String newPassword,String oldPassword,GenricObjectCallback<GenricUser> cb){

        // todo perform api call
        cb.onEntity(null);
    }

    public void emailPhoneLogin(String emailPhone,String paswd,GenricObjectCallback<GenricUser> cb){

        // todo perform api call
        cb.onEntity(null);

    }

    public GenricUser getLoggedInUser() {
        return new GenricUser();
    }

}

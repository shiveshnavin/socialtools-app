package com.dotpot.app.services;

import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.ui.AccountActivity;
import com.dotpot.app.ui.BaseActivity;

public class LoginService {

    private BaseActivity ctx;
    private InAppNavService inAppNavService;

    public LoginService(BaseActivity ctx) {
        this.ctx = ctx;
        inAppNavService = ctx.inAppNavService;
    }

    public void googleLogin(AccountActivity act){
        act.beginSignup(true);
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
        return null;
    }
}

package com.dotpot.app.services;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentManager;

import com.dotpot.app.Constants;
import com.dotpot.app.ui.AccountActivity;
import com.dotpot.app.ui.BaseActivity;

public class InAppNavService {

    private BaseActivity ctx;
    private FragmentManager fragmentManager;

    public InAppNavService(BaseActivity ctx) {
        this.ctx = ctx;
        fragmentManager = ctx.fragmentManager;
    }

    private void startActivity(Intent it){
        ctx.startActivity(it);
    }

    public void startHome(){
        ctx.startHome();
    }

    public void startLogin(){

        Intent it=new Intent(ctx, AccountActivity.class);
        it.putExtra("action", Constants.ACTION_LOGIN);
        startActivity(it);

    }

    public void startRegister(){

        Intent it=new Intent(ctx, AccountActivity.class);
        it.putExtra("action", Constants.ACTION_SIGNUP);
        startActivity(it);

    }

    public void startVerifyPhone(){

        Intent it=new Intent(ctx, AccountActivity.class);
        it.putExtra("action", Constants.ACTION_VERIFY_PHONE);
        startActivity(it);
    }

    public void startChangePassword(){

        Intent it=new Intent(ctx, AccountActivity.class);
        it.putExtra("action", Constants.ACTION_CHANGE_PASSWORD);
        startActivity(it);
    }

    public void starMyAccount() {

    }

    public void fragmentTransaction(@IdRes int fragmentViewId, Class<? extends androidx.fragment.app.Fragment> target
            , String name, Bundle data){
        fragmentManager.beginTransaction()
                .replace(fragmentViewId, target, data)
                .setReorderingAllowed(true)
                .addToBackStack(name)
                .commit();
    }

}

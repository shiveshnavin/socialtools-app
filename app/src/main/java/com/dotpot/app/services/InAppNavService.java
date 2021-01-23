package com.dotpot.app.services;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dotpot.app.Constants;
import com.dotpot.app.R;
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
            , String name, Bundle data,boolean addToBackStack ){
//        .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left)
//        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(fragmentViewId, target, data)
                .setReorderingAllowed(true);

//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
//                .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_in_right,
//                        R.anim.slide_in_right, R.anim.slide_out_left)
//                .replace(fragmentViewId, target, data)
//                .setReorderingAllowed(true);

        if(addToBackStack){
            fragmentTransaction =  fragmentTransaction.addToBackStack(name);
        }else {
            fragmentTransaction =  fragmentTransaction
                    .disallowAddToBackStack();
        }
        fragmentTransaction.commit();

    }

}

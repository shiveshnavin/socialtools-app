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
import com.dotpot.app.ui.credit.AddCreditFragment;
import com.dotpot.app.ui.list.ViewListFragment;

public class InAppNavService {

    private BaseActivity ctx;
    private FragmentManager fragmentManager;

    public InAppNavService(BaseActivity ctx) {
        this.ctx = ctx;
        fragmentManager = ctx.fragmentManager;
    }
//
//    private static InAppNavService inAppNavService;
//    public static InAppNavService getInstance(BaseActivity ctx) {
//        if(inAppNavService==null)
//            inAppNavService  = new InAppNavService(ctx);
//        return inAppNavService;
//    }

    private void startActivity(Intent it) {
        ctx.startActivity(it);
    }

    public void startHome() {
        ctx.startHome();
    }

    public void startLogin() {

        Intent it = new Intent(ctx, AccountActivity.class);
        it.putExtra("action", Constants.ACTION_LOGIN);
        startActivity(it);

    }


    public void startChangePassword() {

        Intent it = new Intent(ctx, AccountActivity.class);
        it.putExtra("action", Constants.ACTION_CHANGE_PASSWORD);
        startActivity(it);
    }

    public void startRegister() {

        Intent it = new Intent(ctx, AccountActivity.class);
        it.putExtra("action", Constants.ACTION_SIGNUP);
        startActivity(it);

    }

    public void startVerifyPhone() {

        Intent it = new Intent(ctx, AccountActivity.class);
        it.putExtra("action", Constants.ACTION_VERIFY_PHONE);
        startActivity(it);
    }


    public void startAddCredits(@IdRes int fragmentViewId) {
        fragmentTransaction(fragmentViewId, AddCreditFragment.class, "credits", null, true, Constants.TRANSITION_HORIZONTAL);
    }

    public void startGameListPage(@IdRes int fragmentViewId) {
        fragmentTransaction(fragmentViewId, ViewListFragment.class, "games", null, true, Constants.TRANSITION_HORIZONTAL);
    }

    public void starMyAccount() {

    }

    public void fragmentTransaction(@IdRes int fragmentViewId, Class<? extends androidx.fragment.app.Fragment> target
            , String name, Bundle data, boolean addToBackStack) {

        fragmentTransaction(fragmentViewId, target, name, data, addToBackStack, Constants.TRANSITION_VERTICAL);

    }

    public void fragmentTransaction(@IdRes int fragmentViewId, Class<? extends androidx.fragment.app.Fragment> target
            , String name, Bundle data, boolean addToBackStack, int transition) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (transition == Constants.TRANSITION_HORIZONTAL)
            fragmentTransaction = fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        else if (transition == Constants.TRANSITION_VERTICAL)
            fragmentTransaction = fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom);


        fragmentTransaction.replace(fragmentViewId, target, data)
                .setReorderingAllowed(true);

        if (addToBackStack) {
            fragmentTransaction = fragmentTransaction.addToBackStack(name);
        } else {
            fragmentTransaction = fragmentTransaction
                    .disallowAddToBackStack();
        }
        fragmentTransaction.commit();

    }

}

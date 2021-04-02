package com.dotpot.app.services;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.models.Game;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.ui.BaseFragment;
import com.dotpot.app.ui.activities.AccountActivity;
import com.dotpot.app.ui.activities.GameActivity;
import com.dotpot.app.ui.fragments.AddCreditFragment;
import com.dotpot.app.ui.fragments.GameListFragment;
import com.dotpot.app.ui.fragments.ShopFragment;
import com.dotpot.app.utils.ObjectTransporter;

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

    public void startEdit() {

        Intent it = new Intent(ctx, AccountActivity.class);
        it.putExtra("action", Constants.ACTION_ACCOUNT);
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


    public void startSelectGameAmount(@IdRes int fragmentViewId, @Nullable Float amount) {
        Bundle bundle = new Bundle();
        if (amount != null)
            bundle.putFloat("amount", amount);
        bundle.putString("action", "select_game_amount");
        fragmentTransaction(fragmentViewId, AddCreditFragment.getInstance(), "credits", bundle, true, Constants.TRANSITION_HORIZONTAL);
    }

    public void startAddCredits(@IdRes int fragmentViewId) {
        fragmentTransaction(fragmentViewId, AddCreditFragment.getInstance(), "credits", null, true, Constants.TRANSITION_HORIZONTAL);
    }

    public void startGameListPage(@IdRes int fragmentViewId) {
        fragmentTransaction(fragmentViewId, GameListFragment.getInstance(), "games", null, true, Constants.TRANSITION_HORIZONTAL);
    }

    public void starMyAccount() {

        Intent it = new Intent(ctx, AccountActivity.class);
        it.putExtra("action", Constants.ACTION_ACCOUNT);
        startActivity(it);
    }


    public void startShop(@IdRes int fragmentViewId) {
        fragmentTransaction(fragmentViewId, ShopFragment.getInstance("shop"), "shop", null, true, Constants.TRANSITION_HORIZONTAL);
    }


    public void startUserShop(@IdRes int fragmentViewId) {
        fragmentTransaction(fragmentViewId, ShopFragment.getInstance("myshop"), "my shop", null, true, Constants.TRANSITION_HORIZONTAL);
    }

    public void startEarnShop(@IdRes int fragmentViewId) {
        fragmentTransaction(fragmentViewId, ShopFragment.getInstance("earn"), "earn", null, true, Constants.TRANSITION_HORIZONTAL);
    }

    public <T extends BaseFragment> void fragmentTransaction(@IdRes int fragmentViewId, T target
            , String name, Bundle data, boolean addToBackStack) {

        fragmentTransaction(fragmentViewId, target, name, data, addToBackStack, Constants.TRANSITION_VERTICAL);

    }

    public <T extends BaseFragment> void fragmentTransaction(@IdRes int fragmentViewId, T target
            , String name, Bundle data, boolean addToBackStack, int transition) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (transition == Constants.TRANSITION_HORIZONTAL)
            fragmentTransaction = fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        else if (transition == Constants.TRANSITION_VERTICAL)
            fragmentTransaction = fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom);

        target.setActivityAndContext(ctx);
        target.setArguments(data);
        fragmentTransaction.replace(fragmentViewId, target)
                .setReorderingAllowed(true);

        if (addToBackStack) {
            fragmentTransaction = fragmentTransaction.addToBackStack(name);
        } else {
            fragmentTransaction = fragmentTransaction
                    .disallowAddToBackStack();
        }
        fragmentTransaction.commit();

    }

    public void startGame(Game data) {

        ObjectTransporter.getInstance().put(data.getId(),data);
        Intent it = new Intent(ctx, GameActivity.class);
        it.putExtra("gameId",data.getId());
        startActivity(it);
    }
}

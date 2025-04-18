package com.igramassistant.app.services;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.igramassistant.app.Constants;
import com.igramassistant.app.R;
import com.igramassistant.app.models.Game;
import com.igramassistant.app.models.Product;
import com.igramassistant.app.ui.BaseActivity;
import com.igramassistant.app.ui.BaseFragment;
import com.igramassistant.app.ui.activities.AccountActivity;
import com.igramassistant.app.ui.activities.GameActivity;
import com.igramassistant.app.ui.activities.WebViewActivity;
import com.igramassistant.app.ui.fragments.AddCreditFragment;
import com.igramassistant.app.domain.igram.fragments.AutomatorFragment;
import com.igramassistant.app.ui.fragments.GameListFragment;
import com.igramassistant.app.ui.fragments.ShopDetailFragment;
import com.igramassistant.app.ui.fragments.ShopFragment;
import com.igramassistant.app.ui.fragments.WithdrawFragment;
import com.igramassistant.app.ui.messaging.MessagingFragment;
import com.igramassistant.app.utils.ObjectTransporter;

public class InAppNavService {

    private BaseActivity ctx;
    private FragmentManager fragmentManager;

    public InAppNavService(BaseActivity ctx) {
        this.ctx = ctx;
        fragmentManager = ctx.fragmentManager;
    }

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
        fragmentTransaction(fragmentViewId, AddCreditFragment.getInstance(), "credits", bundle, true, Constants.TRANSITION_HORIZONTAL,false);
    }

    public void startAddCredits(@IdRes int fragmentViewId) {
        fragmentTransaction(fragmentViewId, AddCreditFragment.getInstance(), "credits", null, true, Constants.TRANSITION_HORIZONTAL);
    }

    public void startGameListPage(@IdRes int fragmentViewId) {
        fragmentTransaction(fragmentViewId, GameListFragment.getInstance(), "games", null, true, Constants.TRANSITION_HORIZONTAL);
    }

    public void startSupport(@IdRes int fragmentViewId) {
        fragmentTransaction(fragmentViewId, MessagingFragment.getInstance(), "suport", null, true, Constants.TRANSITION_HORIZONTAL);
    }

    public void startWebsite(String title, String url) {
        Intent it = new Intent(ctx, WebViewActivity.class);
        it.putExtra("title", title);
        it.putExtra("url", url);
        ctx.startActivity(it);

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
        fragmentTransaction(fragmentViewId,target,name,data,addToBackStack,transition,true);
    }

    public <T extends BaseFragment> void fragmentTransaction(@IdRes int fragmentViewId, T target
            , String name, Bundle data, boolean addToBackStack, int transition, boolean replaceFragment) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (transition == Constants.TRANSITION_HORIZONTAL)
            fragmentTransaction = fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        else if (transition == Constants.TRANSITION_VERTICAL)
            fragmentTransaction = fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_top, R.anim.slide_out_bottom);

        target.setActivityAndContext(ctx);
        target.setArguments(data);
        if (replaceFragment)
            fragmentTransaction.replace(fragmentViewId, target)
                    .setReorderingAllowed(true);
        else
            fragmentTransaction.add(fragmentViewId, target)
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

        ObjectTransporter.getInstance().put(data.getId(), data);
        Intent it = new Intent(ctx, GameActivity.class);
        it.putExtra("gameId", data.getId());
        startActivity(it);
    }


    public void startWithdraw(@IdRes int fragmentViewId) {
        fragmentTransaction(fragmentViewId, WithdrawFragment.getInstance(), "withdraw", null, true, Constants.TRANSITION_HORIZONTAL);
    }

    public void startShopDetail(Product item, @IdRes int fragmentViewId) {
        fragmentTransaction(fragmentViewId, ShopDetailFragment.getInstance(item), "product", null, true, Constants.TRANSITION_HORIZONTAL,false);
    }

    public void startIgAutomation( @IdRes int fragmentViewId, String actionType) {
        fragmentTransaction(fragmentViewId, AutomatorFragment.getInstance(actionType), "product", null, true, Constants.TRANSITION_HORIZONTAL,false);
    }
}

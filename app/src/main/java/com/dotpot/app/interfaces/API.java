package com.dotpot.app.interfaces;


import com.dotpot.app.models.ActionItem;
import com.dotpot.app.models.Game;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.models.Transaction;
import com.dotpot.app.models.Wallet;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;

import org.json.JSONObject;

public interface API {

    void getGenricUser(String userId, GenricObjectCallback<GenricUser> cb);

    default void createTransaction(float amount, GenricObjectCallback<JSONObject> cb) {
        utl.e("Not Implemented API::createTransaction");
    }

    default void checkTransaction(String orderId, GenricObjectCallback<JSONObject> cb) {
        utl.e("Not Implemented API::createTransaction");
    }

    default void getWallet(GenricObjectCallback<Wallet> cb) {
        utl.e("Not Implemented API::getWallet");
    }

    default void getTransactions(String type, GenricObjectCallback<Transaction> cb) {
        utl.e("Not Implemented API::getTransactions");
    }

    void getActionItems(BaseActivity activity, GenricObjectCallback<ActionItem> cb);

    default void getLeaderBoard(GenricObjectCallback<GenricUser> cb) {
        utl.e("Not Implemented API::getLeaderBoard");
    }

    default void redeemReferral(String text, GenricDataCallback cb) {
        utl.e("Not Implemented API::redeemReferral");
    }

    default void getGameAmounts(GenricObjectCallback<Float> cb) {
        utl.e("Not Implemented API::getGameAmounts");
    }

    default void getUserGames(GenricObjectCallback<Game> cb) {
        utl.e("Not Implemented API::getUserGames");
    }

    default void getPayAmounts(GenricObjectCallback<Float> cb) {
        utl.e("Not Implemented API::getPayAmounts");
    }

    default void createGame(Float amount, GenricObjectCallback<Game> gameGenricObjectCallback) {
        utl.e("Not Implemented API::createGame");
    }


    default void finishGame(Game game, GenricObjectCallback<Game> gameGenricObjectCallback) {
        utl.e("Not Implemented API::finishGame");
    }

    default void invalidateCacheWalletAndTxns() {
        utl.e("Not Implemented API::invalidateCacheWalletAndTxns");
    }

    default void invalidateCacheGames() {
        utl.e("Not Implemented API::invalidateCacheGames");
    }

}
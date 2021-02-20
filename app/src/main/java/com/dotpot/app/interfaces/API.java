package com.dotpot.app.interfaces;


import com.dotpot.app.models.ActionItem;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.models.Transaction;
import com.dotpot.app.models.Wallet;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;

import org.json.JSONObject;

public interface API {

    default void getGenricUser(String userId, GenricObjectCallback<GenricUser> cb){
        utl.e("Not Implemented API::getGenricUser");
    };

    default void createTransaction(float amount,GenricObjectCallback<JSONObject> cb){
        utl.e("Not Implemented API::createTransaction");
    }

    default void checkTransaction(String orderId,GenricObjectCallback<JSONObject> cb){
        utl.e("Not Implemented API::createTransaction");
    }

    default void getWallet(GenricObjectCallback<Wallet> cb){
        utl.e("Not Implemented API::getWallet");
    };

    default void getTransactions(String type,GenricObjectCallback<Transaction> cb){
        utl.e("Not Implemented API::getTransactions");
    };

    default void getActionItems(BaseActivity activity, GenricObjectCallback<ActionItem> cb){
        utl.e("Not Implemented API::getActionItems");
    };

    default void getLeaderBoard(GenricObjectCallback<GenricUser> cb){
        utl.e("Not Implemented API::getLeaderBoard");
    };

    default void redeemReferral(String text, GenricDataCallback cb){
        utl.e("Not Implemented API::redeemReferral");
    };


}

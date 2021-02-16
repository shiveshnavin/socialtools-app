package com.dotpot.app.interfaces;


import com.dotpot.app.models.GenricUser;
import com.dotpot.app.models.Transaction;
import com.dotpot.app.models.Wallet;
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

}

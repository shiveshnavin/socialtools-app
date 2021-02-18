package com.dotpot.app.services;

import android.content.Context;

import com.androidnetworking.error.ANError;
import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.binding.GenericUserViewModel;
import com.dotpot.app.interfaces.API;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.interfaces.NetworkRequestCallback;
import com.dotpot.app.interfaces.NetworkService;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.models.Transaction;
import com.dotpot.app.models.Wallet;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;
import com.google.gson.Gson;

import org.json.JSONObject;


public class RestAPI implements API {

    Context ctx;
    NetworkService networkService;
    LocalAPIService localAPI;

    private static RestAPI instance;

    /***
     *
     * @param c Application Context {Context}
     * @return
     */
    public static RestAPI getInstance(Context c){
        if(instance==null)
            instance=new RestAPI(c);
        return instance;
    }

    private RestAPI(Context b)
    {
        this.ctx = b;
        networkService=AndroidNetworkService.getInstance(b);
        localAPI=new LocalAPIService(b);
    }

    @Override
    public void getGenricUser(String userId, GenricObjectCallback<GenricUser> cb) {
        cb.onEntity(utl.readUserData());
    }

    @Override
    public void createTransaction(float amount, GenricObjectCallback<JSONObject> cb) {

        JSONObject jop=new JSONObject();
        GenricUser user=GenericUserViewModel.getInstance().getUser().getValue();
        if(user==null){
            cb.onError(ctx.getString(R.string.err_pls_login));
            return;
        }
        try{
            jop.put("NAME",user.getName());
            jop.put("EMAIL",user.getEmail());
            jop.put("MOBILE_NO", user.getPhone());
            jop.put("PRODUCT_NAME", BaseActivity.mFirebaseRemoteConfig.getValue("PRODUCT_NAME"));
            jop.put("TXN_AMOUNT",amount);

        }catch(Exception e) {
            e.printStackTrace();
        }

        networkService.callPost(Constants.u(Constants.API_CREATE_TXN), jop, false, new NetworkRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if(response.has("payurl")){
                    cb.onEntity(response);
                }
                else {
                    cb.onError(ctx.getString(R.string.error_msg));
                }
            }

            @Override
            public void onFail(ANError job) {
                if(job.getErrorBody()!=null){
                    cb.onError(job.getErrorBody());
                }
                else {
                    cb.onError(job.getErrorDetail());
                }
            }
        });

    }


    @Override
    public void checkTransaction(String orderId, GenricObjectCallback<JSONObject> cb) {
        JSONObject jop=new JSONObject();

        try{
            jop.put("ORDER_ID",orderId);

        }catch(Exception e) {
            e.printStackTrace();
        }

        networkService.callPost(Constants.u(Constants.API_CHECK_TXN), jop, false, new NetworkRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if(response.has("status")){
                    cb.onEntity(response);
                }
                else {
                    cb.onError(ctx.getString(R.string.error_msg));
                }
            }

            @Override
            public void onFail(ANError job) {
                if(job.getErrorBody()!=null){
                    cb.onError(job.getErrorBody());
                }
                else {
                    cb.onError(job.getErrorDetail());
                }
            }
        });
    }


    @Override
    public void getWallet(GenricObjectCallback<Wallet> cb) {
        JSONObject jop=new JSONObject();
        GenricUser user=GenericUserViewModel.getInstance().getUser().getValue();
        if(user==null){
            cb.onError(ctx.getString(R.string.err_pls_login));
            return;
        }


        networkService.callGet(Constants.API_WALLET(user.getId()), false, new NetworkRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if(response.has("creditBalance")){
                    cb.onEntity(new Gson().fromJson(response.toString(),Wallet.class));
                }
                else {
                    cb.onError(ctx.getString(R.string.error_msg));
                }
            }

            @Override
            public void onFail(ANError job) {
                if(job.getErrorBody()!=null){
                    cb.onError(job.getErrorBody());
                }
                else {
                    cb.onError(job.getErrorDetail());
                }
            }
        });
    }

    @Override
    public void getTransactions(String debitOrCredit, GenricObjectCallback<Transaction> cb) {
        JSONObject jop=new JSONObject();
        GenricUser user=GenericUserViewModel.getInstance().getUser().getValue();
        if(user==null){
            cb.onError(ctx.getString(R.string.err_pls_login));
            return;
        }

        networkService.callGet(Constants.API_TRANSACTIONS(user.getId(),debitOrCredit),  false, new NetworkRequestCallback() {
            @Override
            public void onSuccessString(String response) {

                utl.JSONParser<Transaction> jsonParser = new utl.JSONParser<Transaction>();
                cb.onEntitySet(jsonParser.parseJSONArray(response,Transaction.class));

            }

            @Override
            public void onFail(ANError job) {
                if(job.getErrorBody()!=null){
                    cb.onError(job.getErrorBody());
                }
                else {
                    cb.onError(job.getErrorDetail());
                }
            }
        });
    }
}

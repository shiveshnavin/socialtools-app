package com.dotpot.app.services;

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

    BaseActivity act;
    NetworkService service;
    LocalAPIService localAPI;

    public RestAPI(BaseActivity b)
    {
        this.act = b;
        service=b.netService;
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
            cb.onError(act.getString(R.string.err_pls_login));
            return;
        }
        try{
            jop.put("NAME",user.getName());
            jop.put("EMAIL",user.getEmail());
            jop.put("MOBILE_NO", user.getPhone());
            jop.put("PRODUCT_NAME",act.mFirebaseRemoteConfig.getValue("PRODUCT_NAME"));
            jop.put("TXN_AMOUNT",amount);

        }catch(Exception e) {
            e.printStackTrace();
        }

        service.callPost(Constants.u(Constants.API_CREATE_TXN), jop, false, new NetworkRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if(response.has("payurl")){
                    cb.onEntity(response);
                }
                else {
                    cb.onError(act.getString(R.string.error_msg));
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

        service.callPost(Constants.u(Constants.API_CHECK_TXN), jop, false, new NetworkRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if(response.has("status")){
                    cb.onEntity(response);
                }
                else {
                    cb.onError(act.getString(R.string.error_msg));
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
            cb.onError(act.getString(R.string.err_pls_login));
            return;
        }


        service.callGet(Constants.API_WALLET(user.getId()), false, new NetworkRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                if(response.has("creditBalance")){
                    cb.onEntity(new Gson().fromJson(response.toString(),Wallet.class));
                }
                else {
                    cb.onError(act.getString(R.string.error_msg));
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
    public void getTransactions(String type, GenricObjectCallback<Transaction> cb) {
        JSONObject jop=new JSONObject();
        GenricUser user=GenericUserViewModel.getInstance().getUser().getValue();
        if(user==null){
            cb.onError(act.getString(R.string.err_pls_login));
            return;
        }

        service.callGet(Constants.API_TRANSACTIONS(user.getId(),type),  false, new NetworkRequestCallback() {
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

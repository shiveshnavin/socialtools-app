package com.dotpot.app.services;

import android.content.Context;

import com.androidnetworking.error.ANError;
import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.binding.GenericUserViewModel;
import com.dotpot.app.interfaces.API;
import com.dotpot.app.interfaces.GenricDataCallback;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.interfaces.NetworkRequestCallback;
import com.dotpot.app.interfaces.NetworkService;
import com.dotpot.app.models.ActionItem;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.models.Transaction;
import com.dotpot.app.models.Wallet;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utl;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;


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
    public static API getInstance(Context c){
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

    public static String getMessageFromANError(ANError job){
        if(job.getErrorBody()!=null){
            return (job.getErrorBody());
        }
        else {
            return (job.getErrorDetail());
        }
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

    @Override
    public void getActionItems(BaseActivity activity, GenricObjectCallback<ActionItem> cb) {

        ArrayList<ActionItem> actionItems = new ArrayList<>();

        ActionItem actionShowWalletBalance = new ActionItem();
        actionShowWalletBalance.actionType= ResourceUtils.getString(R.string.add_credits);
        actionShowWalletBalance.dateTime=System.currentTimeMillis();
        actionShowWalletBalance.id="balance";
        actionShowWalletBalance.dataId=Constants.BEHAVIOUR_SHOW_BALANCE;
        actionShowWalletBalance.actionType=Constants.ACTION_ADD_CREDITS;

        actionItems.add(actionShowWalletBalance);

        ActionItem actionShowRewards = new ActionItem();
        actionShowRewards.actionType=ResourceUtils.getString(R.string.redeem);
        actionShowRewards.dateTime=System.currentTimeMillis();
        actionShowRewards.id="redeem";
        actionShowRewards.dataId=Constants.BEHAVIOUR_SHOW_AWARDS;
        actionShowRewards.actionType=Constants.ACTION_REDEEM_OPTIONS;


        actionItems.stream().forEach(actionItem -> actionItem.act=activity);
        cb.onEntitySet(actionItems);

    }

    @Override
    public void getLeaderBoard(GenricObjectCallback<GenricUser> cb) {

        ArrayList<GenricUser> leaderList = new ArrayList<>();
        int N = 10;
        while (N-- > 0) {
            GenricUser leader = new GenricUser();
            leader.setRank(""+(10-N));
            leader.setId(utl.uid(10));
            leader.setName(utl.uid(6)+" "+utl.uid(4));
            leader.setAbout(""+utl.randomInt(3));
            leader.setImage("https://i.pravatar.cc/"+leader.getAbout());
            leaderList.add(leader);
        }

        cb.onEntitySet(leaderList);

    }


    @Override
    public void redeemReferral(String referralCode, GenricDataCallback cb) {

        JSONObject jop=new JSONObject();
        try{

            jop.put("referralCode",referralCode);

        }catch(Exception e)
        {}

        networkService.callPost(Constants.u(Constants.API_REDEEM_REFERRAL), jop, false, new NetworkRequestCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                cb.onStart(response.optString("message"),1);
            }

            @Override
            public void onFail(ANError job) {
                cb.onStart(getMessageFromANError(job),-1);
            }
        });


    }



}

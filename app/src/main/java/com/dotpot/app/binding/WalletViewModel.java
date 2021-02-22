package com.dotpot.app.binding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dotpot.app.App;
import com.dotpot.app.interfaces.API;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Transaction;
import com.dotpot.app.models.Wallet;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.utl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class WalletViewModel extends ViewModel {

    private static WalletViewModel instance;
    private static API restApi;
    private MutableLiveData<Wallet> userWalletLive;
    private MutableLiveData<List<Transaction>> userTransactionsLive;
    private MutableLiveData<ArrayList<Float>> payAmounts;

    public static WalletViewModel getInstance() {
        if (instance == null) {
            instance = new WalletViewModel();
            instance.userWalletLive = new MutableLiveData<>();
            instance.userTransactionsLive = new MutableLiveData<>();
            instance.payAmounts = new MutableLiveData<>();
            restApi = RestAPI.getInstance(App.getAppContext());
        }
        return instance;
    }

    public void refresh(@Nullable String debitOrCredit) {
        restApi.getWallet(new GenricObjectCallback<Wallet>() {
            @Override
            public void onEntity(Wallet wallet) {
                if (wallet != null)
                    userWalletLive.postValue(wallet);
            }

            @Override
            public void onError(String message) {
                utl.e("WalletViewModel.class", "Unable to refresh " + message);
            }
        });

        RestAPI.getInstance(App.getAppContext())
                .getPayAmounts(new GenricObjectCallback<Float>() {
                    @Override
                    public void onEntitySet(ArrayList<Float> listItems) {
                        payAmounts.postValue(listItems);
                    }

                    @Override
                    public void onError(String message) {
                        utl.e("WalletViewModel.class", "Unable to refresh " + message);
                    }
                });

        if (debitOrCredit != null && debitOrCredit.equals("skip"))
            return;

        restApi.getTransactions(debitOrCredit == null ? "" : debitOrCredit, new GenricObjectCallback<Transaction>() {
            @Override
            public void onEntitySet(ArrayList<Transaction> transactions) {
                if (transactions != null)
                    userTransactionsLive.postValue(transactions);
            }

            @Override
            public void onError(String message) {
                utl.e("WalletViewModel.class", "Unable to refresh " + message);
            }
        });
    }

    public LiveData<Wallet> getWallet() {
        return userWalletLive;
    }
    public LiveData<ArrayList<Float>> getPayAmounts() {
        return payAmounts;
    }
    public LiveData<List<Transaction>> getTransactions() {
        return userTransactionsLive;
    }
}
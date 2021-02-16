package com.dotpot.app.binding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Transaction;
import com.dotpot.app.models.Wallet;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;

import java.util.ArrayList;
import java.util.List;

public class WalletViewModel extends ViewModel {

    private static WalletViewModel instance;
    private MutableLiveData<Wallet> userWalletLive;
    private MutableLiveData<List<Transaction>> userTransactionsLive;

    public static WalletViewModel getInstance() {
        if (instance == null) {
            instance = new WalletViewModel();
            instance.userWalletLive = new MutableLiveData<>();
            instance.userTransactionsLive = new MutableLiveData<>();
        }
        return instance;
    }

    public void refresh(BaseActivity ctx) {
            ctx.restApi.getWallet(new GenricObjectCallback<Wallet>() {
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

        ctx.restApi.getTransactions("",new GenricObjectCallback<Transaction>() {
            @Override
            public void onEntitySet(ArrayList<Transaction> wallet) {
                if (wallet != null)
                    userTransactionsLive.postValue(wallet);
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

    public LiveData<List<Transaction>> getTransactions() {
        return userTransactionsLive;
    }
}
package com.dotpot.app.ui.wallet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dotpot.app.models.Transaction;

import java.util.ArrayList;
import java.util.List;

public class WalletViewModel extends ViewModel {

    private MutableLiveData<List<Transaction>> mText;

    public WalletViewModel() {
        mText = new MutableLiveData<>();
        List<Transaction> transactions = new ArrayList<>();
        int N=20;
        while (N -- > 0)
            transactions.add(new Transaction());
        mText.setValue(transactions);
    }

    public LiveData<List<Transaction>> getText() {
        return mText;
    }
}
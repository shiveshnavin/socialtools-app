package com.dotpot.app.ui.fragments;

import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dotpot.app.R;
import com.dotpot.app.binding.WalletViewModel;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Wallet;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.ui.BaseFragment;
import com.dotpot.app.ui.activities.HomeActivity;
import com.dotpot.app.utils.ShowHideLoader;
import com.dotpot.app.utl;
import com.dotpot.app.views.LoadingView;
import com.dotpot.app.views.RoundRectCornerImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class WithdrawFragment extends BaseFragment {

    private static WithdrawFragment mInstance;
    ShowHideLoader showHideLoader;
    private LinearLayout contLogin;
    private TextInputLayout contamount;
    private TextInputEditText amount;
    private TextView available;
    private TextView selectPayMethod;
    private ConstraintLayout contPaytm;
    private RadioButton paytmRadio;
    private TextView textPaytm;
    private RoundRectCornerImageView paytmImg;
    private TextInputLayout contPaytmNo;
    private TextInputEditText paytmNo;
    private ConstraintLayout contUpi;
    private RadioButton UpiRadio;
    private TextView textUpi;
    private RoundRectCornerImageView UpiImg;
    private TextInputLayout contUpiId;
    private TextInputEditText UpiIdNo;
    private TextView subtext;
    private LinearLayout linearLayout;
    private LoadingView loader;
    private Button request;

    public static WithdrawFragment getInstance() {
        if (mInstance == null)
            mInstance = new WithdrawFragment();
        return mInstance;
    }

    private void findViews(View root) {
        contLogin = (LinearLayout) root.findViewById(R.id.cont_login);
        contamount = (TextInputLayout) root.findViewById(R.id.contamount);
        amount = (TextInputEditText) root.findViewById(R.id.amount);
        available = (TextView) root.findViewById(R.id.available);
        selectPayMethod = (TextView) root.findViewById(R.id.selectPayMethod);
        contPaytm = (ConstraintLayout) root.findViewById(R.id.contPaytm);
        paytmRadio = (RadioButton) root.findViewById(R.id.paytmRadio);
        textPaytm = (TextView) root.findViewById(R.id.textPaytm);
        paytmImg = (RoundRectCornerImageView) root.findViewById(R.id.paytmImg);
        contPaytmNo = (TextInputLayout) root.findViewById(R.id.contPaytmNo);
        paytmNo = (TextInputEditText) root.findViewById(R.id.paytmNo);
        contUpi = (ConstraintLayout) root.findViewById(R.id.contUpi);
        UpiRadio = (RadioButton) root.findViewById(R.id.UpiRadio);
        textUpi = (TextView) root.findViewById(R.id.textUpi);
        UpiImg = (RoundRectCornerImageView) root.findViewById(R.id.UpiImg);
        contUpiId = (TextInputLayout) root.findViewById(R.id.contUpiId);
        UpiIdNo = (TextInputEditText) root.findViewById(R.id.UpiIdNo);
        subtext = (TextView) root.findViewById(R.id.subtext);
        linearLayout = (LinearLayout) root.findViewById(R.id.linearLayout);
        loader = (LoadingView) root.findViewById(R.id.loader);
        request = (Button) root.findViewById(R.id.request);
        showHideLoader = ShowHideLoader.create().content(request).loader(loader);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (HomeActivity) getActivity();
        init();

        View root = inflater.inflate(R.layout.fragment_withdraw, container, false);
        setUpToolbar(root);
        findViews(root);
        setTitle(getString(R.string.withdraw));

        root.findViewById(R.id.bgg).setOnClickListener(c->{

        });
        WalletViewModel.getInstance().getWallet().observe(getViewLifecycleOwner(), wallet -> {
            available.setText(String.format(getString(R.string.available_award_balance), getString(R.string.currency),""+wallet.getWinningBalance()));
        });

        String allowedPayments = FirebaseRemoteConfig.getInstance().getString("allowed_payment_methods");
        if(!allowedPayments.contains("paytm")){
            contPaytm.setVisibility(View.GONE);
        }
        if(!allowedPayments.contains("upi")){
            contUpi.setVisibility(View.GONE);
        }

        if(utl.getKey("paytm",ctx)!=null){
            paytmNo.setText(utl.getKey("paytm",ctx));
        }
        if(utl.getKey("upi",ctx)!=null){
            UpiIdNo.setText(utl.getKey("upi",ctx));
        }

        paytmRadio.setOnCheckedChangeListener((c, checked) -> {
            TransitionManager.beginDelayedTransition(contLogin);
            if (checked) {
                UpiRadio.setChecked(false);
                contPaytmNo.setVisibility(View.VISIBLE);
                contUpiId.setVisibility(View.GONE);

            }

        });

        UpiRadio.setOnCheckedChangeListener((c, checked) -> {
            TransitionManager.beginDelayedTransition(contLogin);
            if (checked) {
                paytmRadio.setChecked(false);
                contPaytmNo.setVisibility(View.GONE);
                contUpiId.setVisibility(View.VISIBLE);
            }
        });

        contPaytm.setOnClickListener(c->{
            paytmRadio.setChecked(true);
        });
        contUpi.setOnClickListener(c->{
            UpiRadio.setChecked(true);
        });


        request.setOnClickListener(c -> {

            String amts = amount.getText().toString();
            if(utl.isEmpty(amts)){
                contamount.setError(getString(R.string.invalidinput));
                return;
            }
            long amt = Long.parseLong(amount.getText().toString());
            long minWithdrawlAmount = FirebaseRemoteConfig.getInstance().getLong("min_withdrawl_amount");

            if(UpiRadio.isChecked()){
                if(utl.isEmpty(UpiIdNo.getText().toString())){
                    utl.snack(act, R.string.select_pay);
                    return;
                }
            }
            else
            if(paytmRadio.isChecked()){
                if(utl.isEmpty(paytmNo.getText().toString())){
                    utl.snack(act, R.string.select_pay);
                    return;
                }
            }
            else {
                utl.snack(act, R.string.select_pay);
                return;
            }

            if(amt < minWithdrawlAmount){
                contamount.setError(String.format(getString(R.string.min_withdrawl),""+ minWithdrawlAmount));
                return;
            }

            Wallet wallet = WalletViewModel.getInstance().getWallet().getValue();
           if(wallet !=null){
               if(wallet.getWinningBalance() > amt){
                   amount.setText("");
                   contamount.setError(null);
                   showHideLoader.loading();
                   RestAPI.getInstance().withdraw(paytmNo.getText().toString(),UpiIdNo.getText().toString(),amt, new GenricObjectCallback<String>() {
                       @Override
                       public void onEntity(String data) {
                           showHideLoader.loaded();
                           utl.diagInfo(request, data, getString(R.string.dismiss),
                                   paytmRadio.isChecked() ?
                                           R.drawable.ic_paytm:R.drawable.ic_upi
                                   , dialogInterface -> {

                                   });
                       }

                       @Override
                       public void onError(String message) {
                           showHideLoader.loaded();
                           utl.snack(act,message);
                       }
                   });
               }
               else {
                   contamount.setError(getString(R.string.insufficient_award));
               }
           }


        });

        return root;

    }

}
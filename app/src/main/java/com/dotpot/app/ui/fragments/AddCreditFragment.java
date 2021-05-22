package com.dotpot.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dotpot.app.R;
import com.dotpot.app.adapters.GenriXAdapter;
import com.dotpot.app.binding.GameViewModel;
import com.dotpot.app.binding.WalletViewModel;
import com.dotpot.app.interfaces.GenricCallback;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Game;
import com.dotpot.app.models.Wallet;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.ui.BaseFragment;
import com.dotpot.app.ui.activities.WebViewActivity;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utils.ShowHideLoader;
import com.dotpot.app.utl;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

public class AddCreditFragment extends BaseFragment {

    private GenriXAdapter<Float> adapter;
    private RecyclerView listTransactions;
    private View loader;

    private static AddCreditFragment mInstance;
    public static AddCreditFragment getInstance(){
        if(mInstance==null)
            mInstance = new AddCreditFragment();
        return mInstance;
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        act = (BaseActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_credit, container, false);
        listTransactions = root.findViewById(R.id.list);
        loader = root.findViewById(R.id.loader);

        setUpToolbar(root);

        go();

        return root;
    }
    private void go(){
        Bundle arguments = getArguments();
        String action = null;
        if (arguments != null) {
            action = arguments.getString("action");
        }
        if (action == null) {
            action = "select_pay_amount";
        }
        String finalAction = action;
        if (action.equals("select_game_amount")) {
            if (arguments.getFloat("amount") > 0) {
                checkWalletAndStartGame(arguments.getFloat("amount"), onDone,onInsuff);
            }
            setTitle(getString(R.string.select_game_credits));
            setUpAmounts(GameViewModel.getInstance().getGameAmounts().getValue(),finalAction);

        } else {

            setTitle(getString(R.string.add_credits));
            setUpAmounts(WalletViewModel.getInstance().getPayAmounts().getValue(),finalAction);
        }

    }

    private void setUpAmounts(List<Float> listData, final String action) {

        Collections.sort(listData);
        listData.remove(0f);
        if(action.equals("select_game_amount")){
           listData.add(0f);
        }
        adapter = new GenriXAdapter<Float>(getContext(), R.layout.row_credit, listData) {
            @Override
            public void onBindViewHolder(@NonNull GenriXAdapter.CustomViewHolder viewHolder, int i) {

                final int pos = viewHolder.getAdapterPosition();
                final CustomViewHolder vh = (CustomViewHolder) viewHolder;
                final Float amount = listData.get(pos);


                if(amount == 0f && action.equals("select_game_amount"))
                {
                    vh.textView(R.id.currency).setVisibility(View.GONE);
                    vh.textView(R.id.walletBalance).setText(R.string.practice);
                    vh.textView(R.id.yourWalletBalanceTxt).setText(R.string.brush_up_practice_txt);
                }
                else {
                    vh.textView(R.id.currency).setVisibility(View.VISIBLE);
                    vh.textView(R.id.walletBalance).setText(String.format("%s", amount));
                    vh.textView(R.id.yourWalletBalanceTxt).setText(String.format(getString(R.string.possible_rewards), getString(R.string.currency), ""+amount * 1.8));
                }

                if(action.equals("select_game_amount")){
                    vh.textView(R.id.addBtn).setText(R.string.start_game);
                    vh.textView(R.id.addBtn).setTextColor(act.getcolor(R.color.colorAccent));

                }
                else {
                    vh.textView(R.id.addBtn).setText(R.string.add_credits);
                    vh.textView(R.id.addBtn).setTextColor(act.getcolor(R.color.colorTextSuccess));

                }

                vh.itemView.setOnClickListener(view -> {

                    ShowHideLoader.create().content(listTransactions).loader(loader).loading();
                    if (action.equals("select_game_amount")) {
                        checkWalletAndStartGame(amount, onDone,onInsuff);
                    } else {
                        startCreditAdd(amount);
                    }

                });

            }
        };

        listTransactions.setLayoutManager(new GridLayoutManager(getContext(), 2));
        listTransactions.setAdapter(adapter);


    }


    GenricObjectCallback<Game> onDone = new GenricObjectCallback<Game>() {
        @Override
        public void onEntity(Game data) {

            new Handler(Looper.myLooper()).postDelayed(()->{
                ShowHideLoader.create().content(listTransactions).loader(loader).loaded();
            },1000);

            navService.startGame(data);
            if(data.getAmount()>0)
                WalletViewModel.getInstance().refresh("");

        }

        @Override
        public void onError(String message) {
            ShowHideLoader.create().content(listTransactions).loader(loader).loaded();
            utl.diagBottom(ctx, getString(R.string.error), message, R.drawable.error);
        }
    };

    GenricObjectCallback<Game> onInsuff = new GenricObjectCallback<Game>() {
        @Override
        public void onError(String message) {
            ShowHideLoader.create().content(listTransactions).loader(loader).loaded();
            utl.diagBottom(ctx, getString(R.string.insufficient_credits_header),
                    getString(R.string.insufficient_credits), true, getString(R.string.add_credits), new GenricCallback() {
                        @Override
                        public void onStart() {
                            navService.startAddCredits(fragmentId);
                        }
                    });
        }
    };

    public static void checkWalletAndStartGame(Float amount,
                                                GenricObjectCallback<Game> onDone,
                                                GenricObjectCallback<Game> onNoBalance) {

        Wallet data = WalletViewModel.getInstance().getWallet().getValue();

        if (data == null || data.getCreditBalance() >= amount) {
            RestAPI.getInstance().createGame(amount,onDone);
        } else {
            onNoBalance.onError( ResourceUtils.getString(R.string.insufficient_credits_header));
        }
    }


    private void startCreditAdd(Float amount) {
        RestAPI.getInstance().createTransaction(amount, new GenricObjectCallback<JSONObject>() {
            @Override
            public void onEntity(JSONObject response) {

                new Handler(Looper.myLooper()).postDelayed(()->{
                    ShowHideLoader.create().content(listTransactions).loader(loader).loaded();
                },1000);

                Intent it = new Intent(ctx, WebViewActivity.class);
                it.putExtra("title", getString(R.string.pay));
                it.putExtra("orderId", response.optString("orderId"));
                it.putExtra("url", response.optString("payurl"));
                getActivity().startActivityForResult(it, WebViewActivity.REQUEST_PAYMENT);

            }

            @Override
            public void onError(String message) {
                ShowHideLoader.create().content(listTransactions).loader(loader).loaded();
                utl.diagBottom(ctx, getString(R.string.error), message, R.drawable.error);
            }
        });

    }

}
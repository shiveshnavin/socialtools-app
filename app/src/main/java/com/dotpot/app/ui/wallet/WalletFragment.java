package com.dotpot.app.ui.wallet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dotpot.app.R;
import com.dotpot.app.adapters.GenriXAdapter;
import com.dotpot.app.binding.WalletViewModel;
import com.dotpot.app.models.Transaction;
import com.dotpot.app.models.Wallet;
import com.dotpot.app.ui.BaseFragment;
import com.dotpot.app.utl;
import com.dotpot.app.views.RoundRectCornerImageView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class WalletFragment extends BaseFragment {

    private WalletViewModel walletViewModel;
    private GenriXAdapter<Transaction> adapter;

    private ConstraintLayout contWalletBalancecont;
    private LinearLayout contWalletBalance;
    private TextView currency;
    private TextView walletBalance;
    private TextView yourWalletBalanceTxt;
    private View yourWalletBalanceSep;
    private ConstraintLayout contaddMoneyBalance;
    private TextView addMoneyBal;
    private TextView addMoneyBalTxt;
    private RoundRectCornerImageView addMoneyBalIcon;
    private RoundRectCornerImageView next;
    private Button addBtn;
    private View withdrawBtn;
    private LinearLayout contNumbers;
    private ConstraintLayout contAwardBalance;
    private Button awardBalAction;
    private TextView awardBalTxt;
    private TextView awardBal;
    private RoundRectCornerImageView awardBalIcon;
    private ConstraintLayout contRefBalance;
    private TextView refBal;
    private TextView refBalTxt;
    private RoundRectCornerImageView refBalIcon;
    private TextView recentTxnTxt;
    private TabLayout tabTxns;
    private TabItem tabAll;
    private TabItem tabCredit;
    private TabItem tabDebit;
    private RecyclerView listTransactions;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2021-01-21 02:22:51 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews(View root) {
        contWalletBalancecont = (ConstraintLayout)root.findViewById( R.id.contWalletBalancecont );
        contWalletBalance = (LinearLayout)root.findViewById( R.id.contWalletBalance );
        currency = (TextView)root.findViewById( R.id.currency );
        walletBalance = (TextView)root.findViewById( R.id.walletBalance );
        yourWalletBalanceTxt = (TextView)root.findViewById( R.id.yourWalletBalanceTxt );
        yourWalletBalanceSep = (View)root.findViewById( R.id.yourWalletBalanceSep );
        contaddMoneyBalance = (ConstraintLayout)root.findViewById( R.id.contaddMoneyBalance );
        addMoneyBal = (TextView)root.findViewById( R.id.addMoneyBal );
        addMoneyBalTxt = (TextView)root.findViewById( R.id.addMoneyBalTxt );
        addMoneyBalIcon = (RoundRectCornerImageView)root.findViewById( R.id.addMoneyBalIcon );
        next = (RoundRectCornerImageView)root.findViewById( R.id.next );
        addBtn = (Button)root.findViewById( R.id.addBtn );
        withdrawBtn =  root.findViewById( R.id.withdrawBtn );
        contNumbers = (LinearLayout)root.findViewById( R.id.contNumbers );
        contAwardBalance = (ConstraintLayout)root.findViewById( R.id.editProfileCont);
        awardBalAction = (Button)root.findViewById( R.id.awardBalAction );
        awardBalTxt = (TextView)root.findViewById( R.id.awardBalTxt );
        awardBal = (TextView)root.findViewById( R.id.editProfileTxt);
        awardBalIcon = (RoundRectCornerImageView)root.findViewById( R.id.awardBalIcon );
        contRefBalance = (ConstraintLayout)root.findViewById( R.id.contRefBalance );
        refBal = (TextView)root.findViewById( R.id.refBal );
        refBalTxt = (TextView)root.findViewById( R.id.refBalTxt );
        refBalIcon = (RoundRectCornerImageView)root.findViewById( R.id.refBalIcon );
        recentTxnTxt = (TextView)root.findViewById( R.id.recentTxnTxt );
        tabTxns = (TabLayout)root.findViewById( R.id.tabTxns );
        tabAll = (TabItem)root.findViewById( R.id.tab_all );
        tabCredit = (TabItem)root.findViewById( R.id.tab_credit );
        tabDebit = (TabItem)root.findViewById( R.id.tab_debit );
        listTransactions = (RecyclerView)root.findViewById( R.id.listTransactions );
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        walletViewModel = WalletViewModel.getInstance();
        View root = inflater.inflate(R.layout.fragment_wallet, container, false);
        findViews(root);
        try {
            if (act.fragmentManager.getBackStackEntryCount() > 0)
            {
                act.fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        } catch (Exception e) {
            utl.e("Minor err at WalletFragment:115");
           // e.printStackTrace();
        }

        walletViewModel.getTransactions().observe(getViewLifecycleOwner(), this::setUpTransactionsList);
        walletViewModel.getWallet().observe(getViewLifecycleOwner(), this::setUpWallet);
        addBtn.setOnClickListener(view -> navService.startAddCredits(fragmentId));

        tabAll.setOnClickListener(v->{
            walletViewModel.refresh(null);
        });
        tabCredit.setOnClickListener(v->{
            walletViewModel.refresh("wallet_credit");
        });
        tabDebit.setOnClickListener(v->{
            walletViewModel.refresh("wallet_debit");
        });

        return root;
    }

    private void setUpWallet(Wallet wallet) {

        walletBalance.setText(""+wallet.getCreditBalance());
        awardBal.setText(getString(R.string.award_bal)+getString(R.string.currency)+" "+wallet.getWinningBalance());
        awardBal.setText(getString(R.string.ref_bal)+getString(R.string.currency)+" "+wallet.getAggReferralBalance());

    }

    private void setUpTransactionsList(List<Transaction> transactionsList){

        adapter = new GenriXAdapter<Transaction>(getContext(),R.layout.row_transaction,transactionsList){
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int pos = viewHolder.getAdapterPosition();
                final CustomViewHolder vh = (CustomViewHolder)viewHolder;
                final Transaction transaction = transactionsList.get(pos);
                vh.textView(R.id.txnId).setText("ID #"+transaction.getId());
                vh.textView(R.id.txnAmtTxt).setText(getString(R.string.currency)+" "+transaction.getAmount());
                vh.textView(R.id.txnStatusTxt).setText(transaction.getStatus());

            }
        };

        listTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        listTransactions.setAdapter(adapter);


    }

}
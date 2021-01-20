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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dotpot.app.R;
import com.dotpot.app.adapters.GenriXAdapter;
import com.dotpot.app.models.Transaction;
import com.dotpot.app.utl;
import com.dotpot.app.views.RoundRectCornerImageView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class WalletFragment extends Fragment {

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
        contAwardBalance = (ConstraintLayout)root.findViewById( R.id.contAwardBalance );
        awardBalAction = (Button)root.findViewById( R.id.awardBalAction );
        awardBalTxt = (TextView)root.findViewById( R.id.awardBalTxt );
        awardBal = (TextView)root.findViewById( R.id.awardBal );
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
        walletViewModel =
                new ViewModelProvider(this).get(WalletViewModel.class);
        View root = inflater.inflate(R.layout.fragment_wallet, container, false);
        findViews(root);
        walletViewModel.getText().observe(getViewLifecycleOwner(), this::setUpTransactionsList);
        return root;
    }

    private void setUpTransactionsList(List<Transaction> transactionsList){

        adapter = new GenriXAdapter<Transaction>(getContext(),R.layout.row_transaction,transactionsList){
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int pos = viewHolder.getAdapterPosition();
                final CustomViewHolder vh = (CustomViewHolder)viewHolder;
                final Transaction transaction = transactionsList.get(pos);
                vh.textView(R.id.txnId).setText("ID #"+transaction.getId());
            }
        };

        listTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        listTransactions.setAdapter(adapter);


    }

}
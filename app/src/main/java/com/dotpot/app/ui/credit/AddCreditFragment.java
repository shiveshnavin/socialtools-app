package com.dotpot.app.ui.credit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dotpot.app.R;
import com.dotpot.app.adapters.GenriXAdapter;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.GenericItem;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.ui.BaseFragment;
import com.dotpot.app.ui.WebViewActivity;
import com.dotpot.app.utl;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddCreditFragment extends BaseFragment {

    private GenriXAdapter<GenericItem> adapter;
    private RecyclerView listTransactions;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        act = (BaseActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_credit, container, false);
        listTransactions=root.findViewById(R.id.list);
        setUpToolbar(root);
        setTitle(getString(R.string.add_credits));
        List<GenericItem> items=new ArrayList<>();
        items.add(new GenericItem());
        items.add(new GenericItem());
        items.add(new GenericItem());
        items.add(new GenericItem());
        items.add(new GenericItem());
        setUpTransactionsList(items);
        return root;
    }

    private void setUpTransactionsList(List<GenericItem> listData){

        adapter = new GenriXAdapter<GenericItem>(getContext(),R.layout.row_credit,listData){
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int pos = viewHolder.getAdapterPosition();
                final CustomViewHolder vh = (CustomViewHolder)viewHolder;
                final GenericItem item = listData.get(pos);
                final int amount = ((2+pos)*100);
                vh.textView(R.id.walletBalance).setText(""+amount);
                vh.textView(R.id.yourWalletBalanceTxt).setText("Possible rewards upto "+getString(R.string.currency)+" "+(amount * 1.8));
                vh.itemView.setOnClickListener(view -> {


                    act.restApi.createTransaction(amount, new GenricObjectCallback<JSONObject>() {
                        @Override
                        public void onEntity(JSONObject response) {

                            Intent it = new Intent(ctx,WebViewActivity.class);
                            it.putExtra("title",getString(R.string.pay));
                            it.putExtra("orderId",response.optString("orderId"));
                            it.putExtra("url",response.optString("payurl"));
                            startActivityForResult(it,WebViewActivity.REQUEST_PAYMENT);

                        }

                        @Override
                        public void onError(String message) {
                            utl.diagBottom(ctx,getString(R.string.error),message,R.drawable.error);
                        }
                    });


                });

            }
        };

        listTransactions.setLayoutManager(new GridLayoutManager(getContext(),2));
        listTransactions.setAdapter(adapter);


    }
}
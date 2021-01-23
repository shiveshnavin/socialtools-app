package com.dotpot.app.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dotpot.app.R;
import com.dotpot.app.adapters.GenriXAdapter;
import com.dotpot.app.models.GenericItem;
import com.dotpot.app.ui.AccountActivity;
import com.dotpot.app.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewListFragment<T extends GenericItem> extends BaseFragment {

    private GenriXAdapter<T> adapter;
    private RecyclerView listTransactions;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        act = (AccountActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        listTransactions=root.findViewById(R.id.list);

        List<GenericItem> items=new ArrayList<>();
        items.add(new GenericItem());
        items.add(new GenericItem());
        items.add(new GenericItem());
        items.add(new GenericItem());
        items.add(new GenericItem());

        return root;
    }

    private void setUpTransactionsList(List<T> listData){

        adapter = new GenriXAdapter<T>(getContext(),R.layout.row_transaction,listData){
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int pos = viewHolder.getAdapterPosition();
                final GenriXAdapter.CustomViewHolder vh = (CustomViewHolder)viewHolder;
                final T item = listData.get(pos);
                vh.textView(R.id.txnId).setText("ID #"+item.getId());
            }
        };

        listTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        listTransactions.setAdapter(adapter);


    }
}
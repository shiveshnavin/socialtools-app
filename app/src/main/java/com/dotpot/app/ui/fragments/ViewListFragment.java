package com.dotpot.app.ui.fragments;

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
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class ViewListFragment<T extends GenericItem> extends BaseFragment {

    private GenriXAdapter<GenericItem> adapter;
    private RecyclerView listTransactions;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        act = (BaseActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        listTransactions=root.findViewById(R.id.list);
        setUpToolbar(root);
        setTitle(getString(R.string.mygames));
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

        adapter = new GenriXAdapter<GenericItem>(getContext(),R.layout.row_style_shop,listData){
            @Override
            public void onBindViewHolder(@NonNull GenriXAdapter.CustomViewHolder viewHolder, int i) {

                final int pos = viewHolder.getAdapterPosition();
                final GenriXAdapter.CustomViewHolder vh = (CustomViewHolder)viewHolder;
                final GenericItem item = listData.get(pos);
            }
        };

        listTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        listTransactions.setAdapter(adapter);


    }
}
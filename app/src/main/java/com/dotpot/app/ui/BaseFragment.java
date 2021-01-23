package com.dotpot.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dotpot.app.R;
import com.dotpot.app.services.InAppNavService;

public class BaseFragment extends Fragment {

    public BaseActivity act;
    public Context ctx;
    public InAppNavService navService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        act = (BaseActivity) getActivity();
        ctx = getContext();
        navService = new InAppNavService(act);
        View root = inflater.inflate(R.layout.fragment_blank, container, false);
        return root;
    }
}

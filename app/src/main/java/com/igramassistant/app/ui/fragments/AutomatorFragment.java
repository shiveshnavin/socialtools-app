package com.igramassistant.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import com.igramassistant.app.R;
import com.igramassistant.app.binding.WalletViewModel;
import com.igramassistant.app.interfaces.GenricObjectCallback;
import com.igramassistant.app.models.Product;
import com.igramassistant.app.services.RestAPI;
import com.igramassistant.app.ui.BaseFragment;
import com.igramassistant.app.ui.activities.HomeActivity;
import com.igramassistant.app.utils.ShowHideLoader;
import com.igramassistant.app.utl;
import com.igramassistant.app.views.AdvancedWebView;
import com.igramassistant.app.views.LoadingView;
import com.igramassistant.app.views.RoundRectCornerImageView;
import com.squareup.picasso.Picasso;

public class AutomatorFragment extends BaseFragment {

    ShowHideLoader showHideLoader;
    private String actionType;

    public static AutomatorFragment getInstance(String actionType) {
        AutomatorFragment mInstance = new AutomatorFragment();
        mInstance.actionType=actionType;
        return mInstance;
    }

     private NestedScrollView contScroll;
    private LinearLayout contLogin;
    private AdvancedWebView webview;
    private LinearLayout linearLayout;
    private Button request;
    private LoadingView loader;

    private void findViews(View root) {
         contScroll = (NestedScrollView)root.findViewById( R.id.contScroll );
        contLogin = (LinearLayout)root.findViewById( R.id.cont_login );
        webview = (AdvancedWebView)root.findViewById( R.id.webview );
        linearLayout = (LinearLayout)root.findViewById( R.id.linearLayout );
        request = (Button)root.findViewById( R.id.request );
        loader = (LoadingView)root.findViewById( R.id.loader );
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (HomeActivity) getActivity();
        init();

        View root = inflater.inflate(R.layout.fragment_automator, container, false);
        setUpToolbar(root);
        findViews(root);
        setTitle(getString(R.string.automator));

        root.findViewById(R.id.bgg).setOnClickListener(c->{

        });

        prepare("https://instagram.com");
        request.setOnClickListener(c->{
            start("https://instagram.com");
        });

        return root;

    }

    public void prepare(String base){
        start(base);
    }

    public void start(String base){
        webview.loadUrl(base);
    }

}
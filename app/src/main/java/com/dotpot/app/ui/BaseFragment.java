package com.dotpot.app.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.dotpot.app.R;
import com.dotpot.app.services.InAppNavService;

public class BaseFragment extends Fragment {

    public BaseActivity act;
    public Context ctx;
    public InAppNavService navService;
    public int fragmentId;

    public TextView title ;
    public ImageView logo;

    public void setUpToolbar(View root){
        title = root.findViewById(R.id.title);
        logo = root.findViewById(R.id.logo);
        if(logo!=null)
        {
            logo.setOnClickListener(v->{
                getActivity().onBackPressed();
            });
        }
    }


    public void setTitle(String titl){
        if(title!=null)
            title.setText(titl);
    }

    public void init(){
        act = (BaseActivity) getActivity();
        ctx = getContext();
        navService = new InAppNavService(act);
        try{
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        init();
        fragmentId = container.getId();

        View root = inflater.inflate(R.layout.fragment_blank, container, false);
        return root;
    }

    public void setActivityAndContext(BaseActivity act) {
        this.act = act;
        this.ctx = ctx;
        this.navService = act.inAppNavService;
    }
}

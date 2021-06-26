package com.igramassistant.app.ui.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.igramassistant.app.Constants;
import com.igramassistant.app.R;
import com.igramassistant.app.interfaces.GenricObjectCallback;
import com.igramassistant.app.models.GenricUser;
import com.igramassistant.app.ui.BaseFragment;
import com.igramassistant.app.ui.activities.AccountActivity;
import com.igramassistant.app.utl;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordFragment extends BaseFragment {

    private static ChangePasswordFragment mInstance;
    public static ChangePasswordFragment getInstance(){
        if(mInstance==null)
            mInstance = new ChangePasswordFragment();
        return mInstance;
    }
    private AccountActivity act ;
    private LinearLayout contLogin;
    private TextInputLayout contentpaswd;
    private TextInputEditText password;
    private TextInputLayout contentpaswd2;
    private TextInputEditText password2;
    private LinearLayout linearLayout;
    private Button login;
    private TextView subtext;

    private void findViews(View root) {
        contLogin = (LinearLayout)root.findViewById( R.id.cont_login );
        contentpaswd = (TextInputLayout)root.findViewById( R.id.contentpaswd );
        password = (TextInputEditText)root.findViewById( R.id.password );
        contentpaswd2 = (TextInputLayout)root.findViewById( R.id.contentpaswd2 );
        password2 = (TextInputEditText)root.findViewById( R.id.password2 );
        linearLayout = (LinearLayout)root.findViewById( R.id.linearLayout );
        login = (Button)root.findViewById( R.id.request);
        subtext = (TextView)root.findViewById( R.id.subtext );
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (AccountActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_paswd, container, false);
        findViews(root);
        String textBtm = subtext.getText().toString();
        subtext.setText(Html.fromHtml(textBtm));
        subtext.setOnClickListener(v->{
            act.inAppNavService.startWebsite(act.getString(R.string.terms), Constants.HOST+"/toc.html");
        });

        if(utl.isEmpty(act.loginService.getTempGenricUser().getPassword())){
            contentpaswd.setVisibility(View.GONE);
        }
        login.setOnClickListener(v->{
            String oldPasswd = password.getText().toString();
            String newPasswd = password2.getText().toString();
            if(oldPasswd.isEmpty() && contentpaswd.getVisibility()==View.VISIBLE){
                contentpaswd.setError(getString(R.string.invalidinput));
                return;
            }
            else {
                contentpaswd.setError(null);
            }

            if(newPasswd.isEmpty()){
                contentpaswd2.setError(getString(R.string.invalidinput));
                return;
            }
            else {
                contentpaswd2.setError(null);
            }
            login.setText(R.string.processing);
            String fcmToken = utl.getKey("fcm_token",act);
            if(fcmToken!=null){
                act.loginService.getTempGenricUser().setFcmToken(fcmToken);
            }
            act.loginService.commitPasswordAndPhone(
                    oldPasswd
                    ,newPasswd
                    ,null
                    , new GenricObjectCallback<GenricUser>() {
                        @Override
                        public void onEntity(GenricUser data) {
                            act.inAppNavService.startHome();
//                            act.finishAffinity();
                        }

                        @Override
                        public void onError(String message) {
                            login.setText(R.string.finish);
                            contentpaswd.setError(getString(R.string.invalidinput)+" "+message);
                            contentpaswd2.setError(getString(R.string.invalidinput)+" "+message);
                        }
                    });
        });

        return root;
    }
}
package com.dotpot.app.ui.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.dotpot.app.R;
import com.dotpot.app.ui.AccountActivity;
import com.dotpot.app.ui.BaseFragment;
import com.dotpot.app.utl;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignupFragment extends BaseFragment {

    private AccountActivity act ;
    private LinearLayout contLogin;
    private TextInputLayout contentmail;
    private TextInputEditText email;
    private TextInputLayout contentname;
    private TextInputEditText name;
    private TextInputLayout contentpaswd;
    private TextInputEditText paswd;
    private LinearLayout linearLayout;
    private Button login;


    private void findViews(View root) {
        contLogin = (LinearLayout)root.findViewById( R.id.cont_login );
        contentmail = (TextInputLayout)root.findViewById( R.id.contentmail );
        email = (TextInputEditText)root.findViewById( R.id.email );
        contentname = (TextInputLayout)root.findViewById( R.id.contentname );
        name = (TextInputEditText)root.findViewById( R.id.name );
        contentpaswd = (TextInputLayout)root.findViewById( R.id.contentpaswd );
        paswd = (TextInputEditText)root.findViewById( R.id.paswd );
        linearLayout = (LinearLayout)root.findViewById( R.id.linearLayout );
        login = (Button)root.findViewById( R.id.login );
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (AccountActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_signup, container, false);
        findViews(root);

        if(act.loginService.getTempGenricUser()!=null){
            email.setText(act.loginService.getTempGenricUser().getEmail());
        }else {
            utl.toast(ctx,getString(R.string.error_msg));
            act.beginLogin(false);
        }
        //todo update generic user and move to phone
        // todo OR move to paswd if already exists
        login.setOnClickListener(v->act.beginPhone(true));

        return root;
    }
}
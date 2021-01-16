package com.dotpot.app.ui.verifyphone;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dotpot.app.R;
import com.dotpot.app.ui.AccountActivity;
import com.dotpot.app.ui.BaseFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class VerifyPhoneFragment extends BaseFragment {

    private AccountActivity act ;

    private LinearLayout contLogin;
    private TextInputLayout contentphone;
    private TextInputEditText phone;
    private TextInputLayout contentpaswd;
    private TextInputEditText password;
    private LinearLayout linearLayout;
    private Button login;
    private TextView subtext;

    private void findViews(View root) {
        contLogin = (LinearLayout)root.findViewById( R.id.cont_login );
        contentphone = (TextInputLayout)root.findViewById( R.id.contentphone );
        phone = (TextInputEditText)root.findViewById( R.id.phone );
        contentpaswd = (TextInputLayout)root.findViewById( R.id.contentpaswd );
        password = (TextInputEditText)root.findViewById( R.id.password );
        linearLayout = (LinearLayout)root.findViewById( R.id.linearLayout );
        login = (Button)root.findViewById( R.id.login );
        subtext = (TextView)root.findViewById( R.id.subtext );
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (AccountActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_phone, container, false);
        findViews(root);

        //todo verify phone
        login.setOnClickListener(v->act.beginChangePassword(true));

        return root;
    }
}
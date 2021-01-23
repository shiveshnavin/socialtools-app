package com.dotpot.app.ui.password;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dotpot.app.R;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.ui.AccountActivity;
import com.dotpot.app.ui.BaseFragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ChangePasswordFragment extends BaseFragment {

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
        login = (Button)root.findViewById( R.id.login );
        subtext = (TextView)root.findViewById( R.id.subtext );
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (AccountActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_paswd, container, false);
        findViews(root);
        String textBtm = subtext.getText().toString();
        subtext.setText(Html.fromHtml(textBtm));

        login.setOnClickListener(v->act.loginService.updatePassword(
                password2.getText().toString()
                ,password.getText().toString()
                , new GenricObjectCallback<GenricUser>() {
            @Override
            public void onEntity(GenricUser data) {
                act.inAppNavService.startHome();
                act.finishAffinity();
            }

            @Override
            public void onError(String message) {
                password2.setError(message);
            }
        }));

        return root;
    }
}
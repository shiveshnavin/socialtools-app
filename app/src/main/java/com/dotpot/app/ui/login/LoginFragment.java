package com.dotpot.app.ui.login;

import android.os.Bundle;
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

public class LoginFragment extends BaseFragment {

    private AccountActivity act ;

    private LinearLayout contLogin;
    private TextInputLayout contentmail;
    private TextInputEditText email;
    private TextInputLayout contentpaswd;
    private TextInputEditText paswd;
    private LinearLayout linearLayout;
    private Button signup;
    private Button login;
    private TextView subtext;

    private void findViews(View root) {
        contLogin = (LinearLayout)root.findViewById( R.id.cont_login );
        contentmail = (TextInputLayout)root.findViewById( R.id.contentmail );
        email = (TextInputEditText)root.findViewById( R.id.email );
        contentpaswd = (TextInputLayout)root.findViewById( R.id.contentpaswd );
        paswd = (TextInputEditText)root.findViewById( R.id.paswd );
        linearLayout = (LinearLayout)root.findViewById( R.id.linearLayout );
        signup = (Button)root.findViewById( R.id.signup );
        login = (Button)root.findViewById( R.id.login );
        subtext = (TextView)root.findViewById( R.id.subtext );
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (AccountActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        findViews(root);

        signup.setOnClickListener(v->act.loginService.googleLogin(act));
        login.setOnClickListener(v->act.loginService.emailPhoneLogin(email.getText().toString(), paswd.getText().toString(), new GenricObjectCallback<GenricUser>() {
            @Override
            public void onEntity(GenricUser data) {
                act.inAppNavService.startHome();
            }

            @Override
            public void onError(String message) {
                contentmail.setError(getString(R.string.invalidemailorpaswd));
            }
        }));

        return root;
    }
}
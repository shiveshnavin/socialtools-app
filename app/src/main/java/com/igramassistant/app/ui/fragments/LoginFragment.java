package com.igramassistant.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.igramassistant.app.R;
import com.igramassistant.app.interfaces.GenricObjectCallback;
import com.igramassistant.app.models.GenricUser;
import com.igramassistant.app.ui.activities.AccountActivity;
import com.igramassistant.app.ui.BaseFragment;
import com.igramassistant.app.utl;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import static com.igramassistant.app.services.LoginService.RC_SIGN_IN;

public class LoginFragment extends BaseFragment {

    private AccountActivity act;

    private LinearLayout contLogin;
    private TextInputLayout contentmail;
    private TextInputEditText email;
    private TextInputLayout contentpaswd;
    private TextInputEditText paswd;
    private LinearLayout linearLayout;
    private Button signup;
    private Button login;
    private TextView forgotPassword;

    private static LoginFragment mInstance;
    public static LoginFragment getInstance(){
        if(mInstance==null)
            mInstance = new LoginFragment();
        return mInstance;
    }
    private void findViews(View root) {
        contLogin = (LinearLayout) root.findViewById(R.id.cont_login);
        contentmail = (TextInputLayout) root.findViewById(R.id.contentmail);
        email = (TextInputEditText) root.findViewById(R.id.email);
        contentpaswd = (TextInputLayout) root.findViewById(R.id.contentpaswd);
        paswd = (TextInputEditText) root.findViewById(R.id.paswd);
        linearLayout = (LinearLayout) root.findViewById(R.id.linearLayout);
        signup = (Button) root.findViewById(R.id.signup);
        login = (Button) root.findViewById(R.id.request);
        forgotPassword = (TextView) root.findViewById(R.id.forgotPassword);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (AccountActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        findViews(root);

        signup.setOnClickListener(v -> act.loginService.googleLogin(RC_SIGN_IN));
        login.setOnClickListener(v ->
                act.loginService.emailPhoneLogin(
                        email.getText().toString(),
                        paswd.getText().toString(),
                        new GenricObjectCallback<GenricUser>() {
                            @Override
                            public void onEntity(GenricUser data) {
                                act.finish();
                                act.inAppNavService.startHome();
                            }

                            @Override
                            public void onError(String message) {
                                contentpaswd.setError(getString(R.string.invalidemailorpaswd));
                                contentmail.setError(getString(R.string.invalidemailorpaswd));
                            }
                        }));


        forgotPassword.setOnClickListener(v -> {

            if (email.getText().toString().isEmpty()) {
                contentmail.setError(getString(R.string.emailorphone));
                return;
            } else {
                contentmail.setError(null);
            }
            forgotPassword.setText(R.string.processing);
            act.loginService.sendPasswordResetMail(email.getText().toString(),
                    (data1, data2) -> {
                        forgotPassword.setText(R.string.forgot_password);

                        if (data2 == 1)
                        {
                            try {
                                utl.diagBottom(act, getString(R.string.reset_sent),getString(R.string.reset_sent_info)
                                ,true,getString(R.string.dismiss),null);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        else
                            utl.snack(act, getString(R.string.error_msg));

                    });

        });



        return root;
    }


}
package com.dotpot.app.ui.signup;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.dotpot.app.R;
import com.dotpot.app.binding.GenericUserViewModel;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.services.LoginService;
import com.dotpot.app.ui.AccountActivity;
import com.dotpot.app.ui.BaseFragment;
import com.dotpot.app.utils.DateTimePicker;
import com.dotpot.app.utl;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignupFragment extends BaseFragment {

    private AccountActivity act;
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
        contLogin = (LinearLayout) root.findViewById(R.id.cont_login);
        contentmail = (TextInputLayout) root.findViewById(R.id.contentmail);
        email = (TextInputEditText) root.findViewById(R.id.email);
        contentname = (TextInputLayout) root.findViewById(R.id.contentname);
        name = (TextInputEditText) root.findViewById(R.id.name);
        contentpaswd = (TextInputLayout) root.findViewById(R.id.contentpaswd);
        paswd = (TextInputEditText) root.findViewById(R.id.paswd);
        linearLayout = (LinearLayout) root.findViewById(R.id.linearLayout);
        login = (Button) root.findViewById(R.id.login);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (AccountActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_signup, container, false);
        findViews(root);

        if (act.loginService.getTempGenricUser() != null) {
            setUpUI(act.loginService.getTempGenricUser());
        } else {
            utl.toast(ctx, getString(R.string.error_msg));
            act.beginLogin(false);
        }
        //todo update generic user and move to phone
        // todo OR move to paswd if already exists

        return root;
    }

    private void setUpUI(GenricUser user) {
        email.setText(user.getEmail());
        name.setText(user.getName());
        paswd.setText(user.getDateofbirthString());

        DateTimePicker dateTimePicker = new DateTimePicker(act, DateTimePicker.DATE_ONLY, (DateTimePicker.MiliisCallback) dateTime -> {
            user.setDateofbirthLong("" + dateTime);
            paswd.setText(user.getDateofbirthString());
        });
        paswd.setOnClickListener(v -> {
            dateTimePicker.pick(false);
        });
        login.setOnClickListener(v -> {

            boolean ok = true;

            if (user.getDateofbirthLong() == null || user.getAge() < 18) {
                ok = false;
                contentpaswd.setError(getString(R.string.must_be18));
            } else
                contentpaswd.setError(null);

            if (user.getName() == null || user.getName().length() <= 1) {
                ok = false;
                contentname.setError(getString(R.string.invalidinput));
            } else
                contentname.setError(null);

            if (user.getEmail() == null || user.getEmail().length() <= 1 || !user.getEmail().contains("@")) {
                ok = false;
                contentmail.setError(getString(R.string.invalidinput));
            } else
                contentmail.setError(null);

            if (ok) {
                GenericUserViewModel.getInstance().update(act, user);
                if (LoginService.isValidPhone(user.getPhone())) {
                    act.beginSignup(true);
                } else
                    act.beginPhone(true);
            }

        });

    }
}
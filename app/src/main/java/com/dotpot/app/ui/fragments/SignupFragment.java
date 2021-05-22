package com.dotpot.app.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.binding.GenericUserViewModel;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.services.LoginService;
import com.dotpot.app.ui.activities.AccountActivity;
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


    private static SignupFragment mInstance;
    public static SignupFragment getInstance(){
        if(mInstance==null)
            mInstance = new SignupFragment();
        return mInstance;
    }
    private void findViews(View root) {
        contLogin = (LinearLayout) root.findViewById(R.id.cont_login);
        contentmail = (TextInputLayout) root.findViewById(R.id.contentmail);
        email = (TextInputEditText) root.findViewById(R.id.email);
        contentname = (TextInputLayout) root.findViewById(R.id.contentname);
        name = (TextInputEditText) root.findViewById(R.id.name);
        contentpaswd = (TextInputLayout) root.findViewById(R.id.contentpaswd);
        paswd = (TextInputEditText) root.findViewById(R.id.paswd);
        linearLayout = (LinearLayout) root.findViewById(R.id.linearLayout);
        login = (Button) root.findViewById(R.id.request);
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        init();
        act = (AccountActivity) getActivity();
        ctx = getActivity();
        View root = inflater.inflate(R.layout.fragment_signup, container, false);
        findViews(root);

        if (act.loginService.getTempGenricUser() != null) {
            setUpUI(act.loginService.getTempGenricUser());
        } else {
            utl.toast(getContext(), getString(R.string.error_msg));
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

        long maxBirth = System.currentTimeMillis() - 15 * 31556952000L;
        long initBirth = System.currentTimeMillis() - 23 * 31556952000L;

        dateTimePicker.setDateConstraints(0,initBirth,maxBirth);
        paswd.setOnClickListener(v -> {
            dateTimePicker.pick(false);
        });
        login.setOnClickListener(v -> {

            boolean ok = true;

            if (user.getDateofbirthLong() == null || user.getAge() < 16) {
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
                user.setName(name.getText().toString());
                login.setVisibility(View.GONE);
                act.loginService.commitTemporaryUserToServer(new GenricObjectCallback<GenricUser>() {
                    @Override
                    public void onEntity(GenricUser data) {
                        login.setVisibility(View.VISIBLE);
                        GenericUserViewModel.getInstance().updateLocalAndNotify(act, data);
                        Intent it = getActivity().getIntent();
                        if(it!=null && it.getStringExtra("action")!=null
                        && it.getStringExtra("action").equals(Constants.ACTION_ACCOUNT))
                        {
                            act.finish();
                        }
                        else if (LoginService.isValidPhone(user.getPhone())) {
                            act.beginChangePassword(true);
                        } else
                            act.beginPhone(true);
                    }

                    @Override
                    public void onError(String message) {
                        login.setText(R.string.next);
                        login.setVisibility(View.VISIBLE);
                        utl.snack(act,getString(R.string.error_msg)+" "+message);
                    }
                });

            }

        });

    }
}
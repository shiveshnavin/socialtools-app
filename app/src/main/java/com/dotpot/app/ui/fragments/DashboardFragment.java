package com.dotpot.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.binding.GenericUserViewModel;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.ui.BaseFragment;
import com.dotpot.app.views.RoundRectCornerImageView;
import com.squareup.picasso.Picasso;

public class DashboardFragment extends BaseFragment {

    private GenericUserViewModel userViewModel;

    private LinearLayout topContCont;
    private RelativeLayout headerCont;
    private RoundRectCornerImageView userImage;
    private TextView textDashboard;
    private NestedScrollView menuMainScroll;
    private LinearLayout menuMain;
    private ConstraintLayout editProfileCont;
    private TextView awardBal;
    private RoundRectCornerImageView awardBalIcon;
    private RoundRectCornerImageView expandIcon;
    private ConstraintLayout contpasswordance;
    private TextView password;
    private RoundRectCornerImageView passwordIcon;
    private RoundRectCornerImageView passwordexpandIcon;
    private ConstraintLayout contreferralBalance;
    private TextView referralBal;
    private RoundRectCornerImageView referralBalIcon;
    private RoundRectCornerImageView referralexpandIcon;
    private ConstraintLayout contnotifBalance;
    private TextView notifBal;
    private RoundRectCornerImageView notifBalIcon;
    private RoundRectCornerImageView notifexpandIcon;
    private ConstraintLayout contwalletance;
    private TextView wallet;
    private RoundRectCornerImageView walletIcon;
    private RoundRectCornerImageView walletexpandIcon;
    private ConstraintLayout contguideance;
    private TextView guide;
    private RoundRectCornerImageView guideIcon;
    private RoundRectCornerImageView guideexpandIcon;
    private ConstraintLayout contsupportance;
    private TextView support;
    private RoundRectCornerImageView supportIcon;
    private RoundRectCornerImageView supportexpandIcon;
    private ConstraintLayout contlogoutance;
    private TextView logout;
    private RoundRectCornerImageView logoutIcon;
    private RoundRectCornerImageView logoutexpandIcon;
    private View contMyShop;

    private void findViews(View root) {
        topContCont = (LinearLayout) root.findViewById(R.id.topContCont);
        headerCont = (RelativeLayout) root.findViewById(R.id.headerCont);
        userImage = (RoundRectCornerImageView) root.findViewById(R.id.userImage);
        textDashboard = (TextView) root.findViewById(R.id.text_dashboard);
        menuMainScroll = (NestedScrollView) root.findViewById(R.id.menuMainScroll);
        menuMain = (LinearLayout) root.findViewById(R.id.menuMain);
        editProfileCont = (ConstraintLayout) root.findViewById(R.id.editProfileCont);
        awardBal = (TextView) root.findViewById(R.id.editProfileTxt);
        awardBalIcon = (RoundRectCornerImageView) root.findViewById(R.id.awardBalIcon);
        expandIcon = (RoundRectCornerImageView) root.findViewById(R.id.expandIcon);
        contpasswordance = (ConstraintLayout) root.findViewById(R.id.contpasswordance);
        password = (TextView) root.findViewById(R.id.password);
        passwordIcon = (RoundRectCornerImageView) root.findViewById(R.id.passwordIcon);
        passwordexpandIcon = (RoundRectCornerImageView) root.findViewById(R.id.passwordexpandIcon);
        contreferralBalance = (ConstraintLayout) root.findViewById(R.id.contreferralBalance);
        referralBal = (TextView) root.findViewById(R.id.referralBal);
        referralBalIcon = (RoundRectCornerImageView) root.findViewById(R.id.referralBalIcon);
        referralexpandIcon = (RoundRectCornerImageView) root.findViewById(R.id.referralexpandIcon);
        contnotifBalance = (ConstraintLayout) root.findViewById(R.id.contnotifBalance);
        notifBal = (TextView) root.findViewById(R.id.notifBal);
        notifBalIcon = (RoundRectCornerImageView) root.findViewById(R.id.notifBalIcon);
        notifexpandIcon = (RoundRectCornerImageView) root.findViewById(R.id.notifexpandIcon);
        contwalletance = (ConstraintLayout) root.findViewById(R.id.contwalletance);
        wallet = (TextView) root.findViewById(R.id.wallet);
        walletIcon = (RoundRectCornerImageView) root.findViewById(R.id.walletIcon);
        walletexpandIcon = (RoundRectCornerImageView) root.findViewById(R.id.walletexpandIcon);
        contguideance = (ConstraintLayout) root.findViewById(R.id.contguideance);
        guide = (TextView) root.findViewById(R.id.guide);
        guideIcon = (RoundRectCornerImageView) root.findViewById(R.id.guideIcon);
        guideexpandIcon = (RoundRectCornerImageView) root.findViewById(R.id.guideexpandIcon);
        contsupportance = (ConstraintLayout) root.findViewById(R.id.contsupportance);
        support = (TextView) root.findViewById(R.id.support);
        supportIcon = (RoundRectCornerImageView) root.findViewById(R.id.supportIcon);
        supportexpandIcon = (RoundRectCornerImageView) root.findViewById(R.id.supportexpandIcon);
        contlogoutance = (ConstraintLayout) root.findViewById(R.id.contlogoutance);
        logout = (TextView) root.findViewById(R.id.logout);
        logoutIcon = (RoundRectCornerImageView) root.findViewById(R.id.logoutIcon);
        logoutexpandIcon = (RoundRectCornerImageView) root.findViewById(R.id.logoutexpandIcon);
        contMyShop = root.findViewById(R.id.contMyShop);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        userViewModel = GenericUserViewModel.getInstance();


        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        findViews(root);
        if (act.fragmentManager.getBackStackEntryCount() > 0)
            act.fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


        final TextView textView = root.findViewById(R.id.text_dashboard);

        editProfileCont.setOnClickListener(v -> {
            navService.startEdit();
        });

        textDashboard.setOnClickListener(view -> editProfileCont.callOnClick());
        contpasswordance.setOnClickListener(view -> navService.startChangePassword());
        contreferralBalance.setOnClickListener(view -> navService.startGameListPage(fragmentId));
        contMyShop.setOnClickListener(view -> navService.startUserShop(fragmentId));
        contsupportance.setOnClickListener(view -> navService.startSupport(fragmentId));
        contlogoutance.setOnClickListener(v->act.startLogout());
        contguideance.setOnClickListener(view -> navService.startWebsite(getString(R.string.help_and_guide),Constants.HOST+"/help.html"));


        GenericUserViewModel.getInstance().getUser().observe(getViewLifecycleOwner(), new Observer<GenricUser>() {
            @Override
            public void onChanged(GenricUser user) {
                textDashboard.setText(user.getName());
                if(user.getImage()!=null && user.getImage().contains("http")){
                    Picasso.get().load(user.getImage()).placeholder(R.drawable.account).into(userImage);
                }
            }
        });
        return root;
    }
}
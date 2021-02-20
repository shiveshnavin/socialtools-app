package com.dotpot.app.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dotpot.app.App;
import com.dotpot.app.R;
import com.dotpot.app.adapters.GenriXAdapter;
import com.dotpot.app.binding.WalletViewModel;
import com.dotpot.app.models.ActionItem;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.ui.BaseFragment;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utl;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import tyrantgit.explosionfield.ExplosionField;

public class HomeFragment extends BaseFragment {

    private HomeViewModel homeViewModel;
    private GenriXAdapter<ActionItem> actionAdapter;
    private GenriXAdapter<GenricUser> leaderBoardAdapter;


    private ImageView poster;
    private ImageView playIcon;
    private TextView textHome;
    private TextView bottomText;
    private ConstraintLayout contNotif;
    private TextView notifAction;
    private TextView notifTxt;
    private ConstraintLayout contRef;
    private TextView weeklyLeaderboardtxt;
    private RecyclerView listLeaderboard;
    private RecyclerView listItems;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2021-02-20 13:57:35 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews(View root) {
        poster = (ImageView)root.findViewById( R.id.poster );
        playIcon = (ImageView)root.findViewById( R.id.playIcon );
        textHome = (TextView)root.findViewById( R.id.text_home );
        bottomText = (TextView)root.findViewById( R.id.bottomText );
        contNotif = (ConstraintLayout)root.findViewById( R.id.contNotif );
        notifAction = (TextView)root.findViewById( R.id.notifAction );
        notifTxt = (TextView)root.findViewById( R.id.notifTxt );
        contRef = (ConstraintLayout)root.findViewById( R.id.contRef );
        weeklyLeaderboardtxt = (TextView)root.findViewById( R.id.weeklyLeaderboardtxt );
        listLeaderboard = (RecyclerView)root.findViewById( R.id.listLeaderboard );
        listItems = (RecyclerView)root.findViewById( R.id.listItems );
    }


    public static class DiaglogReferral{
        private View contRef;
        private LinearLayout contRefCard;
        private TextView currency;
        private TextView refBalance;
        private TextView refActionCode;
        private TextView refTxt;
        private Button addBtn;
        private TextView haveAcode;

        private void findViews(View root) {
            contRef = root.findViewById( R.id.contRef );
            contRefCard = (LinearLayout)root.findViewById( R.id.contRefCard );
            currency = (TextView)root.findViewById( R.id.currency );
            refBalance = (TextView)root.findViewById( R.id.refBalance );
            refActionCode = (TextView)root.findViewById( R.id.refActionCode );
            refTxt = (TextView)root.findViewById( R.id.refTxt );
            addBtn = (Button)root.findViewById( R.id.addBtn );
            haveAcode=root.findViewById(R.id.haveAcode);

        }

        private String getReferralMessage(String refCode){
            return String.format(ResourceUtils.getString(R.string.referral_message),
                    ResourceUtils.getString(R.string.app_name),refCode, BaseActivity.mFirebaseRemoteConfig.getString("download_link"));
        }

        public DiaglogReferral(View root,BaseFragment fragment){
            findViews(root);
            WalletViewModel.getInstance().getWallet().observe(fragment.getViewLifecycleOwner(), wallet -> {
                currency.setText(ResourceUtils.getString(R.string.ref_bal)+" "+ResourceUtils.getString(R.string.currency));
                refBalance.setText(""+wallet.getAggReferralBalance());
                refActionCode.setText(wallet.getReferralCode());
                refActionCode.setOnClickListener(view -> {
                    utl.copyToClipBoard(ResourceUtils.getString(R.string.refer)+" "+
                            ResourceUtils.getString(R.string.app_name),

                            (wallet.getReferralCode()),
                            view.getContext());
                });
                addBtn.setOnClickListener(view -> {
                    fragment.act.shareText(getReferralMessage(wallet.getReferralCode()),"");
                });
                if(utl.isEmpty(wallet.getReferredBy())){
                    haveAcode.setVisibility(View.VISIBLE);
                    haveAcode.setOnClickListener((v)->{
                        
                        utl.diagInputTextImage(root.getContext(), ResourceUtils.getString(R.string.enter_ref_code), text -> {

                            RestAPI.getInstance(App.getAppContext())
                                    .redeemReferral(text, (data1, data2) -> {
                                        WalletViewModel.getInstance().refresh(null);
                                        utl.diagBottom(root.getContext(),data1);
                                    });

                        });
                        
                    });
                }
                else {
                    haveAcode.setVisibility(View.GONE);
                }

            });
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        findViews(root);
        new DiaglogReferral(root,this);
        homeViewModel.refresh(act);
        if (act.fragmentManager.getBackStackEntryCount() > 0)
            act.fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getActions().observe(getViewLifecycleOwner(), this::setUpActionList);
        homeViewModel.getLeaderboard().observe(getViewLifecycleOwner(), this::setUpLeaderboardList);



        final Animation press = AnimationUtils.loadAnimation(ctx, R.anim.motion_play_anim);
        final Animation release = AnimationUtils.loadAnimation(ctx, R.anim.motion_play_anim);
        release.setInterpolator(paramFloat -> Math.abs(paramFloat -1f));
        ExplosionField explosionField = ExplosionField.attach2Window(getActivity());

        playIcon.setOnTouchListener((view, event) -> {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    playIcon.animate().scaleX(1.5f)
                            .scaleY(1.5f).setDuration(300)
                            .rotation(360f).start();
                    return true;
                case MotionEvent.ACTION_UP:
                    playIcon.animate().scaleX(1)
                            .scaleY(1f).setDuration(300)
                            .rotation(0f).start();
                    break;
                case MotionEvent.ACTION_CANCEL:
                    playIcon.animate().scaleX(1f)
                            .scaleY(1f).setDuration(300)
                            .rotation(0f).start();
                    break;
                default:
                    break;
            }


            return false;
        });

        contNotif.setOnClickListener(view -> {

        });

        return root;
    }

    private void setUpActionList(List<ActionItem> actionList) {

        actionAdapter = new GenriXAdapter<ActionItem>(getContext(), R.layout.row_card_menu, actionList) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int pos = viewHolder.getAdapterPosition();
                final GenriXAdapter.CustomViewHolder vh = (CustomViewHolder) viewHolder;
                vh.itemView.setOnClickListener(view -> {

                });
            }
        };
        listItems.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listItems.setAdapter(actionAdapter);


    }


    private void setUpLeaderboardList(List<GenricUser> genricUsers) {

        Collections.sort(genricUsers, (user, t1) -> {
            Long aw1 = Long.parseLong(user.getAbout());
            Long aw2 = Long.parseLong(t1.getAbout());

            return aw2.compareTo(aw1);
        });

        leaderBoardAdapter = new GenriXAdapter<GenricUser>(getContext(), R.layout.row_user, genricUsers) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int pos = viewHolder.getAdapterPosition();
                final GenriXAdapter.CustomViewHolder vh = (CustomViewHolder) viewHolder;
                final GenricUser leader = genricUsers.get(pos);

                vh.textView(R.id.username).setText(leader.getName());
                vh.textView(R.id.rank).setText(leader.getRank());
                vh.textView(R.id.earnings).setText(ResourceUtils.getString(R.string.currency)+" "+leader.getAbout());
                Picasso.get().load(leader.getImage())
                        .placeholder(R.drawable.ic_users)
                        .into(vh.imageView(R.id.userImage));

                vh.itemView.setOnClickListener(view -> {

                });
            }
        };

        listLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        listLeaderboard.setAdapter(leaderBoardAdapter);


    }
}
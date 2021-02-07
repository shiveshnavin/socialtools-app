package com.dotpot.app.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dotpot.app.R;
import com.dotpot.app.adapters.GenriXAdapter;
import com.dotpot.app.models.ActionItem;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.ui.BaseFragment;

import java.util.List;

import tyrantgit.explosionfield.ExplosionField;

public class HomeFragment extends BaseFragment {

    private HomeViewModel homeViewModel;
    private GenriXAdapter<ActionItem> actionAdapter;
    private GenriXAdapter<GenricUser> leaderBoardAdapter;

    private ImageView poster;
    private TextView textHome;
    private TextView bottomText;
    private RecyclerView listActions;
    private ConstraintLayout contNotif;
    private TextView notifAction;
    private TextView notifTxt;
    private TextView weeklyLeaderboardtxt;
    private RecyclerView listLeaderboard;
    private ImageView playIcon;


    private void findViews(View root) {
        playIcon = root.findViewById(R.id.playIcon);
        poster = (ImageView) root.findViewById(R.id.poster);
        textHome = (TextView) root.findViewById(R.id.text_home);
        bottomText = (TextView) root.findViewById(R.id.bottomText);
        listActions = (RecyclerView) root.findViewById(R.id.listItems);
        contNotif = (ConstraintLayout) root.findViewById(R.id.contNotif);
        notifAction = (TextView) root.findViewById(R.id.notifAction);
        notifTxt = (TextView) root.findViewById(R.id.notifTxt);
        weeklyLeaderboardtxt = (TextView) root.findViewById(R.id.weeklyLeaderboardtxt);
        listLeaderboard = (RecyclerView) root.findViewById(R.id.listLeaderboard);
    }

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        findViews(root);
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
//                    view.postDelayed(()->explosionField.explode(view),300);
//                    new Handler().postDelayed(()->{
//                        view.invalidate();
//                        explosionField.clear();
//                    },1000);
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
        listActions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listActions.setAdapter(actionAdapter);


    }


    private void setUpLeaderboardList(List<GenricUser> genricUsers) {

        leaderBoardAdapter = new GenriXAdapter<GenricUser>(getContext(), R.layout.row_user, genricUsers) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int pos = viewHolder.getAdapterPosition();
                final GenriXAdapter.CustomViewHolder vh = (CustomViewHolder) viewHolder;


                vh.itemView.setOnClickListener(view -> {

                });
            }
        };

        listLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        listLeaderboard.setAdapter(leaderBoardAdapter);


    }
}
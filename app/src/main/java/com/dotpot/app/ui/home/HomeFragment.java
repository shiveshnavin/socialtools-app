package com.dotpot.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dotpot.app.R;
import com.dotpot.app.adapters.GenriXAdapter;
import com.dotpot.app.models.ActionItem;
import com.dotpot.app.models.GenricUser;

import java.util.List;

public class HomeFragment extends Fragment {

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



    private void findViews(View root) {
        poster = (ImageView)root.findViewById( R.id.poster );
        textHome = (TextView)root.findViewById( R.id.text_home );
        bottomText = (TextView)root.findViewById( R.id.bottomText );
        listActions = (RecyclerView)root.findViewById( R.id.listItems );
        contNotif = (ConstraintLayout)root.findViewById( R.id.contNotif );
        notifAction = (TextView)root.findViewById( R.id.notifAction );
        notifTxt = (TextView)root.findViewById( R.id.notifTxt );
        weeklyLeaderboardtxt = (TextView)root.findViewById( R.id.weeklyLeaderboardtxt );
        listLeaderboard = (RecyclerView)root.findViewById( R.id.listLeaderboard );
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        findViews(root);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getActions().observe(getViewLifecycleOwner(), this::setUpActionList);
        homeViewModel.getLeaderboard().observe(getViewLifecycleOwner(), this::setUpLeaderboardList);



        contNotif.setOnClickListener(view -> {

        });

        return root;
    }

    private void setUpActionList(List<ActionItem> actionList){

        actionAdapter = new GenriXAdapter<ActionItem>(getContext(),R.layout.row_card_menu,actionList){
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int pos = viewHolder.getAdapterPosition();
                final GenriXAdapter.CustomViewHolder vh = (CustomViewHolder)viewHolder;
                vh.itemView.setOnClickListener(view -> {

                });
            }
        };
        listActions.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listActions.setAdapter(actionAdapter);


    }


    private void setUpLeaderboardList(List<GenricUser> genricUsers){

        leaderBoardAdapter = new GenriXAdapter<GenricUser>(getContext(),R.layout.row_user,genricUsers){
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

                final int pos = viewHolder.getAdapterPosition();
                final GenriXAdapter.CustomViewHolder vh = (CustomViewHolder)viewHolder;


                vh.itemView.setOnClickListener(view -> {

                });
            }
        };

        listLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        listLeaderboard.setAdapter(leaderBoardAdapter);


    }
}
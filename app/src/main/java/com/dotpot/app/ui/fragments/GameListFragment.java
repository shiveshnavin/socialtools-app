package com.dotpot.app.ui.fragments;

import com.dotpot.app.App;
import com.dotpot.app.R;
import com.dotpot.app.binding.GameViewModel;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Game;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.utl;

import java.util.ArrayList;
import java.util.Date;

public class GameListFragment extends ViewListFragment<Game>{


    private static GameListFragment mInstance;
    public static GameListFragment getInstance(){
        if(mInstance==null)
            mInstance = new GameListFragment();
        return mInstance;
    }


    @Override
    void onReadyToReceiveItems() {
        setTitle(getString(R.string.mygames));
        GameViewModel.getInstance().refreshGames(act, () -> GameViewModel.getInstance().getUserGames().observe(getViewLifecycleOwner(), this::reset));
    }

    @Override
    void renderCurrentItem(Game item, ViewListFragment.ViewListItemHolder itemUI, int pos) {
        itemUI.itemTitle.setText(getString(R.string.games)+" ");
        itemUI.itemAddTitle.setText(item.getId());

        if(item.isPlayer1Won()){
            itemUI.itemDescription.setText(String.format(getString(R.string.you_won),item.getAward()));
        }
        else {
            itemUI.itemDescription.setText(R.string.you_lost);
        }
        itemUI.actionBtn.setText(getString(R.string.view));
        itemUI.image.setImageResource(R.drawable.motion_play);
        itemUI.bottomNote.setText(utl.getDateTimeFormatted(new Date(item.getTimeStamp())));
    }

    @Override
    void loadNextPage(int pageNo, int sizeOfExistingList, GenricObjectCallback<Game> onNewItems) {

        RestAPI.getInstance(App.getAppContext())
                .getUserGames(sizeOfExistingList,new GenricObjectCallback<Game>(){
                    @Override
                    public void onEntitySet(ArrayList<Game> listItems) {
                        onNewItems.onEntitySet(listItems);
                    }

                    @Override
                    public void onError(String message) {
                        utl.snack(act, R.string.error_msg);
                        onNewItems.onError(message);
                    }
                });

    }
}

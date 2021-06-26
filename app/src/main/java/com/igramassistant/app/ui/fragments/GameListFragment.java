package com.igramassistant.app.ui.fragments;

import com.igramassistant.app.App;
import com.igramassistant.app.R;
import com.igramassistant.app.binding.GameViewModel;
import com.igramassistant.app.interfaces.GenricObjectCallback;
import com.igramassistant.app.models.Game;
import com.igramassistant.app.models.Wallet;
import com.igramassistant.app.services.RestAPI;
import com.igramassistant.app.utl;

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
        itemUI.itemTitle.setText(getString(R.string.game)+" of ");
        itemUI.itemAddTitle.setText(Wallet.wrap(item.getAmount()));


        if(item.isPlayer1Won()){
            itemUI.image.setImageResource(R.drawable.win);
            itemUI.itemDescription.setText(String.format(getString(R.string.you_won),item.getAward()));
        }
        else {
            itemUI.image.setImageResource(R.drawable.replay);
            itemUI.itemDescription.setText(R.string.you_lost);
        }
        itemUI.actionBtn.setText(getString(R.string.view));
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

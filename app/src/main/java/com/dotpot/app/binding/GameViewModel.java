package com.dotpot.app.binding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dotpot.app.App;
import com.dotpot.app.R;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Game;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;

import java.util.ArrayList;

public class GameViewModel extends ViewModel {

    private static GameViewModel instance;
    private MutableLiveData<ArrayList<Game>> userGames;
    private MutableLiveData<ArrayList<Float>> gameAmounts;

    private GameViewModel() {
        userGames = new MutableLiveData<>();
        gameAmounts = new MutableLiveData<>();
        ArrayList<Game> leaderList = new ArrayList<>();
        ArrayList<Float> actionList = new ArrayList<>();
        userGames.setValue(leaderList);
        gameAmounts.setValue(actionList);

    }

    public static GameViewModel getInstance() {
        if (instance == null) {
            instance = new GameViewModel();
        }
        return instance;
    }


    public void refreshAmounts(BaseActivity activity){
        RestAPI.getInstance(App.getAppContext())
                .getGameAmounts(new GenricObjectCallback<Float>(){
                    @Override
                    public void onEntitySet(ArrayList<Float> listItems) {
                        gameAmounts.postValue(listItems);
                    }

                    @Override
                    public void onError(String message) {
                        utl.snack(activity, R.string.error_msg);
                    }
                });

    }


    public void refreshGames(BaseActivity activity){
        RestAPI.getInstance(App.getAppContext())
                .getUserGames(0,new GenricObjectCallback<Game>(){
                    @Override
                    public void onEntitySet(ArrayList<Game> listItems) {
                        userGames.postValue(listItems);
                    }

                    @Override
                    public void onError(String message) {
                        utl.snack(activity, R.string.error_msg);
                    }
                });
    }


    public LiveData<ArrayList<Game>> getUserGames() {
        return userGames;
    }

    public LiveData<ArrayList<Float>> getGameAmounts() {
        return gameAmounts;
    }

}
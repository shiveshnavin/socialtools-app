package com.igramassistant.app.binding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.igramassistant.app.App;
import com.igramassistant.app.R;
import com.igramassistant.app.interfaces.GenricCallback;
import com.igramassistant.app.interfaces.GenricObjectCallback;
import com.igramassistant.app.models.Game;
import com.igramassistant.app.services.RestAPI;
import com.igramassistant.app.ui.BaseActivity;
import com.igramassistant.app.utl;

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


    public void refreshGames(BaseActivity activity, final GenricCallback cb){
        RestAPI.getInstance(App.getAppContext())
                .getUserGames(0,new GenricObjectCallback<Game>(){
                    @Override
                    public void onEntitySet(ArrayList<Game> listItems) {
                        userGames.postValue(listItems);
                        if(cb!=null)
                            cb.onStart();
                    }

                    @Override
                    public void onError(String message) {
                        if(cb!=null)
                            cb.onStart();
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
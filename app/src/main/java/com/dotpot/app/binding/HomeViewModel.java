package com.dotpot.app.binding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dotpot.app.App;
import com.dotpot.app.R;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.ActionItem;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;

import java.util.ArrayList;

public class HomeViewModel extends ViewModel {


    private MutableLiveData<ArrayList<GenricUser>> leaders;
    private MutableLiveData<ArrayList<ActionItem>> actions;

    public HomeViewModel() {
        leaders = new MutableLiveData<>();
        actions = new MutableLiveData<>();
        ArrayList<GenricUser> leaderList = new ArrayList<>();
        ArrayList<ActionItem> actionList = new ArrayList<>();
        leaders.setValue(leaderList);
        actions.setValue(actionList);

    }

    public void refresh(BaseActivity activity){
        RestAPI.getInstance(App.getAppContext())
                .getActionItems(activity,new GenricObjectCallback<ActionItem>(){
                    @Override
                    public void onEntitySet(ArrayList<ActionItem> listItems) {
                        actions.postValue(listItems);
                    }

                    @Override
                    public void onError(String message) {
                        utl.snack(activity, R.string.error_msg_restart);
                    }
                });

        RestAPI.getInstance(App.getAppContext())
                .getLeaderBoard(new GenricObjectCallback<GenricUser>(){
                    @Override
                    public void onEntitySet(ArrayList<GenricUser> listItems) {
                        leaders.postValue(listItems);
                    }

                    @Override
                    public void onError(String message) {
                        utl.snack(activity, R.string.error_msg_restart);
                    }
                });


    }

    public LiveData<ArrayList<GenricUser>> getLeaderboard() {
        return leaders;
    }

    public LiveData<ArrayList<ActionItem>> getActions() {
        return actions;
    }

}
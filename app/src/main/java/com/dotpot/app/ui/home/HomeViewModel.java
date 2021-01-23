package com.dotpot.app.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dotpot.app.models.ActionItem;
import com.dotpot.app.models.GenricUser;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {


    private MutableLiveData<List<GenricUser>> leaders;
    private MutableLiveData<List<ActionItem>> actions;

    public HomeViewModel() {
        leaders = new MutableLiveData<>();
        actions = new MutableLiveData<>();
        List<GenricUser> leaderList = new ArrayList<>();
        List<ActionItem> actionList = new ArrayList<>();
        int N = 20;
        while (N-- > 0) {
            leaderList.add(new GenricUser());
            actionList.add(new ActionItem());
        }
        leaders.setValue(leaderList);
        actions.setValue(actionList);

    }

    public LiveData<List<GenricUser>> getLeaderboard() {
        return leaders;
    }

    public LiveData<List<ActionItem>> getActions() {
        return actions;
    }

}
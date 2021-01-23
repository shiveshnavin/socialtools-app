package com.dotpot.app.binding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dotpot.app.models.GenricUser;

public class GenericUserViewModel extends ViewModel {

    private MutableLiveData<GenricUser> genricUserLive;
    private static GenericUserViewModel instance;

    public static GenericUserViewModel getInstance(){
        if(instance==null){
            instance = new GenericUserViewModel();
            instance.genricUserLive = new MutableLiveData<>();
            instance.genricUserLive.setValue(new GenricUser());
        }
        return instance;
    }

    public void refresh(){

    }

    public LiveData<GenricUser> getText() {
        return genricUserLive;
    }
}
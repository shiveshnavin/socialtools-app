package com.igramassistant.app.binding;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.igramassistant.app.App;
import com.igramassistant.app.interfaces.GenricObjectCallback;
import com.igramassistant.app.models.GenricUser;
import com.igramassistant.app.ui.BaseActivity;
import com.igramassistant.app.utl;

public class GenericUserViewModel extends ViewModel {

    private static GenericUserViewModel instance;
    private MutableLiveData<GenricUser> genricUserLive;

    public static GenericUserViewModel getInstance() {
        if (instance == null) {
            instance = new GenericUserViewModel();
            instance.genricUserLive = new MutableLiveData<>();
        }
        return instance;
    }

    public void refresh() {
        GenricUser user = utl.readUserData();
        if (user != null) {
            BaseActivity.restApi.getGenricUser(user.getId(), new GenricObjectCallback<GenricUser>() {
                @Override
                public void onEntity(GenricUser data) {
                    genricUserLive.setValue(data);
                }

                @Override
                public void onError(String message) {
                    utl.e(GenericUserViewModel.class, "Unable to refresh " + message);
                }
            });
        } else {
            utl.e(GenericUserViewModel.class, "Unable to refresh as not user found");
        }
    }

    public MutableLiveData<GenricUser> getUser() {
        return genricUserLive;
    }

    public void updateLocalAndNotify(Context act, GenricUser user) {
        if (act != null) {
            utl.writeUserData(user, act);
        } else {
            utl.writeUserData(user, App.getAppContext());
        }
        if (user != null)
            genricUserLive.postValue(user);

    }

    public void onlyIfPresent(GenricObjectCallback<GenricUser> cb){
        if(getUser().getValue()!=null){
            cb.onEntity(getUser().getValue());
        }
    }

}
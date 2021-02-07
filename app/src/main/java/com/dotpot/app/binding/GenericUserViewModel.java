package com.dotpot.app.binding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;

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

    public void refresh(BaseActivity ctx){
        GenricUser user = utl.readUserData();
        if(user!=null){
            ctx.restApi.getGenricUser(user.getId(), new GenricObjectCallback<GenricUser>() {
                @Override
                public void onEntity(GenricUser data) {
                    genricUserLive.setValue(data);
                }

                @Override
                public void onError(String message) {
                    utl.e(GenericUserViewModel.class,"Unable to refresh "+message);
                }
            });
        }
        else {
            utl.e(GenericUserViewModel.class,"Unable to refresh as not user found");
        }
    }

    public LiveData<GenricUser> getUser() {
        return genricUserLive;
    }
}
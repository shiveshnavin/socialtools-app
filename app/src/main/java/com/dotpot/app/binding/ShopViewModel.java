package com.dotpot.app.binding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dotpot.app.App;
import com.dotpot.app.R;
import com.dotpot.app.interfaces.GenricCallback;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Product;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;

import java.util.ArrayList;

public class ShopViewModel extends ViewModel {

    private static ShopViewModel instance;
    private MutableLiveData<ArrayList<Product>> userGames;

    private ShopViewModel() {
        userGames = new MutableLiveData<>();
        ArrayList<Product> leaderList = new ArrayList<>();
        userGames.setValue(leaderList);

    }

    public static ShopViewModel getInstance() {
        if (instance == null) {
            instance = new ShopViewModel();
        }
        return instance;
    }

    public void refreshGames(BaseActivity activity,String type, final GenricCallback cb){
        RestAPI.getInstance(App.getAppContext())
                .getProducts(0,type,new GenricObjectCallback<Product>(){
                    @Override
                    public void onEntitySet(ArrayList<Product> listItems) {
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


    public LiveData<ArrayList<Product>> getProducts() {
        return userGames;
    }


}
package com.igramassistant.app.binding;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.igramassistant.app.App;
import com.igramassistant.app.R;
import com.igramassistant.app.interfaces.GenricCallback;
import com.igramassistant.app.interfaces.GenricObjectCallback;
import com.igramassistant.app.models.Product;
import com.igramassistant.app.services.RestAPI;
import com.igramassistant.app.ui.BaseActivity;
import com.igramassistant.app.utl;

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
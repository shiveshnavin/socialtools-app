package com.dotpot.app.services;

import com.dotpot.app.interfaces.API;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.interfaces.NetworkService;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;


public class RestAPI implements API {

    BaseActivity act;
    NetworkService service;
    LocalAPIService localAPI;

    public RestAPI(BaseActivity b)
    {
        this.act = b;
        service=b.netService;
        localAPI=new LocalAPIService(b);
    }

    @Override
    public void getGenricUser(String userId, GenricObjectCallback<GenricUser> cb) {
        cb.onEntity(utl.readUserData());
    }
}

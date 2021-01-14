package com.dotpot.app.services;

import com.dotpot.app.interfaces.API;
import com.dotpot.app.interfaces.NetworkService;
import com.dotpot.app.ui.BaseActivity;


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
}

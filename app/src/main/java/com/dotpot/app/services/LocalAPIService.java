package com.dotpot.app.services;

import com.dotpot.app.interfaces.API;
import com.dotpot.app.ui.BaseActivity;

public class LocalAPIService implements API {

    public BaseActivity ctx;

    public LocalAPIService(BaseActivity ctx) {
        this.ctx = ctx;
    }

}


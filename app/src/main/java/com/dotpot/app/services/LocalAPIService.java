package com.dotpot.app.services;

import android.content.Context;

import com.dotpot.app.interfaces.API;

public class LocalAPIService implements API {

    public Context ctx;

    public LocalAPIService(Context ctx) {
        this.ctx = ctx;
    }

}


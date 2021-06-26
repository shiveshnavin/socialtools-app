package com.igramassistant.app.utils;

import android.content.Context;
import android.view.View;

public class ShowHideLoader {

    private Context ctx;
    private View content;
    private View loader;

    public ShowHideLoader(){
    }

    public static ShowHideLoader create(){
        return new ShowHideLoader();
    }

    public ShowHideLoader content(View content){
        this.content=content;
        return this;
    }
    public ShowHideLoader loader(View loader){
        this.loader=loader;
        return this;
    }
    public ShowHideLoader loaded(){
        if(this.content!=null){
            content.setVisibility(View.VISIBLE);
        }
        if(this.loader!=null){
            this.loader.setVisibility(View.GONE);
        }
        return this;
    }

    public ShowHideLoader loading(){
        if(this.content!=null){
            content.setVisibility(View.GONE);
        }
        if(this.loader!=null){
            this.loader.setVisibility(View.VISIBLE);
        }
        return this;
    }


}

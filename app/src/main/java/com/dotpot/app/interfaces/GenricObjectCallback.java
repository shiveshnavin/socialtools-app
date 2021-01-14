package com.dotpot.app.interfaces;

import com.dotpot.app.utl;

import java.util.ArrayList;

public interface GenricObjectCallback<T> {

    default void onEntity(T data){
        utl.e("GenricObjectCallback::onEntity Not Implemented");
    };
    default void onEntitySet(ArrayList<T> listItems){
        utl.e("GenricObjectCallback::onEntitySet Not Implemented");
    };
}

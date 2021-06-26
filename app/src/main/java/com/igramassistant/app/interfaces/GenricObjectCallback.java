package com.igramassistant.app.interfaces;

import com.igramassistant.app.utl;

import java.util.ArrayList;

public interface GenricObjectCallback<T> {

    default void onEntity(T data){
        utl.e("GenricObjectCallback::onEntity Not Implemented");
    };
    default void onEntitySet(ArrayList<T> listItems){
        utl.e("GenricObjectCallback::onEntitySet Not Implemented");
    };
    default void onError(String message){
        utl.e("GenricObjectCallback::onError Not Implemented");
    }
}

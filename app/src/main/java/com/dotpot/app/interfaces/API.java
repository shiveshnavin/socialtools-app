package com.dotpot.app.interfaces;


import com.dotpot.app.models.GenricUser;
import com.dotpot.app.utl;

public interface API {

    default void getGenricUser(String userId, GenricObjectCallback<GenricUser> cb){
        utl.e("Not Implemented API::getGenricUser");
    };

}

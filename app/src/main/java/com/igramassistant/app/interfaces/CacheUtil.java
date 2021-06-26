package com.igramassistant.app.interfaces;

import org.json.JSONObject;

public interface CacheUtil {


    default void buildCache() throws  Exception {};

    default void dumpCache(){};

    default void invalidateOne(String url){};

    default void invalidateAll(){};

    default void putIntoCache(String url, JSONObject body, String value){};

    default void putIntoCache(String key, String value){};

    default Long getTimeout(String key){
        return -1l;
    };

    default boolean getFromCache(String url, JSONObject body, NetworkRequestCallback cb){
        return false;
    };

    default boolean getFromCache(String key, NetworkRequestCallback cb){
        return false;
    };
}

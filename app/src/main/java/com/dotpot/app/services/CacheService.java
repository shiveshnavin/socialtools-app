package com.dotpot.app.services;

import android.content.Context;

import com.dotpot.app.models.GenricUser;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.dotpot.app.App;
import com.dotpot.app.interfaces.CacheUtil;
import com.dotpot.app.interfaces.NetworkRequestCallback;
import com.dotpot.app.models.KeyValue;
import com.dotpot.app.utl;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class CacheService implements CacheUtil {
    private static CacheUtil ourInstance ;
    private static Cache<String , KeyValue> cache;
    private FirebaseRemoteConfig remoteConfig;
    private JSONObject cachePersistentJs;
    private GenricUser user;

    private CacheService() {
        remoteConfig=FirebaseRemoteConfig.getInstance();
        user = utl.readUserData();
    }

    public static CacheUtil getInstance() {
        if(CacheService.ourInstance==null){
            if(!FirebaseRemoteConfig.getInstance().getBoolean("caching_enabled")){
                ourInstance=new CacheUtil(){ };
                return ourInstance;
            }
            CacheService.ourInstance=new CacheService();
            CacheService.cache= CacheBuilder.newBuilder()
                    .expireAfterWrite(60, TimeUnit.MINUTES).build();
            try {
                CacheService.ourInstance.buildCache();
            } catch (Exception e) {
                utl.e("Cache","Building Cache Failed");
                e.printStackTrace();
            }
        }
        return CacheService.ourInstance;
    }

    @Override
    public void dumpCache(){
        Context ctx=App.getAppContext();
        if(ctx==null)
            return;
        JSONObject jsonObject=new JSONObject();
        cache.asMap().entrySet().stream().forEach(stringKeyValueEntry -> {
            try {
                jsonObject.put(stringKeyValueEntry.getKey(),utl.js.toJson(stringKeyValueEntry.getValue()));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        utl.setKey("net_cache",jsonObject.toString(),ctx);
        utl.e("Cache","Dumping Cache Success : Size = "+cache.size());
    }

    public void buildCache() throws  Exception {
        Context ctx= App.getAppContext();
        if(ctx==null)
            return;
        if(cachePersistentJs ==null){
            String cachePersistantString = utl.getKey("net_cache", ctx);
            if(cachePersistantString !=null){
                cachePersistentJs =new JSONObject(cachePersistantString);
            }
            else {
                utl.e("Cache","Building Cache Failed ! JStr is null");
                return;
            }
        }
        Iterator<String> keys = cachePersistentJs.keys();

        while(keys.hasNext()) {
            String key = keys.next();
            KeyValue kv=utl.js.fromJson(cachePersistentJs.getString(key),KeyValue.class);
            cache.put(key,kv);
        }
        utl.e("Cache","Building Cache Success : Size = "+cache.size());
    }
    private int hash(String s) {
        int h = 0;
        for (int i = 0; i < s.length(); i++) {
            h = 31 * h + s.charAt(i);
        }
        return h;
    }

    @Override
    public void invalidateOne(String url){
        cache.asMap().entrySet().stream().forEach(stringKeyValueEntry -> {

            if(stringKeyValueEntry.getKey().contains(url)){
                cache.invalidate(stringKeyValueEntry.getKey());
                utl.e("Cache","Forcefully invalidated "+stringKeyValueEntry.getKey());
            }

        });
    }
    @Override
    public void invalidateAll(){
        cache.invalidateAll();
    }
    @Override
    public void putIntoCache(String url, JSONObject body, String value){

        String key = url;
        try {
            if(body!=null){
                key += "---"+hash(body.toString());//key +"---"+
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(getTimeout(key)==-1){
            return;
        }
        putIntoCache(key,value);
    }
    @Override
    public void putIntoCache(String key, String value){
        if(getTimeout(key)==-1){
            return;
        }
        utl.e("Cache","Saving to cache "+key);
        KeyValue keyValue=new KeyValue(""+System.currentTimeMillis(),value);
        cache.put(key,keyValue);
    }

    @Override
    public Long getTimeout(String key){
        try{
            String jsr=remoteConfig.getString("cache_config");
            JSONObject cacheConf=new JSONObject(jsr);
            Iterator<String > i=cacheConf.keys();
            while (i.hasNext()){
                String k=i.next();
                String keyFromConfig = k;
                if(user != null)
                    keyFromConfig = keyFromConfig.replace("${userId}",user.getId());
                keyFromConfig = keyFromConfig.toLowerCase();
                if(key.toLowerCase().contains(keyFromConfig)){
                        return cacheConf.getLong(k) * 1000;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return -1L;
    }

    KeyValue lastCall=new KeyValue("0","");
    @Override
    public boolean getFromCache(String url, JSONObject body, NetworkRequestCallback cb){
        String key = url;
        try {
            if(body!=null){
                key += "---"+hash(body.toString());//body +"---"+
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{

            Long call = Long.parseLong(lastCall.key);
            if(call+1000L> System.currentTimeMillis() && lastCall.val.equals(key)){
               utl.e("Cache","Skipping frquent call "+key);
               return true;
            }
            lastCall=new KeyValue(""+System.currentTimeMillis(),key);
        }catch (Exception e){
            e.printStackTrace();
        }
       return getFromCache(key,cb);
    }

    @Override
    public boolean getFromCache(String key, NetworkRequestCallback cb){
        KeyValue data= cache.getIfPresent(key);
        if(data!=null){
            utl.e("Cache","Retrieved from Cache "+key);
            Long allowedTimeout =getTimeout(key);
            Long entryTime=Long.parseLong(data.key);
            if( allowedTimeout==-1 || entryTime+allowedTimeout < System.currentTimeMillis()){
                cache.invalidate(key);
                return false;
            }
            try {
                cb.onSuccessString(data.val);
                cb.onSuccess(new JSONObject(data.val));
            } catch (Exception e) {
               utl.e("Cache",""+e.getMessage());
            }
            return true;
        }
        utl.e("Cache","Not found in Cache "+key);
        return false;
    }


    public static void invalidateCachesAndConfig(){
        CacheService.getInstance().invalidateAll();
        utl.setKey("amounts",null,App.getAppContext());
        utl.setKey("pamounts",null,App.getAppContext());

    }

}

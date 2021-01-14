package com.dotpot.app.utils;


import java.util.HashMap;

public class ObjectTransporter  {

    HashMap<String ,Object> objectHashMap=new HashMap<>();
    private static ObjectTransporter instance;
    public static ObjectTransporter getInstance()
    {
        if(instance==null)
            instance=new ObjectTransporter();
        return instance;
    }
    private ObjectTransporter(){}

    public void put(String tag,Object obj)
    {
        //utl.e("Mapper","put "+obj);
        objectHashMap.put(tag,obj);
    }

    public Object get(String tag)
    {
        if(objectHashMap.containsKey(tag))
        {
            //utl.e("Mapper","get "+objectHashMap.get(tag));
            return objectHashMap.get(tag);
        }
        return null;
    }

    public Object remove(String tag)
    {
        if(objectHashMap.containsKey(tag))
        {
            Object obj=objectHashMap.get(tag);
            objectHashMap.remove(tag);
            //utl.e("Mapper","remove "+obj);

            return obj;
        }
        return null;
    }
}

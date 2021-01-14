package com.dotpot.app.models;

public class KeyValue extends KeyValuePair<String,String> {

    public KeyValue(String key, String val) {
        super(key,val);
        this.key = key;
        this.val = val;
    }
}

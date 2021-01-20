package com.dotpot.app.models;

import com.dotpot.app.utl;

public class Transaction {

    private String id;

    public Transaction(){
        id = utl.uid(10);
    }

    public String getId() {
        return id;
    }
}

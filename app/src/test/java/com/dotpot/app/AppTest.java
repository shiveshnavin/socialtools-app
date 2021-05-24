package com.dotpot.app;

import org.junit.Test;

public class AppTest {

    @Test
    public void sample(){

        int x=10;
        while (x-->0){
            System.out.println(utl.randomInt(0,3));
        }

    }
}
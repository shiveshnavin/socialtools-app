package com.dotpot.app.interfaces;

/**
 * Created by shivesh on 14/7/17.
 */

public interface GenricDataCallback {


    /**
     * Generic data callback with success and failure code
     * @param message The result message
     * @param statusCode either 1 , -1 or 0
     */
    void onStart(String message,int statusCode);


}

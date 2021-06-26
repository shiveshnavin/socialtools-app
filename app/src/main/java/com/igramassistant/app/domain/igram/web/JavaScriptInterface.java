package com.igramassistant.app.domain.igram.web;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;

import com.igramassistant.app.interfaces.GenricDataCallback;
import com.igramassistant.app.ui.BaseActivity;
import com.igramassistant.app.utl;

public class JavaScriptInterface {
    private BaseActivity activity;
    GenricDataCallback onMessage;
    int no;

    public JavaScriptInterface(BaseActivity activity, GenricDataCallback onMessage) {
        this.activity = activity;
        this.onMessage = onMessage;
    }

    @JavascriptInterface
    public void log(String tag,String msg){
        utl.l(tag,msg);
        onMessage.onStart(""+tag+" > "+msg,no++);
    }
}

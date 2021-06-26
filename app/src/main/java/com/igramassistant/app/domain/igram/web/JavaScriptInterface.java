package com.igramassistant.app.domain.igram.web;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.igramassistant.app.interfaces.GenricDataCallback;
import com.igramassistant.app.ui.BaseActivity;
import com.igramassistant.app.utl;

public class JavaScriptInterface {
    private BaseActivity activity;
    public GenricDataCallback onMessage;
    public int no = 0;

    public JavaScriptInterface(BaseActivity activity, GenricDataCallback onMessage) {
        this.activity = activity;
        this.onMessage = onMessage;
    }

    @JavascriptInterface
    public void log(String text) {
        onMessage.onStart(text,no++);
    }

    @JavascriptInterface
    public void log(String tag,String msg){
        utl.l(tag,msg);
        onMessage.onStart(""+tag+" > "+msg,no++);
    }
}

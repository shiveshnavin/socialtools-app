package com.igramassistant.app.domain.igram.services;

import android.webkit.ValueCallback;

import com.igramassistant.app.App;
import com.igramassistant.app.interfaces.GenricDataCallback;
import com.igramassistant.app.ui.BaseActivity;
import com.igramassistant.app.ui.BaseFragment;
import com.igramassistant.app.utl;
import com.igramassistant.app.views.AdvancedWebView;

import java.io.IOException;


public class AutomatorService {
    public static final int RUN_JS = 100;
    public static final int LOAD_URL = 101;

    AdvancedWebView mWebView;
    BaseActivity baseActivity;
    BaseFragment baseFragment;
    HttpWebService httpWebService;

    public AutomatorService(AdvancedWebView mWebView, BaseActivity baseActivity, BaseFragment baseFragment) {
        this.mWebView = mWebView;
        this.baseActivity = baseActivity;
        this.baseFragment = baseFragment;
        httpWebService = new HttpWebService();
        httpWebService.setCallback((message, statusCode) -> {
            baseActivity.runOnUiThread(()->{
                handleMessage(message,statusCode);
            });
        });
    }

    public void handleMessage(String payload, int action) {
        baseActivity.runOnUiThread(()->{
            utl.toast(baseActivity,"Received : "+action);
        });

        switch (action){
            case AutomatorService.RUN_JS:
                injectJSAndRun(payload);
                break;
            case AutomatorService.LOAD_URL:
                loadUrl(payload);
                break;
        }

    }

    public void startRemoteDev() {
        try {
            httpWebService.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRemoteDev() {
        httpWebService.stop();
    }

    public void injectJSAndRun(String jsCode){

        mWebView.evaluateJavascript(jsCode, value -> {
            utl.l("auto",value);
        });

    }

    public void loadUrl(String url){

        mWebView.loadUrl(url);

    }

}

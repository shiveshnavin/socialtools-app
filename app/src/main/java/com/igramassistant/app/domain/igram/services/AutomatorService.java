package com.igramassistant.app.domain.igram.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.base.Strings;
import com.igramassistant.app.interfaces.GenricDataCallback;
import com.igramassistant.app.ui.BaseActivity;
import com.igramassistant.app.ui.BaseFragment;
import com.igramassistant.app.utl;
import com.igramassistant.app.views.AdvancedWebView;

import java.io.IOException;
import java.io.StringWriter;


public class AutomatorService {
    public static final int RUN_JS = 100;
    public static final int LOAD_URL = 101;
    public static final int GET_HTML = 102;
    public GenricDataCallback onMessage;

    AdvancedWebView mWebView;
    BaseActivity act;
    BaseFragment baseFragment;
    HttpWebService httpWebService;
    String lastResponse;

    public AutomatorService(AdvancedWebView mWebView, BaseActivity baseActivity, BaseFragment baseFragment) {
        this.mWebView = mWebView;
        this.act = baseActivity;
        this.baseFragment = baseFragment;
        httpWebService = new HttpWebService();
        httpWebService.setAutomatorService(this);
        httpWebService.setCallback((message, statusCode) -> {
            baseActivity.runOnUiThread(() -> {
                handleMessage(message, statusCode);
            });
        });
    }

    public void handleMessage(String payload, int action) {
        act.runOnUiThread(() -> {
            utl.toast(act, "Received : " + action);
        });

        switch (action) {
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

    public void injectJSAndRun(String jsCode) {

        mWebView.evaluateJavascript(jsCode, value -> {
            utl.l("auto", value);
        });

    }

    public void loadUrl(String url) {

        mWebView.loadUrl(url);

    }

    public void evaluate(String jsCode) {
        if (Strings.isNullOrEmpty(jsCode)) {
            jsCode = "(function() { return ('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>'); })();";
        }
        String finalJsCode = jsCode;
        act.runOnUiThread(() -> {
            mWebView.evaluateJavascript(finalJsCode
                    ,
                    response -> {
                        lastResponse = response.replace("\\u003C","<");
                        lastResponse = utl.unescapeJavaString(lastResponse);
                        lastResponse = lastResponse.substring(1,lastResponse.length()-1);
                    });
        });
    }
    public String toString(Object obj) {
        try (StringWriter w = new StringWriter();) {
            new ObjectMapper().configure(SerializationFeature.INDENT_OUTPUT, true).writeValue(w, obj);
            return w.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getEval() {
        return lastResponse;
    }



    String jquery;

    public void initInjections(){
        String url = mWebView.getUrl();
        if(jquery ==null)
            jquery = act.readAssetFile("jquery.js");
        injectJSAndRun(jquery);
        onMessage.onStart("Jquery Loaded!",0);

    }


    public void seemsHtmlLoaded() {
        initInjections();
    }

    public void seemsPageLoadfinished() {

    }
}

package com.igramassistant.app.domain.igram.fragments;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;

import com.igramassistant.app.R;
import com.igramassistant.app.domain.igram.services.AutomatorService;
import com.igramassistant.app.domain.igram.web.JavaScriptInterface;
import com.igramassistant.app.interfaces.GenricDataCallback;
import com.igramassistant.app.ui.BaseFragment;
import com.igramassistant.app.ui.activities.HomeActivity;
import com.igramassistant.app.utils.ShowHideLoader;
import com.igramassistant.app.views.AdvancedWebView;
import com.igramassistant.app.views.LoadingView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AutomatorFragment extends BaseFragment {

    ShowHideLoader showHideLoader;
    AutomatorService automatorService;
    TextView logger;
    GenricDataCallback onMessage;
    private String actionType;
    private NestedScrollView contScroll;
    private LinearLayout contLogin;
    private AdvancedWebView mWebView;
    private LinearLayout linearLayout;
    private Button request;
    private LoadingView loader;

    public static AutomatorFragment getInstance(String actionType) {
        AutomatorFragment mInstance = new AutomatorFragment();
        mInstance.actionType = actionType;
        return mInstance;
    }

    private void findViews(View root) {
        contScroll = (NestedScrollView) root.findViewById(R.id.contScroll);
        contLogin = (LinearLayout) root.findViewById(R.id.cont_login);
        mWebView = (AdvancedWebView) root.findViewById(R.id.webview);
        linearLayout = (LinearLayout) root.findViewById(R.id.linearLayout);
        request = (Button) root.findViewById(R.id.request);
        loader = (LoadingView) root.findViewById(R.id.loader);
        logger = root.findViewById(R.id.logger);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (HomeActivity) getActivity();
        init();

        View root = inflater.inflate(R.layout.fragment_automator, container, false);
        setUpToolbar(root);
        findViews(root);
        showHideLoader = ShowHideLoader.create().loader(loader).content(request);
        setTitle(getString(R.string.automator));

        root.findViewById(R.id.bgg).setOnClickListener(c -> {

        });
        logger.setMovementMethod(new ScrollingMovementMethod());

        contScroll.setOnTouchListener((v, event) -> {
            logger.getParent().requestDisallowInterceptTouchEvent(false);
            return false;
        });

        logger.setOnTouchListener((v, event) -> {
            logger.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
        prepare("https://www.instagram.com/accounts/login/");
        request.setOnClickListener(c -> {
            start("https://www.instagram.com/accounts/login/");
        });


        return root;

    }

    public void prepare(String base) {

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setDomStorageEnabled(true);

        WebSettings mWebSettings = mWebView.getSettings();

        mWebSettings.setSupportZoom(false);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowContentAccess(true);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if (!act.isNetworkAvailable()) {
                    mWebView.loadUrl("file:///android_asset/error.html");
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mWebView.loadUrl("file:///android_asset/error.html");
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                showHideLoader.loading();

            }

            @Override
            public void onPageFinished(WebView view, String url) {

                showHideLoader.loaded();
                automatorService.seemsPageLoadfinished();

            }


        });

        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(newProgress > 5){
                    automatorService.seemsHtmlLoaded();
                }
            }
        });


        onMessage = (msg, sno) -> {
            act.runOnUiThread(()->{
                logger.append(""+sno+". "+msg + "\n");
                final int scrollAmount = logger.getLayout().getLineTop(logger.getLineCount()) - logger.getHeight();
                logger.scrollTo(0, Math.max(scrollAmount, 0));
            });
        };
        JavaScriptInterface jsInterface = new JavaScriptInterface(act,onMessage);
        mWebView.addJavascriptInterface(jsInterface, "igram");
        mWebView.addJavascriptInterface(jsInterface, "console");

        automatorService = new AutomatorService(mWebView, act, this);
        automatorService.onMessage = onMessage;
        start(base);
    }

    public void start(String base) {
        automatorService.stopRemoteDev();
        showHideLoader.loading();
        mWebView.loadUrl(base);
        automatorService.startRemoteDev();
    }


    @Override
    public void onDestroy() {
        automatorService.stopRemoteDev();
        super.onDestroy();
    }
}
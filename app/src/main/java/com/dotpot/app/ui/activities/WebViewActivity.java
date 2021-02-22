package com.dotpot.app.ui.activities;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.binding.WalletViewModel;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utl;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;


public class WebViewActivity extends BaseActivity {

    public static final int REQUEST_SELECT_FILE = 100;
    public static final int REQUEST_PAYMENT = 129;
    private final static int FILECHOOSER_RESULTCODE = 1;
    public ValueCallback<Uri[]> uploadMessage;
    View loader;
    String title = "";
    Timer timer = new Timer();
    boolean completed = false;
    boolean isProcessing = false;
    private WebView mWebView;
    private ValueCallback<Uri> mUploadMessage;

    @Override
    public void loadStart() {
        loader.setAlpha(1.0f);
    }

    @Override
    public void loadStop() {

        loader.setAlpha(0.0f);
    }

    @Override
    public void load(boolean isloading) {

        if (isloading)
            loadStart();
        else
            loadStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        setUpToolbar();
        title = getIntent().getStringExtra("title");
        setTitle(title);
        loader = findViewById(R.id.loader);
        mWebView = findViewById(R.id.web);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccessFromFileURLs(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        mWebView.loadUrl(getIntent().getStringExtra("url"));

        load(true);
        WebSettings mWebSettings = mWebView.getSettings();

        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setSupportZoom(false);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setAllowContentAccess(true);


        ;//swipe.setEnabled(false);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                if(!isNetworkAvailable()){
                    mWebView.loadUrl("file:///android_asset/error.html");
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//                if (!BuildConfig.DEBUG)
                    mWebView.loadUrl("file:///android_asset/error.html");
            }


            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                load(true);
                if (url.contains("callback")) {
                    startCheck();
                }

                ;//swipe.setRefreshing(true);

            }

            @Override
            public void onPageFinished(WebView view, String url) {

                load(false);

            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {

                if (newProgress < 10) {
                    ;//swipe.setRefreshing(true);
                } else if (newProgress > 80)
                    ;//swipe.setRefreshing(false);
            }

            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(getApplicationContext(), ("Browse"), Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(getApplicationContext(), ("Upload Failed"), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public void onBackPressed() {

        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                utl.diagBottom(ctx, "", getString(R.string.are_you_sure_back), true, getString(R.string.confirm), () -> {
                    completed = true;
                    clearTimer();
                    setResult(Activity.RESULT_CANCELED, new Intent());
                    finish();
                });
            }

        } else if (id == R.id.reload) {
            mWebView.reload();
            ;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clearTimer() {
        try {
            timer.cancel();
        } catch (Exception e) {

        }
    }

    public void startCheck() {

        String orderId = getIntent().getStringExtra("orderId");
        if (orderId != null) {

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    if (!isProcessing) {
                        isProcessing = true;
                        restApi.checkTransaction(orderId, new GenricObjectCallback<JSONObject>() {
                            @Override
                            public void onEntity(JSONObject data) {
                                isProcessing = false;
                                String status = data.optString("status");
                                if (status.equals(Constants.TXN_SUCCESS)) {
                                    completed = true;
                                    clearTimer();
                                    Intent it = new Intent();
                                    it.putExtra("data", data.toString());
                                    setResult(Activity.RESULT_OK, it);
                                    WalletViewModel.getInstance().refresh(null);
                                    new Handler(Looper.getMainLooper())
                                            .postDelayed(()->{
                                                    WalletViewModel.getInstance().refresh(null);
                                            },3000);
                                    finish();
                                } else if (status.equals(Constants.TXN_FAILURE)) {
                                    String reason = "";
                                    try {
                                        JSONObject extra = new JSONObject(data.optString("extra"));
                                        reason = extra.optString("RESPMSG");
                                    } catch (Exception e) {
                                    }
                                    showError(getString(R.string.txn_failed) + reason);
                                }

                            }

                            @Override
                            public void onError(String message) {
                                isProcessing = false;
                                showError(message != null ? message : "");
                            }
                        });

                    }
                }
            };
            timer.scheduleAtFixedRate(timerTask, 0, 1000);

        }
    }

    public void showError(String message) {
        completed = true;
        clearTimer();
        setResult(Activity.RESULT_CANCELED, new Intent());
        utl.diagBottom(ctx, getString(R.string.error), getString(R.string.error_msg_try_again) + " " + message, true, getString(R.string.retry), this::finish);
    }


}

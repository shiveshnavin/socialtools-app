package com.igramassistant.app.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.igramassistant.app.BuildConfig;
import com.igramassistant.app.R;
import com.igramassistant.app.binding.GameViewModel;
import com.igramassistant.app.binding.GenericUserViewModel;
import com.igramassistant.app.binding.WalletViewModel;
import com.igramassistant.app.interfaces.GenricObjectCallback;
import com.igramassistant.app.services.DownloadOpenService;
import com.igramassistant.app.services.RestAPI;
import com.igramassistant.app.ui.BaseActivity;
import com.igramassistant.app.utl;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

public class HomeActivity extends BaseActivity {

    NavController navController;
    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navView = findViewById(R.id.nav_view);
        setUpToolbar();
        checkUpdate();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_wallet)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        GenericUserViewModel.getInstance()
                .updateLocalAndNotify(getApplicationContext(), utl.readUserData());
        WalletViewModel.getInstance().refresh(null);
        GameViewModel.getInstance().refreshAmounts(this);

        if (!isNetworkAvailable()) {
            utl.diagInfo(navView, getString(R.string.no_network), getString(R.string.ok), R.drawable.error, dialogInterface -> {

            });
        }
    }

    @Override
    public void onBackPressed() {
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);
        int seletedItemId = bottomNavigationView.getSelectedItemId();
        int backStack = fragmentManager.getBackStackEntryCount();
        if (backStack < 1) {
            if (R.id.navigation_dashboard == seletedItemId || R.id.navigation_wallet == seletedItemId) {
                navController.navigateUp();
            } else {
                super.onBackPressed();
            }
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == WebViewActivity.REQUEST_PAYMENT && resultCode == Activity.RESULT_OK) {
            utl.diagInfo(navView, getString(R.string.wallet_updated), null, R.drawable.ic_done_tick, dialogInterface -> {

            });
            navController.navigate(R.id.navigation_wallet);
        }

    }

    public void processNavigation(Intent intent) {


    }


    public void checkUpdate() {

        DownloadOpenService downloadOpenService = new DownloadOpenService();
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        int versionCode = BuildConfig.VERSION_CODE;
        RestAPI.getInstance().checkUpdate(versionCode, new GenricObjectCallback<JSONObject>() {
            @Override
            public void onEntity(JSONObject response) {
                int codeUpdated = response.optInt("versionCode", versionCode);
                if (codeUpdated > versionCode) {
                    Dialog d = utl.diagInfo(navView, response.optString("message"),
                            "Update", R.drawable.ic_logo,
                            new utl.ClickCallBack() {
                                @Override
                                public void done(DialogInterface dialogInterface) {
                                    try {

                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.optString("url")));
                                        startActivity(browserIntent);

//                                        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.REQUEST_INSTALL_PACKAGES) == PackageManager.PERMISSION_DENIED) {
//                                            downloadOpenService.downloadFile(act, response.optString("url"), getString(R.string.app_name) + ".apk");
//
//                                        } else {
//
//                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.optString("url")));
//                                            startActivity(browserIntent);
//                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    d.setCancelable(!response.optBoolean("mandatory", false));
                }
            }

            @Override
            public void onError(String message) {

            }
        });

    }

}
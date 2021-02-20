package com.dotpot.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dotpot.app.R;
import com.dotpot.app.binding.GenericUserViewModel;
import com.dotpot.app.binding.WalletViewModel;
import com.dotpot.app.utl;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends BaseActivity {

    NavController navController;
    BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        navView = findViewById(R.id.nav_view);
        setUpToolbar();
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
        if(!isNetworkAvailable()){
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


        if(requestCode == WebViewActivity.REQUEST_PAYMENT && resultCode == Activity.RESULT_OK){
            utl.diagInfo(navView,  getString(R.string.wallet_updated),null, R.drawable.ic_done_tick, dialogInterface -> {

            });
            navController.navigate(R.id.navigation_wallet);
        }

    }

    public void processNavigation(Intent intent){



    }


}
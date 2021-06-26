package com.igramassistant.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import com.igramassistant.app.R;
import com.igramassistant.app.binding.WalletViewModel;
import com.igramassistant.app.interfaces.GenricObjectCallback;
import com.igramassistant.app.models.Product;
import com.igramassistant.app.services.RestAPI;
import com.igramassistant.app.ui.BaseFragment;
import com.igramassistant.app.ui.activities.HomeActivity;
import com.igramassistant.app.utils.ShowHideLoader;
import com.igramassistant.app.utl;
import com.igramassistant.app.views.LoadingView;
import com.igramassistant.app.views.RoundRectCornerImageView;
import com.squareup.picasso.Picasso;

public class ShopDetailFragment extends BaseFragment {

    private static ShopDetailFragment mInstance;
    ShowHideLoader showHideLoader;
    private Product product;

    public static ShopDetailFragment getInstance() {
        if (mInstance == null)
            mInstance = new ShopDetailFragment();
        return mInstance;
    }


    public static ShopDetailFragment getInstance(Product item) {
       ShopDetailFragment shopDetailFragment = ShopDetailFragment.getInstance();
       shopDetailFragment.product = item;
       return shopDetailFragment;
    }


    private NestedScrollView contScroll;
    private LinearLayout contLogin;
    private RelativeLayout contRef;
    private LinearLayout contRefCard;
    private TextView itemTitle;
    private TextView itemAddTitle;
    private RoundRectCornerImageView image;
    private TextView itemDescription;
    private TextView bottomNote;
    private Button actionBtn;
    private TextView terms;
    private ConstraintLayout contPaytm;
    private TextView textPaytm;
    private RoundRectCornerImageView paytmImg;
    private LinearLayout linearLayout;
    private LoadingView loader;
    private Button request;
    private TextView available;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2021-05-23 17:29:32 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews(View rootView) {
        contScroll = (NestedScrollView)rootView.findViewById( R.id.contScroll );
        contLogin = (LinearLayout)rootView.findViewById( R.id.cont_login );
        contRef = (RelativeLayout)rootView.findViewById( R.id.contRef );
        contRefCard = (LinearLayout)rootView.findViewById( R.id.contRefCard );
        itemTitle = (TextView)rootView.findViewById( R.id.itemTitle );
        itemAddTitle = (TextView)rootView.findViewById( R.id.itemAddTitle );
        image = (RoundRectCornerImageView)rootView.findViewById( R.id.image );
        itemDescription = (TextView)rootView.findViewById( R.id.itemDescription );
        bottomNote = (TextView)rootView.findViewById( R.id.bottomNote );
        actionBtn = (Button)rootView.findViewById( R.id.actionBtn );
        terms = (TextView)rootView.findViewById( R.id.terms );
        contPaytm = (ConstraintLayout)rootView.findViewById( R.id.contPaytm );
        textPaytm = (TextView)rootView.findViewById( R.id.textPaytm );
        paytmImg = (RoundRectCornerImageView)rootView.findViewById( R.id.paytmImg );
        linearLayout = (LinearLayout)rootView.findViewById( R.id.linearLayout );
        loader = (LoadingView)rootView.findViewById( R.id.loader );
        request = (Button)rootView.findViewById( R.id.request );
        available = (TextView)rootView.findViewById( R.id.available );

        showHideLoader = ShowHideLoader.create().loader(loader).loader(request);
    }




    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        act = (HomeActivity) getActivity();
        init();

        View root = inflater.inflate(R.layout.fragment_shop_detail, container, false);
        setUpToolbar(root);
        findViews(root);
        setTitle(getString(R.string.awards));

        root.findViewById(R.id.bgg).setOnClickListener(c->{

        });

        WalletViewModel.getInstance().getWallet().observe(getViewLifecycleOwner(), wallet -> {
            available.setText(String.format(getString(R.string.available_award_balance_d), getString(R.string.currency),""+wallet.getWinningBalance()
            ,getString(R.string.currency),""+wallet.getCreditBalance()));
        });
        setUpProduct(product);
        return root;

    }

    public void setUpProduct(Product product){

        itemTitle.setText(product.getTitle());
        itemDescription.setText(product.getDesc());
        bottomNote.setText(product.expiry());
        actionBtn.setText(String.format("%s %s",getString(R.string.currency),product.getAmount()));
        terms.setText(product.getTerms());
        request.setText(String.format(getString(R.string.confirm_redeem),getString(R.string.currency),""+product.getAmount()));

        try {
            if(!utl.isEmpty(product.getImage())){
                Picasso.get().load(product.getImage()).into(image);
            }
        } catch (Exception e) {
            utl.e("Picasso err at 137");
        }

        if(product.getSecret()!=null){
            available.setVisibility(View.GONE);
            request.setVisibility(View.GONE);
            contPaytm.setVisibility(View.VISIBLE);
            textPaytm.setText(product.getSecret());
            contPaytm.setOnClickListener(c->{
                utl.copyToClipBoard("Redeem Coupon",product.getSecret(),ctx);
            });
        }
        else {
            contPaytm.setVisibility(View.GONE);
            request.setVisibility(View.VISIBLE);
            request.setOnClickListener(c->{

                request.setVisibility(View.GONE);
                loader.setVisibility(View.VISIBLE);
                RestAPI.getInstance().buyProduct(product.getId(), new GenricObjectCallback<Product>() {
                    @Override
                    public void onEntity(Product data) {
                        utl.diagInfo(request, getString(R.string.rewards_success), getString(R.string.view), R.drawable.ic_star_on, dialogInterface -> {

                        });


                        request.setVisibility(View.VISIBLE);
                        loader.setVisibility(View.GONE);

                        ShopDetailFragment.this.product = data;
                        setUpProduct(data);
                        WalletViewModel.getInstance().refresh(null);
                    }

                    @Override
                    public void onError(String message) {

                        request.setVisibility(View.VISIBLE);
                        loader.setVisibility(View.GONE);
                        utl.snack(act,message);
                    }
                });


            });
        }




    }

}
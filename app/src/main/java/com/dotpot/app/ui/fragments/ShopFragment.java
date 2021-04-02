package com.dotpot.app.ui.fragments;

import com.dotpot.app.App;
import com.dotpot.app.R;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Product;
import com.dotpot.app.models.Wallet;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.utl;
import com.google.common.base.Strings;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ShopFragment extends ViewListFragment<Product> {


    private String contextType = "";

    public static ShopFragment getInstance(String type) {
        ShopFragment mInstance = new ShopFragment();
        mInstance.contextType = type;
        return mInstance;
    }

    @Override
    void onReadyToReceiveItems() {

        get(0, new GenricObjectCallback<Product>() {
            @Override
            public void onEntitySet(ArrayList<Product> listItems) {
                reset(listItems);
            }

            @Override
            public void onError(String message) {
                utl.snack(act, R.string.error_msg);
            }
        });
    }

    @Override
    void renderCurrentItem(Product item, ViewListItemHolder itemUI, int pos) {
        itemUI.itemTitle.setText(item.getTitle());
//        itemUI.itemAddTitle.setText(Wallet.wrap(item.getAmount()));
        itemUI.itemDescription.setText(item.getDesc());
        if(!Strings.isNullOrEmpty(item.getImage())){
            Picasso.get().load(item.getImage()).placeholder(R.drawable.ic_logo).into(itemUI.image);
        }
        else {
            itemUI.image.setImageResource(R.drawable.ic_logo);
        }
        itemUI.actionBtn.setText(Wallet.wrap(item.getAmount()));
        itemUI.bottomNote.setText("Valid till "+
                utl.getDateTimeFormatted(new Date(item.getExpires())));

        itemUI.root.setOnClickListener(v->{

        });

    }

    @Override
    void loadNextPage(int pageNo, int sizeOfExistingList, GenricObjectCallback<Product> onNewItems) {
        get(sizeOfExistingList, onNewItems);
    }

    private void get(int sizeOfExistingList, GenricObjectCallback<Product> onNewItems) {

        if (contextType.equals("myshop")) {
            setTitle(getString(R.string.myshop));

            RestAPI.getInstance(App.getAppContext())
                    .getUserProducts(sizeOfExistingList, "", new GenricObjectCallback<Product>() {
                        @Override
                        public void onEntitySet(ArrayList<Product> listItems) {
                            onNewItems.onEntitySet(listItems);

                        }

                        @Override
                        public void onError(String message) {

                            utl.snack(act, R.string.error_msg);
                            onNewItems.onError(message);
                        }
                    });
        } else {
            if (contextType.equals("earn"))
                setTitle(getString(R.string.earn));
            else
                setTitle(getString(R.string.shop));

            RestAPI.getInstance(App.getAppContext())
                    .getProducts(sizeOfExistingList, contextType, new GenricObjectCallback<Product>() {
                        @Override
                        public void onEntitySet(ArrayList<Product> listItems) {
                            onNewItems.onEntitySet(listItems);

                        }

                        @Override
                        public void onError(String message) {

                            utl.snack(act, R.string.error_msg);
                            onNewItems.onError(message);
                        }
                    });
        }
    }


}


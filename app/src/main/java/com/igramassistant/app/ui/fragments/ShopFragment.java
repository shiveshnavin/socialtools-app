package com.igramassistant.app.ui.fragments;

import com.igramassistant.app.App;
import com.igramassistant.app.R;
import com.igramassistant.app.interfaces.GenricObjectCallback;
import com.igramassistant.app.models.ActionItem;
import com.igramassistant.app.models.Product;
import com.igramassistant.app.models.Wallet;
import com.igramassistant.app.services.EventBusService;
import com.igramassistant.app.services.RestAPI;
import com.igramassistant.app.utl;
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
        if(item.getType().equals(Product.TYPE_EARN)) {
            itemUI.actionBtn.setText(R.string.start);
        }
        else
            itemUI.actionBtn.setText(Wallet.wrap(item.getAmount()));

        itemUI.bottomNote.setText("Valid till "+
                utl.getDateTimeFormatted(new Date(item.getExpires())));


        itemUI.root.setOnClickListener(v->{
            if(item.getType().equals(Product.TYPE_EARN)){
                ActionItem actionItem = item.actionItem();
                if(actionItem!=null){
                    actionItem.act = act;
                    EventBusService.getInstance().doActionItem(actionItem);
                }
            }
            else {
                navService.startShopDetail(item,fragmentId);
            }
        });

        itemUI.actionBtn.setOnClickListener(v->{
            itemUI.root.callOnClick();
        });
    }

    @Override
    void loadNextPage(int pageNo, int sizeOfExistingList, GenricObjectCallback<Product> onNewItems) {
        get(sizeOfExistingList, onNewItems);
    }

    private void get(int sizeOfExistingList, GenricObjectCallback<Product> onNewItems) {

        if (contextType.equals("myshop")) {
            setTitle(getString(R.string.awards));

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


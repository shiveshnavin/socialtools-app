package com.dotpot.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dotpot.app.R;
import com.dotpot.app.adapters.GenriXAdapter;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.ui.BaseFragment;
import com.dotpot.app.views.RoundRectCornerImageView;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.List;

public abstract class ViewListFragment<T> extends BaseFragment {

    protected View loading;
    protected boolean isLoading = false;
    private GenriXAdapter<T> adapter;
    private RecyclerView listTransactions;
    private boolean isLastPage = false;
    private int pageNo = 0;
    private List<T> listData;
    private GenricObjectCallback<T> onNewItems = new GenricObjectCallback<T>() {
        @Override
        public void onEntitySet(ArrayList<T> newItems) {
            isLoading = false;
            loading.setVisibility(View.GONE);
            if (newItems.size() == 0) {
                isLastPage = true;
                return;
            }
            insertData(newItems);
        }

        @Override
        public void onError(String message) {
            isLoading = false;
            loading.setVisibility(View.GONE);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        act = (BaseActivity) getActivity();
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        listTransactions = root.findViewById(R.id.list);
        loading = root.findViewById(R.id.loader);
        setUpToolbar(root);
        setTitle("");
        listData = new ArrayList<>();
        setUpTransactionsList();
        return root;
    }

    public void insertData(ArrayList<T> newItems) {
        listData.addAll(newItems);
        adapter.notifyDataSetChanged();
    }

    public void reset(ArrayList<T> defaultItems) {
        loading.setVisibility(View.GONE);
        isLastPage = false;
        pageNo = 0;
        listData.clear();
        insertData(defaultItems);
    }

    public void setUpTransactionsList() {

        adapter = new GenriXAdapter<T>(getContext(), R.layout.row_style_shop, listData) {
            @Override
            public void onBindViewHolder(@NonNull final GenriXAdapter<T>.CustomViewHolder viewHolder, final int pos) {
                renderCurrentItem(listData.get(pos), ViewListItemHolder.create(viewHolder.base), pos);
            }
        };

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        listTransactions.setLayoutManager(layoutManager);
        listTransactions.setAdapter(adapter);


        onReadyToReceiveItems();


        listTransactions.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {


                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= FirebaseRemoteConfig.getInstance().getLong("page_size")) {
                        pageNo++;
                        isLoading = true;
                        loading.setVisibility(View.VISIBLE);
                        loadNextPage(pageNo, listData.size(), onNewItems);
                    }
                }


            }
        });


//        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
//            @Override
//            public void onScrollChanged()
//            {
//                View view = (View)listTransactions.getChildAt(listTransactions.getChildCount() - 1);
//
//                int diff = (view.getBottom() - (listTransactions.getHeight() + listTransactions
//                        .getScrollY()));
//
//                if (diff == 0) {
//
//                    boolean isLoading= loading.getVisibility() == View.VISIBLE;
//                    if (!isLoading && !isLastPage) {
//                        loading.setVisibility(View.VISIBLE);
//                        pageNo++;
//                        loadNextPage(pageNo, listData.size(), onNewItems);
//                    }
//                }
//            }
//        });

    }

    abstract void renderCurrentItem(final T item, final ViewListItemHolder viewHolder, final int pos);

    abstract void loadNextPage(int pageNo, int sizeOfExistingList, GenricObjectCallback<T> onNewItems);

    abstract void onReadyToReceiveItems();

    public static class ViewListItemHolder {
        public final RelativeLayout contRef;
        public final LinearLayout contRefCard;
        public final TextView itemTitle;
        public final TextView itemAddTitle;
        public final RoundRectCornerImageView image;
        public final TextView itemDescription;
        public final TextView bottomNote;
        public final Button actionBtn;

        private ViewListItemHolder(RelativeLayout contRef, LinearLayout contRefCard, TextView itemTitle, TextView itemAddTitle, RoundRectCornerImageView image, TextView itemDescription, TextView bottomNote, Button actionBtn) {
            this.contRef = contRef;
            this.contRefCard = contRefCard;
            this.itemTitle = itemTitle;
            this.itemAddTitle = itemAddTitle;
            this.image = image;
            this.itemDescription = itemDescription;
            this.bottomNote = bottomNote;
            this.actionBtn = actionBtn;
        }

        public static ViewListItemHolder create(View rootView) {
            RelativeLayout contRef = (RelativeLayout) rootView.findViewById(R.id.contRef);
            LinearLayout contRefCard = (LinearLayout) rootView.findViewById(R.id.contRefCard);
            TextView itemTitle = (TextView) rootView.findViewById(R.id.itemTitle);
            TextView itemAddTitle = (TextView) rootView.findViewById(R.id.itemAddTitle);
            RoundRectCornerImageView image = (RoundRectCornerImageView) rootView.findViewById(R.id.image);
            TextView itemDescription = (TextView) rootView.findViewById(R.id.itemDescription);
            TextView bottomNote = (TextView) rootView.findViewById(R.id.bottomNote);
            Button actionBtn = (Button) rootView.findViewById(R.id.actionBtn);
            return new ViewListItemHolder(contRef, contRefCard, itemTitle, itemAddTitle, image, itemDescription, bottomNote, actionBtn);
        }
    }
}
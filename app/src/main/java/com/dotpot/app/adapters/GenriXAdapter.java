package com.dotpot.app.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dotpot.app.R;

import java.util.List;


public class GenriXAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public List<T> itemList;
    private Context mContext;
    private int layout=-1;

    public GenriXAdapter(Context context,@LayoutRes int layout, List<T> feedItemList) {
        this.itemList = feedItemList;
        this.mContext = context;
        this.layout=layout;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).
                inflate(layout!=-1?layout: R.layout.utl_row_feed,viewGroup, false);

        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        final int pos=viewHolder.getAdapterPosition();
        final T item= itemList.get(pos);
    }

    public void update(List<T> newList)
    {
        itemList.clear();;
        itemList.addAll(newList);
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return (null != itemList ? itemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder
    {

        public View base;

        public EditText editText(@IdRes int id)
        {
            EditText ed=(EditText) base.findViewById(id);
            return ed;
        }

        public TextView textView(@IdRes int id)
        {
            TextView ed=(TextView) base.findViewById(id);
            return ed;
        }

        public Button button(@IdRes int id)
        {
            Button ed=(Button) base.findViewById(id);
            return ed;
        }

        public ImageView imageView(@IdRes int id)
        {
            ImageView ed=(ImageView) base.findViewById(id);
            return ed;
        }

        public View view(@IdRes int id)
        {

            return  base.findViewById(id);
        }


        public CustomViewHolder(View v) {
            super(v);

            base=v;

        }
    }



    public static void setTextOrHide(String text, TextView tv) {
        if (text != null && text.length() > 1) {
            tv.setVisibility(View.VISIBLE);
            tv.setText(Html.fromHtml(text));
        } else {
            tv.setVisibility(View.INVISIBLE);
        }
    }



}


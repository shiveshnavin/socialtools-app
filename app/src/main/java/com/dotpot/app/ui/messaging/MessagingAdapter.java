package com.dotpot.app.ui.messaging;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dotpot.app.R;
import com.dotpot.app.interfaces.GenricDataCallback;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utl;
import com.squareup.picasso.Picasso;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class MessagingAdapter extends RecyclerView.Adapter<MessagingAdapter.CustomViewHolder> {
    public List<InAppMessage> feedItemList;
    private Context ctx;
    private GenricUser user;
    private MessagingService messagingService;

    public MessagingAdapter(List<InAppMessage> feedItemList, Context ctx, GenricUser user, MessagingService messagingService) {
        this.feedItemList = feedItemList;
        this.ctx = ctx;
        this.user = user;
        this.messagingService = messagingService;
    }

    public HashMap<String, Integer> userColorMap = null;
    public ArrayList<Integer> availableColors = null;


    public int[] generateColorArray(int size) {
        int result[] = new int[size];
        Random rnd = new Random(System.currentTimeMillis());

        int i = 0;
        for (; i < size; i++) {
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            result[i] = color;
        }

        return result;
    }



    public Integer getUserColor(Context context, String userid) {
        if (availableColors == null) {
            availableColors = new ArrayList<>();
            int size = 15;
            int[] rainbow = generateColorArray(size);
            for (int i1 : rainbow) {
                availableColors.add(i1);
            }
        }
        if (userColorMap == null) {
            userColorMap = new HashMap<>();
        }
        if (!userColorMap.containsKey(userid)) {
            userColorMap.put(userid, availableColors.get(0));
            availableColors.remove(0);

        }
        return userColorMap.get(userid);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.messaging_row_chat,viewGroup, false);

        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder vh, int i) {

        final int pos=vh.getAdapterPosition();
        final InAppMessage cm=feedItemList.get(pos);

        String blk = "";

        vh.message.setText(cm.getRefinedMessage());


        if (cm.getSenderId().equals(user.getId())) {
            cm.setSenderName("You");
            vh.sender.setTextColor(getUserColor(vh.sender.getContext(), cm.getSenderId()));


                vh.sender.setVisibility(View.GONE);
                vh.root.setGravity(Gravity.RIGHT);
                vh.contAll.setBackground(ResourceUtils.getDrawable(R.drawable.messaging_rounded_green2_chat_filled));

            vh.seen.setAlpha(0f);
        } else {
            vh.root.setGravity(Gravity.LEFT);
                vh.sender.setTextColor(ResourceUtils.getColor(R.color.colorPrimary));
            vh.sender.setVisibility(View.GONE);
                vh.contAll.setBackground(ResourceUtils.getDrawable(R.drawable.messaging_rounded_grey_chat_filled));
            if (cm.getRead()==1) {
                vh.seen.setAlpha(0f);
            } else {
                vh.seen.setAlpha(1.0f);
                vh.seen.animate().alpha(0.0f).setDuration(5000);
            }

        }
        vh.sender.setTextColor(getUserColor(vh.sender.getContext(), cm.getSenderId()));

        if (cm.senderNameOnly() !=null && cm.senderNameOnly().length() < 20)
            for (int k = cm.senderNameOnly().length(); k < 20; k++) {
                blk += " ";
            }

        // utl.e("blk",""+blk.length());
        if(cm.senderNameOnly() !=null )
        vh.sender.setText(cm.senderNameOnly() + blk);
        else
            vh.sender.setText("");

        vh.time.setText(cm.timeFormatted());


        if (cm.getAttachmentUrl() != null && cm.getAttachmentUrl().startsWith("http")) {
            vh.image_msg.setVisibility(View.VISIBLE);
            vh.cardImage.setVisibility(View.VISIBLE);

            Picasso.get().load(cm.getAttachmentUrl()).placeholder(R.drawable.loading_bg)
                    .error(R.drawable.broken_image).
                    into(vh.image_msg);
            vh.image_msg.setOnClickListener((v) -> {


                ImageLoader<String> imageLoader = new ImageLoader<String>() {
                    @Override
                    public void loadImage(ImageView imageView, String image) {


                        Picasso.get().load(image).placeholder(R.drawable.loading_bg).
                                error(R.drawable.broken_image).
                                into(imageView);

                    }
                };

                vh.image_msg.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        showDelete(cm);


                        return true;
                    }
                });

                String[] urls = {cm.getAttachmentUrl()};
                StfalconImageViewer.Builder<String> builder
                        = new StfalconImageViewer.Builder<String>(ctx, urls,
                        imageLoader)
                        .withStartPosition(0)
                        .withTransitionFrom(vh.image_msg);

                builder.build().show(true);


            });
            if (vh.message.getText().toString().length() < 1) {
                vh.message.setVisibility(View.GONE);
            }
        } else {
            vh.image_msg.setVisibility(View.GONE);
        }

        vh.root.setOnLongClickListener((view -> {


            showDelete(cm);


            return true;
        }));


    }


    Dialog lastDialog;
    public void showDelete(InAppMessage cm) {

        if(!user.isAdmin())
            return;

        String menus[] = {"Delete", "Copy"};

        utl.diagBottomMenu(ctx, "", menus, null, true, "Cancel", new GenricDataCallback() {
            @Override
            public void onStart(String data1, int data2) {


                if (data1.equals(menus[0])) {

                    if (Calendar.getInstance().getTimeInMillis()
                            - cm.getDateTime() > 1800000 && !user.isAdmin()) {
                        lastDialog = utl.diagInfo2(ctx, ResourceUtils.getString(R.string.del_not_possible_dialog), "Dismiss", R.drawable.ic_logo, new utl.ClickCallBack() {
                            @Override
                            public void done(DialogInterface dialogInterface) {


                            }
                        });
                    } else {
                        utl.diagBottom(ctx, "", ResourceUtils.getString(R.string.del_promt), true, "Delete",
                                () -> messagingService.deleteMessage(cm.getId()));
                    }

                } else {

                    ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("message",
                            "(" + cm.senderNameOnly() + ") : " + cm.getRefinedMessage()
                            + (cm.getAttachmentUrl() != null && cm.getAttachmentUrl().length() > 4 ? "  \n\n" + cm.getAttachmentUrl() : "")
                    );
                    clipboard.setPrimaryClip(clip);
                    utl.toast(ctx, "Copied to clipboard !");


                }

            }
        });
    }

    public void addAFew(ArrayList<InAppMessage> list)
    {

        Collections.reverse(list);
        feedItemList.addAll(0,list);
        this.notifyDataSetChanged();
    }

    public void updateList(ArrayList<InAppMessage> list)
    {
        feedItemList.clear();
        feedItemList.addAll(list);
        this.notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder
    {

        public View base;
        public RelativeLayout contAll;
        public ImageView seen,image_msg;
        public LinearLayout contText,root;
        public CardView cardImage;
        public TextView sender;
        public TextView message;
        public View quoted;
        public TextView time;

        private void findViews(View rootView) {
            base = rootView;

            root=rootView.findViewById(R.id.root);
            cardImage=rootView.findViewById(R.id.cardImage);
            contAll = (RelativeLayout)rootView.findViewById( R.id.cont_all );
            seen = (ImageView)rootView.findViewById( R.id.seen );
            image_msg = (ImageView)rootView.findViewById( R.id.image_msg );
            contText = (LinearLayout)rootView.findViewById( R.id.cont_text );
            sender = (TextView)rootView.findViewById( R.id.sender );
            message = (TextView)rootView.findViewById( R.id.message );
            quoted =   rootView.findViewById(R.id.reply_cont_all);
            time = (TextView)rootView.findViewById( R.id.time );
        }



        public CustomViewHolder(View v) {
            super(v);

           findViews(v);


        }



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


    }



}


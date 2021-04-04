package com.dotpot.app.ui.messaging;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dotpot.app.R;
import com.dotpot.app.interfaces.GenricCallback;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.services.DBService;
import com.dotpot.app.services.InAppNavService;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.ui.BaseFragment;
import com.dotpot.app.utl;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;

public class MessagingFragment extends BaseFragment {

    public BaseActivity act;
    public Context ctx;
    public InAppNavService navService;
    public int fragmentId;
    public GenricUser user;
    public TextView title ;
    public ImageView logo;
    public String groupId;
    public String recieverId;

    MessagingService messagingService;
    MessagingAdapter messagingAdapter;
    ArrayList<InAppMessage> messages = new ArrayList<>();

    private static MessagingFragment mInstance;
    public static MessagingFragment getInstance(){
        if(mInstance==null)
            mInstance = new MessagingFragment();
        return mInstance;
    }


    private final GenricObjectCallback<InAppMessage> onNewMessages = new GenricObjectCallback<InAppMessage>() {
        @Override
        public void onEntitySet(ArrayList<InAppMessage> listItems) {
            loader.setVisibility(View.GONE);
            messages.addAll(listItems);
            messagingAdapter.notifyDataSetChanged();
            list.smoothScrollToPosition(messages.size());
            listItems.stream().forEach(c-> DBService.getInstance(ctx).makeSeen(c.id));
        }
    };
    private final GenricObjectCallback<InAppMessage> onOldMessages = new GenricObjectCallback<InAppMessage>() {
        @Override
        public void onEntitySet(ArrayList<InAppMessage> listItems) {
            loader.setVisibility(View.GONE);
            messages.addAll(listItems);
            messagingAdapter.notifyDataSetChanged();
        }
    };
    private final GenricCallback onResetSignal = () -> messages.clear();


    public void setUpToolbar(View root){
        title = root.findViewById(R.id.title);
        logo = root.findViewById(R.id.logo);
        if(logo!=null)
        {
            logo.setOnClickListener(v->{
                getActivity().onBackPressed();
            });
        }
    }


    public void setTitle(String titl){
        if(title!=null)
            title.setText(titl);
    }

    public void init(){
        act = (BaseActivity) getActivity();
        ctx = getContext();
        navService = new InAppNavService(act);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        init();
        fragmentId = container.getId();
        user = utl.readUserData();
        recieverId = FirebaseRemoteConfig.getInstance().getString("support_user_id");

        View root = inflater.inflate(R.layout.messaging_fragment, container, false);
        findViews(root);
        setUpToolbar(root);
        setTitle("Chat");
        groupId = "Support:"+user.getEmail();
        messagingService = new MessagingService(groupId,ctx,getViewLifecycleOwner(),onNewMessages,onOldMessages,onResetSignal);
        messagingAdapter = new MessagingAdapter(messages,ctx,user,messagingService);

        list.setAdapter(messagingAdapter);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(ctx);
//        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        list.setLayoutManager(mLayoutManager);
        loader.setVisibility(View.VISIBLE);

        messagingService.fetchMessages(DBService.getInstance(ctx).getLatestMessage(groupId));

        send.setOnClickListener(view -> {
            loader.setVisibility(View.VISIBLE);
            messagingService.sendMessage(user.getId(),recieverId,messageEdit.getText().toString());
            messageEdit.setText("");
        });
        return root;
    }

    public void setActivityAndContext(BaseActivity act) {
        this.act = act;
        this.ctx = ctx;
        this.navService = act.inAppNavService;
    }

    private RelativeLayout contChat;
    private RecyclerView list;
    private LinearProgressIndicator loader;
    private RelativeLayout messgCont;
    private EditText messageEdit;
    private ImageView attach;
    private ImageView send;
    private ConstraintLayout toolCont;
    private LinearLayout headCont;


    private void findViews(View root) {
        contChat = (RelativeLayout)root.findViewById( R.id.cont_chat );
        list = (RecyclerView)root.findViewById( R.id.list );
        loader = (LinearProgressIndicator)root.findViewById( R.id.loader );
        messgCont = (RelativeLayout)root.findViewById( R.id.messg_cont );
        messageEdit = (EditText)root.findViewById( R.id.message_edit );
        attach = (ImageView)root.findViewById( R.id.attach );
        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        send = (ImageView)root.findViewById( R.id.send );
        toolCont = (ConstraintLayout)root.findViewById( R.id.tool_cont );
        logo = (ImageView)root.findViewById( R.id.logo );
        headCont = (LinearLayout)root.findViewById( R.id.head_cont );
        title = (TextView)root.findViewById( R.id.title );
    }

}



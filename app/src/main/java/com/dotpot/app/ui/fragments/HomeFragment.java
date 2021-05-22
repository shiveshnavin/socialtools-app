package com.dotpot.app.ui.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;

import com.dotpot.app.App;
import com.dotpot.app.Constants;
import com.dotpot.app.R;
import com.dotpot.app.adapters.GenriXAdapter;
import com.dotpot.app.binding.HomeViewModel;
import com.dotpot.app.binding.NotificationsViewModel;
import com.dotpot.app.binding.WalletViewModel;
import com.dotpot.app.models.ActionItem;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.models.Wallet;
import com.dotpot.app.services.EventBusService;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.ui.BaseFragment;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utils.ShowHideLoader;
import com.dotpot.app.utl;
import com.dotpot.app.views.sparkbutton.SparkButton;
import com.google.common.base.Strings;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tyrantgit.explosionfield.ExplosionField;

public class HomeFragment extends BaseFragment {
    Handler handler = new Handler();

    GenriXAdapter<utl.NotificationMessage> notificationMessageGenriXAdapter;
    boolean visible = true;
    private HomeViewModel homeViewModel;
    private GenriXAdapter<ActionItem> actionAdapter;
    private GenriXAdapter<GenricUser> leaderBoardAdapter;
    private ImageView poster;
    private View playIcon;
    private TextView text_home;
    private TextView bottomText;
    private ConstraintLayout contNotif;
    private TextView notifAction;
    private TextView notifTxt;
    private LinearLayout contNotifList;
    private RecyclerView notifList;
    private TextView clearNotifs;
    private ConstraintLayout contRef;
    private TextView weeklyLeaderboardtxt;
    private RecyclerView listLeaderboard;
    private RecyclerView listItems;
    private View loader;
    private SparkButton sparkButton;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2021-02-21 15:51:52 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews(View root) {
        poster = (ImageView) root.findViewById(R.id.poster);
        playIcon = root.findViewById(R.id.playIcon);
        text_home = (TextView) root.findViewById(R.id.text_home);
        bottomText = (TextView) root.findViewById(R.id.bottomText);
        contNotif = (ConstraintLayout) root.findViewById(R.id.contNotif);
        notifAction = (TextView) root.findViewById(R.id.notifAction);
        notifTxt = (TextView) root.findViewById(R.id.notifTxt);
        contNotifList = (LinearLayout) root.findViewById(R.id.contNotifList);
        notifList = (RecyclerView) root.findViewById(R.id.notifList);
        clearNotifs = (TextView) root.findViewById(R.id.clearNotifs);
        contRef = (ConstraintLayout) root.findViewById(R.id.contRef);
        weeklyLeaderboardtxt = (TextView) root.findViewById(R.id.weeklyLeaderboardtxt);
        listLeaderboard = (RecyclerView) root.findViewById(R.id.listLeaderboard);
        listItems = (RecyclerView) root.findViewById(R.id.listItems);
        loader = root.findViewById(R.id.loader);
        sparkButton = root.findViewById(R.id.sparkButton);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        visible = false;
    }

    public void flashPlayBtn() {

        sparkButton.playAnimation();

        utl.animate_shake(playIcon);

        sparkButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (visible)
                    flashPlayBtn();
            }
        }, 2000);

    }

    @SuppressLint("ClickableViewAccessibility")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        findViews(root);
        new DiaglogReferral(root, this);
        if (act.fragmentManager.getBackStackEntryCount() > 0)
            act.fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        final TextView textView = root.findViewById(R.id.text_home);
        ShowHideLoader.create().content(listItems).loader(loader).loading();

        WalletViewModel.getInstance().getWallet().observe(getViewLifecycleOwner(), wallet -> {
            ShowHideLoader.create().content(listItems).loader(loader).loaded();
            if (homeViewModel == null) {
                homeViewModel =
                        new ViewModelProvider(HomeFragment.this).get(HomeViewModel.class);
                homeViewModel.refresh(act);
                homeViewModel.getActions().observe(getViewLifecycleOwner(), (result) -> setUpActionList(result));
                homeViewModel.getLeaderboard().observe(getViewLifecycleOwner(), (result) -> {
                    try {
                        setUpLeaderboardList(result);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
            setUpActionList(homeViewModel.getActions().getValue());
        });


        NotificationsViewModel.getInstance().getNotifications()
                .observe(getViewLifecycleOwner(),
                        this::setUpNotifications);
        NotificationsViewModel.getInstance().refresh();

        final Animation press = AnimationUtils.loadAnimation(ctx, R.anim.motion_play_anim);
        final Animation release = AnimationUtils.loadAnimation(ctx, R.anim.motion_play_anim);
        release.setInterpolator(paramFloat -> Math.abs(paramFloat - 1f));
        ExplosionField explosionField = ExplosionField.attach2Window(getActivity());


        playIcon.setOnTouchListener((view, event) -> {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    playIcon.animate().scaleX(1.5f)
                            .scaleY(1.5f).setDuration(300)
//                            .rotation(360f)
                            .start();
                    return true;
                case MotionEvent.ACTION_UP:
                    playIcon.animate().scaleX(1)
                            .scaleY(1f).setDuration(300)
//                            .rotation(0f)
                            .start();
                    sparkButton.playAnimation();
                    navService.startSelectGameAmount(fragmentId, null);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    playIcon.animate().scaleX(1f)
                            .scaleY(1f).setDuration(300)
//                            .rotation(0f)
                            .start();
                    break;
                default:
                    break;
            }


            return false;
        });
        flashPlayBtn();
        contNotif.setOnClickListener(view -> {

        });

        return root;
    }

    private void setUpNotifications(ArrayList<utl.NotificationMessage> notificationMessages) {


        if (notificationMessages.size() > 0) {
            notifTxt.setText(String.format(ResourceUtils.getString(R.string.you_have_notifications), notificationMessages.size()));
//            notifAction.setText(""+notificationMessages.size());
            notifAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (notifAction.getTag() == null) {
                        setDrawable(notifAction, R.drawable.ic_arrow_down);
//                        notifAction.setCompoundDrawables(ResourceUtils.getDrawable(R.drawable.ic_cancel_white),
//                                null,null,null);
                        notifAction.setTag("shown");
                        try {
                            showNotifications(NotificationsViewModel.getInstance().getNotifications().getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        setDrawable(notifAction, R.drawable.ic_notifications_black_24dp);
//                        notifAction.setCompoundDrawables(ResourceUtils.getDrawable(R.drawable.ic_notifications_black_24dp),null,null,null);
                        notifAction.setTag(null);

                        try {
                            hideNotifications();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                }

            });
            if (notificationMessageGenriXAdapter != null) {
                notificationMessageGenriXAdapter.itemList.clear();
                notificationMessageGenriXAdapter.itemList.addAll(notificationMessages);
                notificationMessageGenriXAdapter.notifyDataSetChanged();
            }
            contNotif.setOnClickListener(view -> notifAction.callOnClick());
            clearNotifs.setOnClickListener(view -> {
                utl.NotificationMessage.deleteAll(getContext());
                notifAction.callOnClick();
                NotificationsViewModel.getInstance().refresh();
                try {
                    notificationMessageGenriXAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        } else {
            notifAction.setOnClickListener(view1 -> {
            });
            notifTxt.setText(String.format(ResourceUtils.getString(R.string.you_have_notifications),
                    ResourceUtils.getString(R.string.no)));
        }
    }

    private void setDrawable(TextView titleTextView, @DrawableRes int icon) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Drawable leftDrawable = AppCompatResources
                    .getDrawable(getContext(), icon);
            titleTextView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        } else {
            //Safely create our VectorDrawable on pre-L android versions.
            Drawable leftDrawable = VectorDrawableCompat
                    .create(getContext().getResources(), icon, null);
            titleTextView.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, null, null);
        }
    }

    private void setUpActionList(List<ActionItem> actionList) {

        actionAdapter = new GenriXAdapter<ActionItem>(getContext(), R.layout.row_card_menu, actionList) {
            @Override
            public void onBindViewHolder(@NonNull GenriXAdapter.CustomViewHolder viewHolder, int i) {

                final int pos = viewHolder.getAdapterPosition();
                final GenriXAdapter.CustomViewHolder vh = (CustomViewHolder) viewHolder;
                final ActionItem item = actionList.get(pos);

                if (!utl.isEmpty(item.accentColorId)) {
                    vh.button(R.id.addBtn).setTextColor(Color.parseColor(item.accentColorId));
                } else {
                    vh.button(R.id.addBtn).setTextColor(ResourceUtils.getColor(R.color.colorIcon));
                }

                if (!utl.isEmpty(item.textAction)) {
                    vh.button(R.id.addBtn)
                            .setText(item.textAction);
                }

                if (!utl.isEmpty(item.subTitle)) {
                    vh.textView(R.id.yourWalletBalanceTxt)
                            .setText(item.subTitle);
                }
                if (!utl.isEmpty(item.rightTop)) {
                    vh.textView(R.id.currency)
                            .setText(item.rightTop);
                    if (item.rightTop.equals("skip")) {
                        vh.textView(R.id.currency).setVisibility(View.GONE);
                    } else {
                        vh.textView(R.id.currency).setVisibility(View.VISIBLE);
                    }
                }


                Wallet wallet = WalletViewModel.getInstance().getWallet().getValue();
                if (wallet != null) {
                    switch (item.actionType) {
                        case Constants
                                .ACTION_ADD_CREDITS:
                            vh.textView(R.id.walletBalance).setText("" + wallet.getCreditBalance());

                            break;
                        case Constants
                                .ACTION_REDEEM_OPTIONS:
                            vh.textView(R.id.walletBalance).setText("" + wallet.getWinningBalance());

                            break;
                        default:
                            if (!utl.isEmpty(item.title)) {
                                vh.textView(R.id.walletBalance)
                                        .setText(item.title);
                            } else
                                vh.textView(R.id.walletBalance).setText("");
                    }
                }
                vh.itemView.setOnClickListener(view -> {
                    EventBusService.getInstance().doActionItem(item);
                });
                vh.view(R.id.addBtn).setOnClickListener(v -> {
                    EventBusService.getInstance().doActionItem(item);
                });
            }

        };
        listItems.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        listItems.setAdapter(actionAdapter);


    }

    private void setUpLeaderboardList(List<GenricUser> genricUsers) {

        if (genricUsers == null) {
            return;
        }
        try {
            Collections.sort(genricUsers, (user, t1) -> {
                Long aw1 = Long.parseLong(user.getAbout());
                Long aw2 = Long.parseLong(t1.getAbout());

                return aw2.compareTo(aw1);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        leaderBoardAdapter = new GenriXAdapter<GenricUser>(getContext(), R.layout.row_user, genricUsers) {
            @Override
            public void onBindViewHolder(@NonNull GenriXAdapter.CustomViewHolder viewHolder, int i) {

                final int pos = viewHolder.getAdapterPosition();
                final GenriXAdapter.CustomViewHolder vh = (CustomViewHolder) viewHolder;
                final GenricUser leader = genricUsers.get(pos);

                vh.textView(R.id.username).setText(leader.getName());
                vh.textView(R.id.rank).setText(leader.getRank());
                vh.textView(R.id.earnings).setText(ResourceUtils.getString(R.string.currency) + " " + leader.getAbout());
                try {
                    if (!Strings.isNullOrEmpty(leader.getImage()))
                        Picasso.get().load(leader.getImage())
                                .placeholder(R.drawable.account)
                                .into(vh.imageView(R.id.userImage));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                vh.itemView.setOnClickListener(view -> {

                });
            }
        };

        listLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        listLeaderboard.setAdapter(leaderBoardAdapter);


    }

    public void hideNotifications() {

        contNotifList.animate()
                .translationY(-100)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        contNotifList.setVisibility(View.GONE);
                    }
                });
    }

    public void showNotifications(ArrayList<utl.NotificationMessage> notificationMessages) {

        contNotifList.setVisibility(View.VISIBLE);
        contNotifList.animate()
                .translationY(0)
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                    }
                });

        Collections.sort(notificationMessages, (t0, t1) -> t1.time.compareTo(t0.time));


        if (notificationMessageGenriXAdapter == null) {

            ArrayList<utl.NotificationMessage> listData = new ArrayList<>();
            listData.addAll(notificationMessages);
            notificationMessageGenriXAdapter = new GenriXAdapter<utl.NotificationMessage>(ctx, R.layout.utl_row_notification, listData) {
                @Override
                public void onBindViewHolder(@NonNull final GenriXAdapter.CustomViewHolder viewHolder, int i) {

                    final utl.NotificationMessage nof = notificationMessages.get(viewHolder.getAdapterPosition());
                    final CustomViewHolder vh = (CustomViewHolder) viewHolder;

                    vh.textView(R.id.title).setText(nof.title);
                    if (nof.type.equals(utl.NotificationMessage.TYPE_REQUEST)) {
                        vh.textView(R.id.title).setTextColor(ResourceUtils.getColor(R.color.material_green_500));
                        vh.imageView(R.id.image).setImageResource(nof.icon);
                        vh.imageView(R.id.image).setColorFilter(ContextCompat.getColor(ctx, R.color.material_green_500),
                                android.graphics.PorterDuff.Mode.SRC_IN);

                    } else if (nof.type.equals(utl.NotificationMessage.TYPE_REPLY)) {
                        vh.textView(R.id.title).setTextColor(ResourceUtils.getColor(R.color.material_orange_500));
                        vh.imageView(R.id.image).setImageResource(R.drawable.ic_question_faq);
                        vh.imageView(R.id.image).setColorFilter(ContextCompat.getColor(ctx, R.color.material_orange_500),
                                android.graphics.PorterDuff.Mode.SRC_IN);
                    } else {
                        vh.textView(R.id.title).setTextColor(ResourceUtils.getColor(R.color.colorPrimary));
                        vh.imageView(R.id.image).setImageResource(nof.icon);
                        vh.imageView(R.id.image).setColorFilter(ContextCompat.getColor(ctx, R.color.colorPrimary),
                                android.graphics.PorterDuff.Mode.SRC_IN);
                    }

                    vh.textView(R.id.message).setText(nof.message);
                    vh.textView(R.id.time).setText(nof.getTimeFormatted());
                    vh.imageView(R.id.image).setImageResource(nof.icon);
                    vh.base.setOnClickListener((v) -> {
//                        startActivity(it);
                    });
//                Intent it = nof.getIntent(ctx);
//                if (it != null)
//                    vh.base.setOnClickListener((v) -> {
//                        startActivity(it);
//                    });

                }
            };

            notifList.setLayoutManager(new LinearLayoutManager(ctx));
            notifList.setAdapter(notificationMessageGenriXAdapter);

        } else {
            notificationMessageGenriXAdapter.itemList.clear();
            notificationMessageGenriXAdapter.itemList.addAll(notificationMessages);
            notificationMessageGenriXAdapter.notifyDataSetChanged();
        }
    }

    public static class DiaglogReferral {
        private View contRef;
        private LinearLayout contRefCard;
        private TextView currency;
        private TextView refBalance;
        private TextView refActionCode;
        private TextView refTxt;
        private Button addBtn;
        private TextView haveAcode;

        public DiaglogReferral(View root, BaseFragment fragment) {
            findViews(root);
            WalletViewModel.getInstance().getWallet().observe(fragment.getViewLifecycleOwner(), wallet -> {
                currency.setText(ResourceUtils.getString(R.string.ref_bal) + " " + ResourceUtils.getString(R.string.currency));
                refBalance.setText("" + wallet.getAggReferralBalance());
                refActionCode.setText(wallet.getReferralCode());
                refActionCode.setOnClickListener(view -> {
                    utl.copyToClipBoard(ResourceUtils.getString(R.string.refer) + " " +
                                    ResourceUtils.getString(R.string.app_name),

                            (wallet.getReferralCode()),
                            view.getContext());
                });
                addBtn.setOnClickListener(view -> {
                    fragment.act.shareText(getReferralMessage(wallet.getReferralCode()), "");
                });
                if (utl.isEmpty(wallet.getReferredBy())) {
                    haveAcode.setVisibility(View.VISIBLE);
                    haveAcode.setOnClickListener((v) -> {

                        utl.diagInputTextImage(root.getContext(), ResourceUtils.getString(R.string.enter_ref_code), text -> {

                            RestAPI.getInstance(App.getAppContext())
                                    .redeemReferral(text, (data1, data2) -> {
                                        WalletViewModel.getInstance().refresh(null);
                                        utl.diagBottom(root.getContext(), data1);
                                        if (data1.toLowerCase().contains("success")) {
                                            WalletViewModel.getInstance().refresh(null);
                                        }
                                    });
                        });

                    });
                } else {
                    haveAcode.setVisibility(View.GONE);
                }

            });
        }

        private void findViews(View root) {
            contRef = root.findViewById(R.id.contRef);
            contRefCard = (LinearLayout) root.findViewById(R.id.contRefCard);
            currency = (TextView) root.findViewById(R.id.currency);
            refBalance = (TextView) root.findViewById(R.id.refBalance);
            refActionCode = (TextView) root.findViewById(R.id.refActionCode);
            refTxt = (TextView) root.findViewById(R.id.refTxt);
            addBtn = (Button) root.findViewById(R.id.addBtn);
            haveAcode = root.findViewById(R.id.haveAcode);

        }

        private String getReferralMessage(String refCode) {
            return String.format(ResourceUtils.getString(R.string.referral_message),
                    ResourceUtils.getString(R.string.app_name), refCode, BaseActivity.mFirebaseRemoteConfig.getString("download_link"));
        }


    }


}
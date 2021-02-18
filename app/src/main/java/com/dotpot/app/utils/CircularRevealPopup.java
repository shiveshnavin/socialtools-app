package com.dotpot.app.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.dotpot.app.R;


/**
 * Created by shivesh on 3/6/19.
 */

public class CircularRevealPopup   {

    private Context ctx;
    private View anchor,content;



    public CircularRevealPopup(Context ctx, View anchor,View content) {
        this.ctx = ctx;
        this.anchor = anchor;
        this.content = content;

    }
    public ImageView cancle;
    LinearLayout container;
    public void dismiss ( ){

        cancle.callOnClick();

    };
    public LinearLayout getContainer()
    {
        return container;
    }
    public Dialog popup ( boolean showDismissBtn){


        final View dialogView = View.inflate(ctx, R.layout.utl_diag_circular_reveal,null);
        container=dialogView.findViewById(R.id.container);

        final Dialog dialog = new Dialog(ctx,R.style.MyAlertDialogStyleCircular);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        cancle = (ImageView)dialog.findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                revealShow(dialogView, false, dialog);
            }
        });

//        if(!showDismissBtn){
//            dialog.findViewById(R.id.dismiss).setVisibility(View.GONE);
//        }else {
//            dialog.findViewById(R.id.dismiss).setVisibility(View.VISIBLE);
//        }

        dialog.findViewById(R.id.dismiss).setOnClickListener(v->{
            dismiss();
        });


        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){

                    revealShow(dialogView, false, dialog);
                    return true;
                }

                return false;
            }
        });



        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
        if(content!=null)
        {

            container.addView(content);
        }


        return dialog;
    };
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void revealShow(View dialogView, boolean b, final Dialog dialog) {

        final View view = dialogView.findViewById(R.id.dialog);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (anchor.getX() + (anchor.getWidth()/2));
        int cy = (int) (anchor.getY())+ anchor.getHeight() + 56;


        if(b){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx,cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(300);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(300);
            anim.start();
        }

    }









}

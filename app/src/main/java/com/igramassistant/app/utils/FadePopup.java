package com.igramassistant.app.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.igramassistant.app.R;
import com.igramassistant.app.utl;


/**
 * Created by shivesh on 3/6/19.
 */

public class FadePopup {

    private Context ctx;
    private View anchor;
    private View content;


    public FadePopup(Context ctx,   View content) {
        this.ctx = ctx;
        this.content = content;

    }

    public FadePopup(Context ctx, @Nullable View anchor, View content) {
        this.ctx = ctx;
        this.content = content;

    }
    public LinearLayout getContainer()
    {
        return container;
    }
    private LinearLayout container;
    public Button dismissBtn;
    public Dialog popup ( ){


        final View dialogView = View.inflate(ctx, R.layout.utl_diag_circular_reveal,null);
        container=dialogView.findViewById(R.id.container);
        dismissBtn=dialogView.findViewById(R.id.dismiss);

        final Dialog dialog = new Dialog(ctx,R.style.PopupDialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        ImageView imageView = (ImageView)dialog.findViewById(R.id.cancle);
        cancle=imageView;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();


            }
        });
        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onClickDismiss();
            }
        });



        try {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } catch (Exception e) {
            if(utl.DEBUG_ENABLED) e.printStackTrace();
        }

        try {
            dialog.show();
        } catch (Exception e) {
            if(utl.DEBUG_ENABLED) e.printStackTrace();
        }
        if(content!=null)
         container.addView(content);
        ;

        return dialog;
    };


    public void onClickDismiss()
    {

    }


    public ImageView cancle;
    public void dismiss ( ){

        cancle.callOnClick();

    };


}

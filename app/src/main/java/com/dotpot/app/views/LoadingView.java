package com.dotpot.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.dotpot.app.R;
import com.dotpot.app.utl;

public class LoadingView extends androidx.appcompat.widget.AppCompatImageView {
    boolean isInit = false;

    private void setDimens(){
        setMinimumHeight(utl.pxFromDp(getContext(),32f).intValue());
        setMinimumWidth(utl.pxFromDp(getContext(),32f).intValue());
    }

    public LoadingView(Context context) {
        super(context);
        setDimens();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setDimens();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setDimens();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isInit)
            postDelayed(() -> {
                isInit = true;
                setImageResource(R.drawable.avd_load);
                utl.animate_avd(LoadingView.this);
            }, 100);
    }
}

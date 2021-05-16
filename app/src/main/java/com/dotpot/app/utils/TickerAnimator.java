package com.dotpot.app.utils;

import android.os.CountDownTimer;
import android.view.View;

import com.dotpot.app.interfaces.GenricCallback;
import com.dotpot.app.interfaces.GenricDataCallback;
import com.dotpot.app.utl;

public class TickerAnimator {

    GenricCallback onFinish;
    GenricDataCallback onTick;
    View view;
    CountDownTimer ctr;

    public TickerAnimator(GenricDataCallback onTick,GenricCallback onFinish,  View view) {
        this.onFinish = onFinish;
        this.onTick = onTick;
        this.view = view;
    }

    public void start(int count) {

        if (count <= 0)
            count = utl.randomInt(2, 5) * 1000;

        ctr = new CountDownTimer(count, 1000) {
            @Override
            public void onTick(long l) {

                view.setScaleX(1.5f);
                view.setScaleY(1.5f);

                view.animate().setDuration(1000).scaleX(1);
                view.animate().setDuration(1000).scaleY(1);


                if (onTick != null)
                    onTick.onStart("" + l, (int) l);

            }

            @Override
            public void onFinish() {
                if (onFinish != null)
                    onFinish.onStart();
            }
        };
        ctr.start();

    }

    public void stop() {
        ctr.cancel();
    }

}

package com.igramassistant.app.utils;

import android.os.CountDownTimer;
import android.view.View;

import com.igramassistant.app.interfaces.GenricCallback;
import com.igramassistant.app.interfaces.GenricDataCallback;
import com.igramassistant.app.utl;

import lombok.Getter;
import lombok.Setter;

public class TickerAnimator {

    GenricCallback onFinish;
    GenricDataCallback onTick;
    View view;
    CountDownTimer ctr;
    @Getter
    int count = 10;
    @Getter
    @Setter
    private long INTERVAL = 1000;

    public TickerAnimator(GenricDataCallback onTick,GenricCallback onFinish,  View view) {
        this.onFinish = onFinish;
        this.onTick = onTick;
        this.view = view;
    }

    public void start(int count) {

        if (count <= 0)
            count = utl.randomInt(0, count) * 1000;
        this.count = count;

        ctr = new CountDownTimer(count, INTERVAL) {
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
                    onCompleted();
            }
        };
        ctr.start();

    }

    public void stop() {
        ctr.cancel();
    }

    public void reset(){
        stop();
        start(count);
    }

    public void onCompleted(){
        onFinish.onStart();
    }

}

package com.igramassistant.app.interfaces;

import android.animation.Animator;

public interface AbstractAnimatorListener extends Animator.AnimatorListener {
    @Override
    default void onAnimationStart(Animator animation) {

    }

    @Override
    default void onAnimationEnd(Animator animation) {

    }

    @Override
    default void onAnimationCancel(Animator animation) {

    }

    @Override
    default void onAnimationRepeat(Animator animation) {

    }
}

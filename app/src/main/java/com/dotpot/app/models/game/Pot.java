package com.dotpot.app.models.game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dotpot.app.R;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Game;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utl;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tyrantgit.explosionfield.ExplosionField;

@Getter
@Setter
@Builder
public class Pot {

    transient Game game;
    transient View rootView;
    int value;

    String ownedByUserId;
    boolean isOwned;
    transient TextView textView;
    transient ImageView animLogo;
    transient ExplosionField explosionField;
    transient GenricObjectCallback<Pot> onDestroy;

    public String getId() {
        return "ID" + value;
    }

    public View inflate(boolean allowNumberPreview, boolean startGame, LayoutInflater inflater, ViewGroup contView, ExplosionField explosionField, GenricObjectCallback<Pot> onDestroy) {


        rootView = inflater.inflate(R.layout.row_pot, contView, false);
        textView = rootView.findViewById(R.id.potText);
        animLogo = rootView.findViewById(R.id.animLogo);
        this.explosionField = explosionField;
        this.onDestroy = onDestroy;

        textView.setText("" + getValue());
        if (isOwned()) {
            animLogo.setVisibility(View.INVISIBLE);
//            textView.setVisibility(View.VISIBLE);
        }
        else {
            animLogo.setVisibility(View.VISIBLE);
//            textView.setVisibility(View.GONE);
        }

        rootView.setTransitionName(getId());

        if (startGame)
            rootView.setOnClickListener(v -> {
                if (!isOwned() && game.isPlayer1Turn()) {
                    if(game.getCurrentMagicPot().getValue() == value){
                        own(game.getPlayer1Id());
                    }
                    else {
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(rootView);
                    }
                }

            });

        if(game.getPlayer1Id().equals(ownedByUserId)){
            rootView.setBackground(ResourceUtils.getDrawable(R.drawable.bg_round_clip_outline_accent));
            game.setPlayer1wins(game.getPlayer1wins()+(int)value);
        }
        else if(game.getPlayer2Id().equals(ownedByUserId)){
            rootView.setBackground(ResourceUtils.getDrawable(R.drawable.bg_round_clip_outline_orange));
            game.setPlayer2wins(game.getPlayer2wins()+(int)value);
        }

        return rootView;
    }

    public void own(String ownedByUserId) {
        this.ownedByUserId = ownedByUserId;
        isOwned = true;
        if(game.getPlayer1Id().equals(ownedByUserId)){
            rootView.setBackground(ResourceUtils.getDrawable(R.drawable.bg_round_clip_outline_accent));
            game.setPlayer1wins(game.getPlayer1wins()+(int)value);
        }
        else if(game.getPlayer2Id().equals(ownedByUserId)){
            rootView.setBackground(ResourceUtils.getDrawable(R.drawable.bg_round_clip_outline_orange));
            game.setPlayer2wins(game.getPlayer2wins()+(int)value);
        }
        animLogo.setVisibility(View.VISIBLE);
        isOwned = true;
        onDestroy.onEntity(Pot.this);

        Animation animation = utl.animate_shake(animLogo);
        if (animation != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    explosionField.explode(animLogo);
                    textView.setVisibility(View.VISIBLE);
                    textView.setTextColor(ResourceUtils.getColor(R.color.colorTextPrimary));
                    utl.animate_shake(textView);

                    animLogo.postDelayed(()->{
                        animLogo.setVisibility(View.INVISIBLE);
                    },500);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }


    }

}

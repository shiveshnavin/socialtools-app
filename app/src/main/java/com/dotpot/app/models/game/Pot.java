package com.dotpot.app.models.game;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

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
    float value;

    String ownedByUserId;
    boolean isOwned;
    transient TextView textView;
    transient ImageView animLogo;
    transient ExplosionField explosionField;
    transient GenricObjectCallback<Pot> onDestroy;

    public String getId() {
        return "ID" + value;
    }

    public View inflate(boolean isPreview, boolean startGame, LayoutInflater inflater, ViewGroup contView, ExplosionField explosionField, GenricObjectCallback<Pot> onDestroy) {


        rootView = inflater.inflate(R.layout.row_pot, contView, false);
        textView = rootView.findViewById(R.id.potText);
        animLogo = rootView.findViewById(R.id.animLogo);
        this.explosionField = explosionField;
        this.onDestroy = onDestroy;

        textView.setText("" + getValue());
        if (isOwned() || isPreview) {
            animLogo.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            animLogo.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }

        rootView.setTransitionName(getId());

        if (!isPreview && startGame)
            rootView.setOnClickListener(v -> {
                if (!isOwned() && game.isPlayer1Turn()) {
                    own(game.getPlayer1Id());
                }

            });

        return rootView;
    }

    public void own(String ownedByUserId) {
        this.ownedByUserId = ownedByUserId;
        isOwned = true;
        if(game.getPlayer1Id().equals(ownedByUserId)){
            rootView.setBackground(ResourceUtils.getDrawable(R.drawable.bg_round_clip_outline_accent));
            game.setPlayer1wins(game.getPlayer1wins()+(int)value);
        }
        else {
            rootView.setBackground(ResourceUtils.getDrawable(R.drawable.bg_round_clip_outline_orange));
            game.setPlayer2wins(game.getPlayer2wins()+(int)value);
        }

        Animation animation = utl.animate_shake(animLogo);
        if (animation != null) {
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                    explosionField.explode(animLogo);
                    isOwned = true;
                    animLogo.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    onDestroy.onEntity(Pot.this);
                    utl.animate_shake(textView);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        }


    }

}

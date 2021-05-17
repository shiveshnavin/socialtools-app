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

    public String getId() {
        return "ID" + value;
    }

    public View inflate(boolean isPreview, LayoutInflater inflater, ViewGroup contView, ExplosionField explosionField, GenricObjectCallback<Pot> onDestroy) {


        View rootView = inflater.inflate(R.layout.row_pot, contView, false);
        TextView textView = rootView.findViewById(R.id.potText);
        ImageView animLogo = rootView.findViewById(R.id.animLogo);

        textView.setText("" + getValue());
        if (isOwned() || isPreview) {
            animLogo.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.VISIBLE);
        } else {
            animLogo.setVisibility(View.VISIBLE);
            textView.setVisibility(View.GONE);
        }

        rootView.setTransitionName(getId());

        if (!isPreview)
            rootView.setOnClickListener(v -> {
                if (!isOwned()) {
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
                                ownedByUserId = game.getPlayer1Id();
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

            });

        return rootView;
    }

}

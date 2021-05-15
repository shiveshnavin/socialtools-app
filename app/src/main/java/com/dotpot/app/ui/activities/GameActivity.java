package com.dotpot.app.ui.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dotpot.app.R;
import com.dotpot.app.binding.WalletViewModel;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Game;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utils.ObjectTransporter;
import com.dotpot.app.utl;
import com.dotpot.app.views.RoundRectCornerImageView;
import com.squareup.picasso.Picasso;

public class GameActivity extends BaseActivity {

    Game game;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        LinearLayout contView = findViewById(R.id.contView);

        String gameId = getIntent().getStringExtra("gameId");
        game = (Game) ObjectTransporter.getInstance().remove(gameId);
        if (game == null) {
            utl.toast(ctx, getstring(R.string.error_msg_try_again));
            finish();
            return;
        }

        setupPreGame(contView);

    }

    /* ============== PRE-GAME : SELECT PLAYER ========= */


    private void setupPreGame(LinearLayout contView) {
        if(contView.getChildCount()>0)
            contView.removeAllViews();

        View rootView = getLayoutInflater().inflate(R.layout.fragment_pregame, contView);

        ConstraintLayout contPlayers = (ConstraintLayout) rootView.findViewById(R.id.contPlayers);
        RoundRectCornerImageView player1Image = (RoundRectCornerImageView) rootView.findViewById(R.id.player1Image);
        TextView player1Name = (TextView) rootView.findViewById(R.id.player1Name);
        TextView vsText = (TextView) rootView.findViewById(R.id.vsText);
        RoundRectCornerImageView player2Image = (RoundRectCornerImageView) rootView.findViewById(R.id.player2Image);
        TextView player2Name = (TextView) rootView.findViewById(R.id.player2Name);
        TextView timer = (TextView) rootView.findViewById(R.id.timerText);
        Button startGame = (Button) rootView.findViewById(R.id.startGame);

        Picasso.get().load(user.getImage()).error(R.drawable.account)
                .placeholder(R.drawable.account)
                .into(player1Image);



        CountDownTimer ctr;
        final int count = utl.randomInt(5, 30) * 1000;

        ctr = new CountDownTimer(count, 1000) {
            @Override
            public void onTick(long l) {

                Double f = (count - l) / 1000d;


                timer.setText(f.intValue());

                timer.setScaleX(1.5f);
                timer.setScaleY(1.5f);

                timer.animate().setDuration(1000).scaleX(1);
                timer.animate().setDuration(1000).scaleY(1);


            }

            @Override
            public void onFinish() {
                timer.setVisibility(View.GONE);

                startGame.setScaleX(1.5f);
                startGame.setScaleY(1.5f);

                startGame.animate().setDuration(1000).scaleX(1);
                startGame.animate().setDuration(1000).scaleY(1);

                startGame.setVisibility(View.VISIBLE);
                startGame.setOnClickListener(v->{
                    setupInGame(contView);
                });


                if(game.getPlayer2()==null){
                    utl.diagBottom(ctx,"",getString(R.string.no_opponent),
                            false,getString(R.string.finish),()-> finish());
                    return;
                }
                Picasso.get().load(game.getPlayer2().getImage()).error(R.drawable.account)
                        .placeholder(R.drawable.account)
                        .into(player2Image);
                player2Name.setText(game.getPlayer2().getName());
            }
        };
        ctr.start();

    }



    /* -------------- PRE-GAME : SELECT PLAYER --------- */


    /* ============== IN-GAME ========= */


    private void setupInGame(LinearLayout contView) {
        if(contView.getChildCount()>0)
            contView.removeAllViews();

    }


    /* -------------- IN-GAME --------- */


    /* ============== FINISH-GAME ========= */

    private void setupConcludeGame(LinearLayout contView) {

        View root = getLayoutInflater().inflate(R.layout.fragment_pregame, contView);

        TextView head = root.findViewById(R.id.head);

        if (game.isPlayer1Won()) {
            head.setText("You Won !! INR " + game.getPossibleAward());
        } else {
            head.setText("You Lost !!");
        }

        RestAPI.getInstance().finishGame(game, new GenricObjectCallback<Game>() {
            @Override
            public void onEntity(Game data) {
                utl.snack(act, "Game Finished !!!");
                RestAPI.getInstance().invalidateCacheWalletAndTxns();
                WalletViewModel.getInstance().refresh("");
            }

            @Override
            public void onError(String message) {
                utl.snack(act, "Error !!! " + message);
            }
        });

    }



    /* -------------- FINISH-GAME --------- */


}

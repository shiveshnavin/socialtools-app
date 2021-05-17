package com.dotpot.app.ui.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.dotpot.app.R;
import com.dotpot.app.binding.WalletViewModel;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Game;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.models.game.Pot;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utils.ObjectTransporter;
import com.dotpot.app.utils.TickerAnimator;
import com.dotpot.app.utl;
import com.dotpot.app.views.RoundRectCornerImageView;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

import tyrantgit.explosionfield.ExplosionField;

public class GameActivity extends BaseActivity {

    Game game;
    ExplosionField explosionField;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        LinearLayout contView = findViewById(R.id.contView);
        explosionField = ExplosionField.attach2Window(this);

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
        if (contView.getChildCount() > 0)
            contView.removeAllViews();

        View rootView = getLayoutInflater().inflate(R.layout.fragment_pregame, contView);

        ConstraintLayout contPlayers = rootView.findViewById(R.id.contPlayers);
        RoundRectCornerImageView player1Image = rootView.findViewById(R.id.player1Image);
        TextView player1Name = rootView.findViewById(R.id.player1Name);
        TextView vsText = rootView.findViewById(R.id.vsText);
        RoundRectCornerImageView player2Image = rootView.findViewById(R.id.player2Image);
        TextView player2Name = rootView.findViewById(R.id.player2Name);
        TextView timer = rootView.findViewById(R.id.timerText);
        Button startGame = rootView.findViewById(R.id.startGame);
        ImageView animLogo = rootView.findViewById(R.id.animLogo);
        LinearLayout searchingCont = rootView.findViewById(R.id.searchingCont);

        Picasso.get().load(user.getImage()).error(R.drawable.account)
                .placeholder(R.drawable.account)
                .into(player1Image);

        final int MAX_COUNT = 30;
        final int count = utl.randomInt(2, MAX_COUNT / 3) * 1000;

        utl.animate_avd(animLogo);

        TickerAnimator tickerAnimator = new TickerAnimator((message, time) -> {

            timer.setText(String.format("%d", MAX_COUNT - (count - time) / 1000));

            timer.setScaleX(1.5f);
            timer.setScaleY(1.5f);

            timer.animate().setDuration(1000).scaleX(1);
            timer.animate().setDuration(1000).scaleY(1);

        }, () -> {
            //todo remove hardcoding once API sends player2 info
            GenricUser genricUser = new GenricUser();
            genricUser.setId(game.getPlayer2Id());
            genricUser.setName("nikita.karn");
            genricUser.setImage("https://i.pinimg.com/736x/f3/2b/4d/f32b4da70a38995d1d147704359414ea.jpg");
            game.setPlayer2(genricUser);

            if (game.getPlayer2() == null) {
                utl.diagBottom(ctx, "", getString(R.string.no_opponent),
                        false, getString(R.string.finish), () -> finish());
                return;
            }
            Picasso.get().load(game.getPlayer2().getImage()).error(R.drawable.account)
                    .placeholder(R.drawable.account)
                    .into(player2Image);
            player2Name.setText(game.getPlayer2().getName());

            searchingCont.setVisibility(View.GONE);
            contPlayers.setVisibility(View.VISIBLE);

            utl.addPressReleaseAnimation(player1Image);
            utl.addPressReleaseAnimation(player2Image);

            startGame.setScaleX(1.5f);
            startGame.setScaleY(1.5f);

            startGame.animate().setDuration(400).scaleX(1);
            startGame.animate().setDuration(400).scaleY(1);

            startGame.setVisibility(View.VISIBLE);
            startGame.setOnClickListener(v -> {
                setupInGame(contView);
            });
        }, timer);


        tickerAnimator.start(count);

    }



    /* -------------- PRE-GAME : SELECT PLAYER --------- */


    /* ============== IN-GAME ========= */

    private void setupInGame(LinearLayout contView) {
        if (contView.getChildCount() > 0)
            contView.removeAllViews();

        View rootView = getLayoutInflater().inflate(R.layout.fragment_game, contView);

        TextView timerText = rootView.findViewById(R.id.timerText);
        TextView info = rootView.findViewById(R.id.info);
        ConstraintLayout contPlayers = rootView.findViewById(R.id.contPlayers);
        RoundRectCornerImageView player1Image = rootView.findViewById(R.id.player1Image);
        TextView player1Name = rootView.findViewById(R.id.player1Name);
        TextView vsText = rootView.findViewById(R.id.vsText);
        RoundRectCornerImageView player2Image = rootView.findViewById(R.id.player2Image);
        TextView player2Name = rootView.findViewById(R.id.player2Name);
        GridLayout contPots = rootView.findViewById(R.id.contPots);
        Button startGame = rootView.findViewById(R.id.startGame);


        Picasso.get().load(user.getImage()).error(R.drawable.account)
                .placeholder(R.drawable.account)
                .into(player1Image);

        Picasso.get().load(game.getPlayer2().getImage()).error(R.drawable.account)
                .placeholder(R.drawable.account)
                .into(player2Image);
        player2Name.setText(game.getPlayer2().getName());

        contPlayers.setVisibility(View.VISIBLE);

        utl.addPressReleaseAnimation(player1Image);
        utl.addPressReleaseAnimation(player2Image);



        List<Pot> pots = game.generatePots();
        createPots(true,contView, getLayoutInflater(), contPots, pots);

        info.setText(R.string.watch);
        utl.animate_land(info);
        CountDownTimer countDownTimer = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.format("%d", millisUntilFinished / 1000));
                utl.animate_land(timerText);

                if(millisUntilFinished < 6000){

                    TransitionManager.beginDelayedTransition(contPots, new ChangeBounds());
                    Collections.shuffle(pots);
                    createPots(false,contView, getLayoutInflater(), contPots, pots);

                }
            }

            @Override
            public void onFinish() {
                info.setText(R.string.break_the_pot_with_highest_values);
                utl.animate_land(info);

            }
        };
        countDownTimer.start();

    }

    private void createPots(boolean isPreview,LinearLayout contView, LayoutInflater inflater, ViewGroup layout, List<Pot> titles) {
        layout.removeAllViews();


        for (Pot title : titles) {

            layout.addView(title.inflate(isPreview,inflater, layout,explosionField, new GenricObjectCallback<Pot>() {
                @Override
                public void onEntity(Pot data) {

                    if (game.isOver()) {
                        setupConcludeGame(contView);
                    } else {
                        // todo update score
                    }
                }
            }));
        }

    }

    /* -------------- IN-GAME --------- */


    /* ============== FINISH-GAME ========= */

    private void setupConcludeGame(LinearLayout contView) {
        if (contView.getChildCount() > 0)
            contView.removeAllViews();


        View root = getLayoutInflater().inflate(R.layout.fragment_pregame, contView);

        TextView head = root.findViewById(R.id.timerText);

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

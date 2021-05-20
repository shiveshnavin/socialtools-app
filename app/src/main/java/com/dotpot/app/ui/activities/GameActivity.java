package com.dotpot.app.ui.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.AutoTransition;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.dotpot.app.R;
import com.dotpot.app.binding.WalletViewModel;
import com.dotpot.app.interfaces.GenricCallback;
import com.dotpot.app.interfaces.GenricDataCallback;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Game;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.models.game.Player2Listener;
import com.dotpot.app.models.game.Pot;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utils.ObjectTransporter;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utils.TickerAnimator;
import com.dotpot.app.utl;
import com.dotpot.app.views.RoundRectCornerImageView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import tyrantgit.explosionfield.ExplosionField;

public class GameActivity extends BaseActivity {

    Game game;
    private final GenricObjectCallback<Pot> onTapPotRecieved = new GenricObjectCallback<Pot>() {
        @Override
        public void onEntity(Pot data) {

            if (game.getState() == 1) {
                data.own(game.getPlayer2Id());
            }
        }
    };
    ExplosionField explosionField;
    Player2Listener player2Listener;
    private GenricObjectCallback<String> onEmoRecieved;
    ConstraintLayout container;

    /* ============== PRE-GAME : SELECT PLAYER ========= */
    private GenricObjectCallback<String> onReplayRequested;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        LinearLayout contView = findViewById(R.id.contView);
        container = findViewById(R.id.container);
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

    @Override
    public void onBackPressed() {

        utl.diagBottom(ctx, "", getString(R.string.are_you_sure_back), true, getString(R.string.confirm), this::finish);

    }

    private void setupPreGame(LinearLayout contView) {
        if (contView.getChildCount() > 0)
            contView.removeAllViews();
        game.setState(0);
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
        TextView searchingText = rootView.findViewById(R.id.searchingText);

        Picasso.get().load(user.getImage()).error(R.drawable.account)
                .placeholder(R.drawable.account)
                .into(player1Image);

        GenricCallback moveAnimatePlayers = () -> {


//               app:layout_constraintTop_toTopOf="parent"
//                          app:layout_constraintLeft_toLeftOf="parent"
//                          app:layout_constraintRight_toLeftOf="@id/vsText"
//
            long delay = 600;
            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(delay);
            TransitionManager.beginDelayedTransition(contPlayers, autoTransition);
            player2Image.setVisibility(View.VISIBLE);

            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(contPlayers);

            constraintSet.clear(R.id.player1Image, ConstraintSet.RIGHT);
            constraintSet.clear(R.id.player2Image, ConstraintSet.LEFT);
            constraintSet.applyTo(contPlayers);

            player2Image.postDelayed(() -> {
                vsText.setVisibility(View.VISIBLE);
                player1Name.setVisibility(View.VISIBLE);
                player2Name.setVisibility(View.VISIBLE);
            }, delay / 2);

        };

        final int MAX_COUNT = (int) mFirebaseRemoteConfig.getLong("max_game_waiting");
        final int count = utl.randomInt(MAX_COUNT - MAX_COUNT / 2, MAX_COUNT);

        utl.animate_avd(animLogo);

        TickerAnimator tickerAnimator = new TickerAnimator((message, time) -> {

            timer.setText(String.format("%d", (time) / 1000));

            timer.setScaleX(1.5f);
            timer.setScaleY(1.5f);

            timer.animate().setDuration(1000).scaleX(1);
            timer.animate().setDuration(1000).scaleY(1);

            if(game.getPlayer2()==null){

                //todo remove hardcoding once API sends player2 info
                GenricUser genricUser = new GenricUser();
                genricUser.setId(game.getPlayer2Id());
                genricUser.setName("nikita.karn");
                genricUser.setImage("https://i.pinimg.com/736x/f3/2b/4d/f32b4da70a38995d1d147704359414ea.jpg");
                game.setPlayer2(genricUser);

            }
            if (!player2Name.getText().toString().equals(game.getPlayer2().getName())) {

                player2Image.setVisibility(View.VISIBLE);
                Picasso.get().load(game.getPlayer2().getImage()).error(R.drawable.account)
                        .placeholder(R.drawable.account)
                        .into(player2Image);
                player2Name.setText(game.getPlayer2().getName());

                player2Listener = Player2Listener.builder()
                        .player2(game.getPlayer2())
                        .onTapPot(onTapPotRecieved)
                        .onSendEmo(onEmoRecieved)
                        .onReplayRequest(onReplayRequested).build();

            }
            if (time / 1000 < count && player2Name.getVisibility() != View.VISIBLE) {

                if (game.getPlayer2() == null) {
                    utl.diagBottom(ctx, "", getString(R.string.no_opponent),
                            false, getString(R.string.finish), () -> finish());
                    return;
                }

                searchingText.setText(R.string.starting);
//            searchingCont.setVisibility(View.GONE);
                contPlayers.setVisibility(View.VISIBLE);

                utl.addPressReleaseAnimation(player1Image, true);
                utl.addPressReleaseAnimation(player2Image, true);

                contPlayers.postDelayed(moveAnimatePlayers::onStart, 100);
            }

        }, () -> {
            TransitionManager.beginDelayedTransition(container);
             setupInGame(contView);
        }, timer);


        tickerAnimator.start(MAX_COUNT * 1000);

    }



    /* -------------- PRE-GAME : SELECT PLAYER --------- */


    /* ============== IN-GAME ========= */

    void waitForPlayer2() {
        List<Pot> unTappedPots = game.getPots().stream().filter(pot -> !pot.isOwned()).collect(Collectors.toList());

        Random rand = new Random();
        if (unTappedPots.isEmpty()) {
            return;
        }
        Pot randomElement = unTappedPots.get(rand.nextInt(unTappedPots.size()));

        new Handler().postDelayed(() -> {
            player2Listener.sendTapOnPot(randomElement);
        }, utl.randomInt(1500, ((int) mFirebaseRemoteConfig.getLong("max_user_waiting") - 1) * 1000));

    }

    private void setupInGame(LinearLayout contView) {
        if (contView.getChildCount() > 0)
            contView.removeAllViews();
        game.setState(1);

        View rootView = getLayoutInflater().inflate(R.layout.fragment_game, contView);

        Button startGame = (Button) rootView.findViewById(R.id.startGame);
        ConstraintLayout contPlayers = (ConstraintLayout) rootView.findViewById(R.id.contPlayers);
        TextView player1Name = (TextView) rootView.findViewById(R.id.player1Name);
        TextView vsText = (TextView) rootView.findViewById(R.id.vsText);
        TextView timerText = (TextView) rootView.findViewById(R.id.timerText);
        TextView player2Name = (TextView) rootView.findViewById(R.id.player2Name);
        TextView player2score = (TextView) rootView.findViewById(R.id.player2score);
        RoundRectCornerImageView player2Image = (RoundRectCornerImageView) rootView.findViewById(R.id.player2Image);
        TextView player1score = (TextView) rootView.findViewById(R.id.player1score);
        RoundRectCornerImageView player1Image = (RoundRectCornerImageView) rootView.findViewById(R.id.player1Image);
        TextView info = (TextView) rootView.findViewById(R.id.info);
        GridLayout contPots = (GridLayout) rootView.findViewById(R.id.contPots);
        ExtendedFloatingActionButton chatBtn = (ExtendedFloatingActionButton) rootView.findViewById(R.id.chatBtn);


        game.setOnGameScoreUpdate(new GenricCallback() {
            @Override
            public void onStart() {

                player1score.setText("" + game.getPlayer1wins());
                player2score.setText("" + game.getPlayer2wins());

            }
        });

        GenricDataCallback onNewTurn = (playerId, statusCode) -> {
            game.setTurnOfPlayerId(playerId);
            if (playerId.equals(game.getPlayer1Id())) {
                info.setText(R.string.break_the_pot_with_highest_values);
                utl.animate_land(info);
            } else {
                info.setText(game.getPlayer2().getName() + "'s " + ResourceUtils.getString(R.string.turn));
                utl.animate_land(info);
                waitForPlayer2();
            }
        };


        TickerAnimator tickerAnimator = new TickerAnimator(new GenricDataCallback() {
            @Override
            public void onStart(String message, int statusCode) {
                timerText.setText("" + statusCode / 1000);
            }
        }, () -> {
            // Turn finished
        }, timerText) {
            @Override
            public void onCompleted() {
                onNewTurn.onStart(game.getPlayer2Id(), 1);
                reset();
            }
        };


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
        createPots(true, false, contView, getLayoutInflater(), contPots, pots, onNewTurn, tickerAnimator);

        info.setText(R.string.watch);
        utl.animate_land(info);
        CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.format("%d", millisUntilFinished / 1000));
                utl.animate_land(timerText);

                if (millisUntilFinished < 6000) {

                    TransitionManager.beginDelayedTransition(contPots, new ChangeBounds());
                    Collections.shuffle(pots);
                    createPots(false, false, contView, getLayoutInflater(), contPots, pots, onNewTurn, tickerAnimator);

                }
            }

            @Override
            public void onFinish() {
                createPots(false, true, contView, getLayoutInflater(), contPots, pots, onNewTurn, tickerAnimator);
                tickerAnimator.start((int) mFirebaseRemoteConfig.getLong("max_user_waiting") * 1000);
                onNewTurn.onStart(game.getPlayer1Id(), 1);
            }
        };
        countDownTimer.start();

    }

    private void createPots(boolean isPreview, boolean startGame, LinearLayout contView, LayoutInflater inflater, ViewGroup layout, List<Pot> titles, GenricDataCallback onNewTurn, TickerAnimator tickerAnimator) {
        layout.removeAllViews();


        for (Pot title : titles) {

            layout.addView(title.inflate(isPreview, startGame, inflater, layout, explosionField, new GenricObjectCallback<Pot>() {
                @Override
                public void onEntity(Pot data) {

                    game.getOnGameScoreUpdate().onStart();
                    if (game.isOver()) {
                        setupConcludeGame(contView);
                        tickerAnimator.stop();
                    } else {
                        tickerAnimator.reset();
                        if (data.getOwnedByUserId().equals(game.getPlayer2Id()))
                            onNewTurn.onStart(game.getPlayer1Id(), 1);
                        else
                            onNewTurn.onStart(game.getPlayer2Id(), 2);

                    }
                }
            }));
        }

    }

    /* -------------- IN-GAME --------- */


    /* ============== FINISH-GAME ========= */

    private void setupConcludeGame(LinearLayout contView) {
//        if (contView.getChildCount() > 0)
//            contView.removeAllViews();

        game.setState(2);

//        View root = getLayoutInflater().inflate(R.layout.fragment_pregame, contView);
//
//        TextView head = root.findViewById(R.id.timerText);
//
//        if (game.isPlayer1Won()) {
//            head.setText("You Won !! INR " + game.getPossibleAward());
//        } else {
//            head.setText("You Lost !!");
//        }

        RestAPI.getInstance().finishGame(game, new GenricObjectCallback<Game>() {
            @Override
            public void onEntity(Game data) {
                utl.snack(act, "Game Finished !!! " + data.isPlayer1Won());
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

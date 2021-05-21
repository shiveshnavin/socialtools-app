package com.dotpot.app.ui.activities;

import android.animation.Animator;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dotpot.app.R;
import com.dotpot.app.adapters.GenriXAdapter;
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

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
    ConstraintLayout container;

    /* ============== PRE-GAME : SELECT PLAYER ========= */

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
    long delay = 600;

    private void setupPreGame(LinearLayout contView) {
        if (contView.getChildCount() > 0)
            contView.removeAllViews();
        game.setState(0);
        View rootView = getLayoutInflater().inflate(R.layout.fragment_pregame, contView);
        LinearLayout searchingCont = (LinearLayout)rootView.findViewById( R.id.searchingCont );
        ImageView animLogo = (ImageView)rootView.findViewById( R.id.animLogo );
        TextView timerText = (TextView)rootView.findViewById( R.id.timerText );
        TextView searchingText = (TextView)rootView.findViewById( R.id.searchingText );
        ConstraintLayout contPlayers = (ConstraintLayout)rootView.findViewById( R.id.contPlayers );
        TextView vsText = (TextView)rootView.findViewById( R.id.vsText );
        RoundRectCornerImageView player2Image = (RoundRectCornerImageView)rootView.findViewById( R.id.player2Image );
        TextView player2Name = (TextView)rootView.findViewById( R.id.player2Name );
        RoundRectCornerImageView player1Image = (RoundRectCornerImageView)rootView.findViewById( R.id.player1Image );
        TextView player1Name = (TextView)rootView.findViewById( R.id.player1Name );
        Button startGame = (Button)rootView.findViewById( R.id.startGame );

        Picasso.get().load(user.getImage()).error(R.drawable.account)
                .placeholder(R.drawable.account)
                .into(player1Image);

        GenricCallback moveAnimatePlayers = () -> {

//               app:layout_constraintTop_toTopOf="parent"
//                          app:layout_constraintLeft_toLeftOf="parent"
//                          app:layout_constraintRight_toLeftOf="@id/vsText"
//
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

            timerText.setText(String.format("%d", (time) / 1000));

            timerText.setScaleX(1.5f);
            timerText.setScaleY(1.5f);

            timerText.animate().setDuration(1000).scaleX(1);
            timerText.animate().setDuration(1000).scaleY(1);

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
                        .onTapPotFromPlayer2(onTapPotRecieved) .build();

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
        }, timerText);


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

        Button startGame = (Button)rootView.findViewById( R.id.startGame );
        ConstraintLayout contPlayers = (ConstraintLayout)rootView.findViewById( R.id.contPlayers );
        TextView player1Name = (TextView)rootView.findViewById( R.id.player1Name );
        TextView vsText = (TextView)rootView.findViewById( R.id.vsText );
        TextView timerText = (TextView)rootView.findViewById( R.id.timerText );
        TextView player2Name = (TextView)rootView.findViewById( R.id.player2Name );
        TextView player2score = (TextView)rootView.findViewById( R.id.player2score );
        RoundRectCornerImageView player2Image = (RoundRectCornerImageView)rootView.findViewById( R.id.player2Image );
        TextView player2Emo = (TextView)rootView.findViewById( R.id.player2Emo );
        TextView player1score = (TextView)rootView.findViewById( R.id.player1score );
        RoundRectCornerImageView player1Image = (RoundRectCornerImageView)rootView.findViewById( R.id.player1Image );
        TextView player1Emo = (TextView)rootView.findViewById( R.id.player1Emo );
        TextView info = (TextView)rootView.findViewById( R.id.info );
        GridLayout contPots = (GridLayout)rootView.findViewById( R.id.contPots );
        ConstraintLayout contEmos = (ConstraintLayout)rootView.findViewById( R.id.contEmos );
        ExtendedFloatingActionButton chatBtn = (ExtendedFloatingActionButton)rootView.findViewById( R.id.chatBtn );
        RecyclerView listEmos = (RecyclerView)rootView.findViewById( R.id.listEmos );

        GenricObjectCallback<String> onEmoRecieved = new GenricObjectCallback<String>() {
            @Override
            public void onEntity(String data) {
                if(game.getState() == 1){
                    player2Emo.setVisibility(View.VISIBLE);
                    player2Emo.setText(data);
                    YoYo.with(Techniques.SlideOutUp)
                            .duration(delay * 3)
                            .playOn(player2Emo);
//                    player2Emo.setTranslationY(0);
//                    player2Emo.setAlpha(1.0f);
//                    player2Emo.animate().alpha(0.0f)
//                            .translationYBy(-200f)
//                            .setDuration(delay*3).start();
                }
            }
        };

        player2Listener.setOnEmoFromPlayer2(onEmoRecieved);

        game.setOnGameScoreUpdate(() -> {

            player1score.setText("" + game.getPlayer1wins());
            player2score.setText("" + game.getPlayer2wins());

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

        GenricCallback addEmoButtons = ()->{

            List<String> emos = Arrays.asList("\uD83D\uDE0D","\uD83D\uDC4A","\uD83D\uDE4C","\uD83E\uDD0F","\uD83D\uDE02","\uD83D\uDE21");
            GenriXAdapter<String> genriXAdapter = new GenriXAdapter<String>(ctx,R.layout.row_emo,emos){
                @Override
                public void onBindViewHolder(@NonNull @NotNull CustomViewHolder viewHolder, int i) {
                    String e = emos.get(i);
                    viewHolder.textView(R.id.potText).setText(e);
                    viewHolder.view(R.id.root).setOnClickListener(v->{

                        player1Emo.setVisibility(View.VISIBLE);
                        player1Emo.setText(e);
                        YoYo.with(Techniques.SlideOutUp)
                                .duration(delay * 3)
                                .playOn(player1Emo);

                        player2Listener.sendEmoToPlayer2(e);
                        listEmos.animate().alpha(0.0f).setDuration(delay/2).start();
                        chatBtn.animate().alpha(1.0f)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {
                                        chatBtn.setVisibility(View.VISIBLE);

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                })
                                .setDuration(delay/2).start();
                    });
                }
            };
            listEmos.setLayoutManager(new LinearLayoutManager(ctx,RecyclerView.HORIZONTAL,false));
            listEmos.setAdapter(genriXAdapter);

        };
        chatBtn.setOnClickListener(v->{
            chatBtn.animate().alpha(0.0f)
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            chatBtn.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .setDuration(delay/2).start();
            listEmos.animate().alpha(1.0f).setDuration(delay/2).start();
        });
        addEmoButtons.onStart();

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
        GenricObjectCallback<String> onReplayRequested= new GenricObjectCallback<String>() {
            @Override
            public void onEntity(String data) {

            }
        };


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

package com.dotpot.app.services;

import android.animation.Animator;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;

import com.dotpot.app.R;
import com.dotpot.app.adapters.GenriXAdapter;
import com.dotpot.app.interfaces.AbstractAnimatorListener;
import com.dotpot.app.interfaces.GenricCallback;
import com.dotpot.app.interfaces.GenricDataCallback;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Game;
import com.dotpot.app.models.game.Player2Listener;
import com.dotpot.app.models.game.Pot;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utils.TickerAnimator;
import com.dotpot.app.utl;
import com.dotpot.app.views.RoundRectCornerImageView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tyrantgit.explosionfield.ExplosionField;

public class GameService {

    BaseActivity ctx;
    Game game;
    long delay;
    Player2Listener player2Listener;
    int MAX_USER_WAIT;

    ExplosionField explosionField;
    SoundPool soundPool;
    int soundId;
    GenricDataCallback playSOund;
    GenricObjectCallback<LinearLayout> onGameConclude;

    public GameService(BaseActivity ctx,
                       Game game,
                       long delay,
                       Player2Listener player2Listener,
                       int MAX_USER_WAIT, GenricObjectCallback<LinearLayout> onGameConclude) {

        this.ctx = ctx;
        this.game = game;
        this.delay = delay;
        this.player2Listener = player2Listener;
        this.MAX_USER_WAIT = MAX_USER_WAIT;
        this.onGameConclude = onGameConclude;
        explosionField = ExplosionField.attach2Window(ctx);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
        soundId = soundPool.load(ctx, R.raw.glass_break, 1);
        playSOund = (player, pid) -> {
            soundPool.play(soundId, pid == 1 ? 1.0f : 0.5f, pid == 1 ? 0.5f : 1f, 0, 0, 1);
        };
        GenricObjectCallback<Pot> onTapPotRecieved = new GenricObjectCallback<Pot>() {
            @Override
            public void onEntity(Pot data) {

                if (game.getState() == 1) {
                    playSOund.onStart(game.getPlayer2Id(), 2);
                    data.own(game.getPlayer2Id());
                }
            }
        };
        player2Listener .setOnTapPotFromPlayer2(onTapPotRecieved);
    }

    public void setupInGame(LinearLayout contView) {
        if (contView.getChildCount() > 0)
            contView.removeAllViews();
        game.setState(1);

        View rootView = ctx.getLayoutInflater().inflate(R.layout.fragment_game, contView);

        Button startGame = (Button) rootView.findViewById(R.id.startGame);
        ConstraintLayout contPlayers = (ConstraintLayout) rootView.findViewById(R.id.contPlayers);
        TextView player1Name = (TextView) rootView.findViewById(R.id.player1Name);
        TextView vsText = (TextView) rootView.findViewById(R.id.vsText);
        TextView timerText = (TextView) rootView.findViewById(R.id.timerText);
        TextView player2Name = (TextView) rootView.findViewById(R.id.player2Name);
        TextView player2score = (TextView) rootView.findViewById(R.id.player2score);
        RoundRectCornerImageView player2Image = (RoundRectCornerImageView) rootView.findViewById(R.id.player2Image);
        TextView player2Emo = (TextView) rootView.findViewById(R.id.player2Emo);
        TextView player1score = (TextView) rootView.findViewById(R.id.player1score);
        RoundRectCornerImageView player1Image = (RoundRectCornerImageView) rootView.findViewById(R.id.player1Image);
        TextView player1Emo = (TextView) rootView.findViewById(R.id.player1Emo);
        TextView info = (TextView) rootView.findViewById(R.id.info);
        GridLayout contPots = (GridLayout) rootView.findViewById(R.id.contPots);
        ConstraintLayout contEmos = (ConstraintLayout) rootView.findViewById(R.id.contEmos);
        ExtendedFloatingActionButton pokeBtn = (ExtendedFloatingActionButton) rootView.findViewById(R.id.pokeBtn);
        RecyclerView listEmos = (RecyclerView) rootView.findViewById(R.id.listEmos);
        ProgressBar circularProgressbar = rootView.findViewById(R.id.circularProgressbar);

        ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) player1Emo.getLayoutParams();
        ConstraintLayout.LayoutParams params2 = (ConstraintLayout.LayoutParams) player2Emo.getLayoutParams();

        List<View> temp = new ArrayList<>();
        GenricDataCallback onEmo = (emo, id) -> {

            final TextView textViewP2 = new TextView(ctx);
            textViewP2.setLayoutParams(id == 1 ? params1 : params2);
            textViewP2.setId(View.generateViewId());
            textViewP2.setTextColor(ResourceUtils.getColor(R.color.colorTextPrimary));
            textViewP2.setText(emo);
            textViewP2.setTextSize(48);
            textViewP2.setTranslationY(0);
            textViewP2.setAlpha(1.0f);
            textViewP2.animate().alpha(0.5f)
                    .translationYBy(-500f)
                    .setListener(new AbstractAnimatorListener() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
//                            contPlayers.removeView(textViewP2);
                        }
                    })
                    .setDuration(delay * 3).start();
            contPlayers.addView(textViewP2);
            temp.add(textViewP2);
        };
        GenricObjectCallback<String> onEmoRecieved = new GenricObjectCallback<String>() {
            @Override
            public void onEntity(String data) {
                try {
                    onEmo.onStart(data, 2);
                } catch (Error | Exception e) {
                    e.printStackTrace();
                }
            }
        };
        player2Listener.setOnEmoFromPlayer2(onEmoRecieved);
        GenricCallback addEmoButtons = () -> {

            List<String> emos = player2Listener.getEmos();
            GenriXAdapter<String> genriXAdapter = new GenriXAdapter<String>(ctx, R.layout.row_emo, emos) {
                @Override
                public void onBindViewHolder(@NonNull @NotNull GenriXAdapter.CustomViewHolder viewHolder, int i) {
                    String e = emos.get(i);
                    viewHolder.textView(R.id.potText).setText(e);
                    viewHolder.view(R.id.root).setOnClickListener(v -> {

                        onEmo.onStart(e, 1);
                        player2Listener.sendEmoToPlayer2(e);

                    });
                }
            };
            listEmos.setLayoutManager(new LinearLayoutManager(ctx, RecyclerView.HORIZONTAL, false));
            listEmos.setAdapter(genriXAdapter);

        };
        addEmoButtons.onStart();

        game.setOnGameScoreUpdate(() -> {
            player1score.setText("" + game.getPlayer1wins());
            player2score.setText("" + game.getPlayer2wins());

            for (View v : temp) {
                contPlayers.removeView(v);
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
            }
        };
        TickerAnimator tickerAnimator = new TickerAnimator(new GenricDataCallback() {
            @Override
            public void onStart(String message, int statusCode) {
                int progress = statusCode * 100 / (MAX_USER_WAIT);
                circularProgressbar.setProgress(progress);
                timerText.setText("" + statusCode / 1000);
            }
        }, () -> {
            // Turn finished
        }, timerText) {
            @Override
            public void onCompleted() {
                if (game.getState() != 1) {
                    return;
                }
                if (game.getTurnOfPlayerId().equals(game.getPlayer1Id())) {
                    player2Listener.sendTapOnPotToPlayer2(null);
                    onNewTurn.onStart(game.getPlayer2Id(), 2);
                } else {
                    onNewTurn.onStart(game.getPlayer1Id(), 1);
                }
                reset();
            }
        };

        if (!utl.isEmpty(ctx.user.getImage()))
            Picasso.get().load(ctx.user.getImage()).error(R.drawable.account)
                    .placeholder(R.drawable.account)
                    .into(player1Image);
        if (!utl.isEmpty(game.getPlayer2().getImage()))
            Picasso.get().load(game.getPlayer2().getImage()).error(R.drawable.account)
                    .placeholder(R.drawable.account)
                    .into(player2Image);
        player2Name.setText(game.getPlayer2().getName());
        contPlayers.setVisibility(View.VISIBLE);
        utl.addPressReleaseAnimation(player1Image);
        utl.addPressReleaseAnimation(player2Image);


        List<Pot> pots = game.generatePots();
        createPots(true, false, contView, ctx.getLayoutInflater(), contPots, pots, onNewTurn, tickerAnimator);

        info.setText(R.string.watch);
        utl.animate_land(info);
        CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerText.setText(String.format("%d", millisUntilFinished / 1000));
                utl.animate_land(timerText);
                int progress = (int) millisUntilFinished * 100 / (MAX_USER_WAIT);
                circularProgressbar.setProgress(progress);

                if (millisUntilFinished < 6000) {

                    TransitionManager.beginDelayedTransition(contPots, new ChangeBounds());
                    Collections.shuffle(pots);
                    createPots(false, false, contView, ctx.getLayoutInflater(), contPots, pots, onNewTurn, tickerAnimator);

                }
            }

            @Override
            public void onFinish() {
//                setupConcludeGame(contView);
                tickerAnimator.start(MAX_USER_WAIT);
                createPots(false, true, contView, ctx.getLayoutInflater(), contPots, pots, onNewTurn, tickerAnimator);
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
                public void onEntity(Pot pot) {

                    if (game.getState() == 1)
                        game.getOnGameScoreUpdate().onStart();

                    if (game.isOver()) {
                        onGameConclude.onEntity(contView);
                        tickerAnimator.stop();
                    } else {
                        tickerAnimator.reset();
                        if (pot.getOwnedByUserId().equals(game.getPlayer2Id())) {
                            onNewTurn.onStart(game.getPlayer1Id(), 1);
                        } else {
                            playSOund.onStart(game.getPlayer1Id(), 1);
                            player2Listener.sendTapOnPotToPlayer2(pot);
                            onNewTurn.onStart(game.getPlayer2Id(), 2);
                        }
                    }
                }
            }));
        }

    }

}

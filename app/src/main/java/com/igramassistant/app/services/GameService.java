package com.igramassistant.app.services;

import android.animation.Animator;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.ChangeBounds;
import androidx.transition.Scene;
import androidx.transition.TransitionManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.igramassistant.app.R;
import com.igramassistant.app.adapters.GenriXAdapter;
import com.igramassistant.app.interfaces.AbstractAnimatorListener;
import com.igramassistant.app.interfaces.GenricCallback;
import com.igramassistant.app.interfaces.GenricDataCallback;
import com.igramassistant.app.interfaces.GenricObjectCallback;
import com.igramassistant.app.models.Game;
import com.igramassistant.app.models.game.Player2Listener;
import com.igramassistant.app.models.game.Pot;
import com.igramassistant.app.ui.BaseActivity;
import com.igramassistant.app.utils.ResourceUtils;
import com.igramassistant.app.utils.TickerAnimator;
import com.igramassistant.app.utl;
import com.igramassistant.app.views.LoadingView;
import com.igramassistant.app.views.RoundRectCornerImageView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
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
    Handler handler = new Handler();
    CountDownTimer waitForNextRoundTimer;
    TickerAnimator playerTimeoutTimer;
    CountDownTimer playerTimeoutTimer2;
    private Button startGame;
    private ConstraintLayout contPlayers;
    private TextView player1Name;
    private TextView vsText;
    private ProgressBar circularProgressbar;
    private LoadingView loader;
    private TextView timerText;
    private TextView player2Name;
    private TextView player2score;
    private RoundRectCornerImageView player2Image;
    private TextView player2Emo;
    private TextView player1score;
    private RoundRectCornerImageView player1Image;
    private TextView player1Emo;
    private TextView info;
    private ConstraintLayout contMid;
    private ImageView resultCup;
    private TextView resultText;
    private TextView resultTextSub;
    private GridLayout contPots;
    private ExtendedFloatingActionButton pokeBtn;
    private ConstraintLayout contEmos;
    private RecyclerView listEmos;

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
        game.setMAX_GAME_ROUNDS(FirebaseRemoteConfig.getInstance().getLong("game_rounds"));
        playSOund = (player, pid) -> {
            if(true)
                return;
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
        player2Listener.setOnTapPotFromPlayer2(onTapPotRecieved);
    }

    private void findViews(View rootView) {
        startGame = (Button) rootView.findViewById(R.id.startGame);
        contPlayers = (ConstraintLayout) rootView.findViewById(R.id.contPlayers);
        player1Name = (TextView) rootView.findViewById(R.id.player1Name);
        vsText = (TextView) rootView.findViewById(R.id.vsText);
        circularProgressbar = (ProgressBar) rootView.findViewById(R.id.circularProgressbar);
        loader = (LoadingView) rootView.findViewById(R.id.loader);
        timerText = (TextView) rootView.findViewById(R.id.timerText);
        player2Name = (TextView) rootView.findViewById(R.id.player2Name);
        player2score = (TextView) rootView.findViewById(R.id.player2score);
        player2Image = (RoundRectCornerImageView) rootView.findViewById(R.id.player2Image);
        player2Emo = (TextView) rootView.findViewById(R.id.player2Emo);
        player1score = (TextView) rootView.findViewById(R.id.player1score);
        player1Image = (RoundRectCornerImageView) rootView.findViewById(R.id.player1Image);
        player1Emo = (TextView) rootView.findViewById(R.id.player1Emo);
        info = (TextView) rootView.findViewById(R.id.info);
        contMid = (ConstraintLayout) rootView.findViewById(R.id.contMid);
        resultCup = (ImageView) rootView.findViewById(R.id.resultCup);
        resultText = (TextView) rootView.findViewById(R.id.resultText);
        resultTextSub = (TextView) rootView.findViewById(R.id.resultTextSub);
        contPots = (GridLayout) rootView.findViewById(R.id.contPots);
        pokeBtn = (ExtendedFloatingActionButton) rootView.findViewById(R.id.pokeBtn);
        contEmos = (ConstraintLayout) rootView.findViewById(R.id.contEmos);
        listEmos = (RecyclerView) rootView.findViewById(R.id.listEmos);
    }

    public void setupInGame(LinearLayout contView) {
        if (contView.getChildCount() > 0)
            contView.removeAllViews();
        game.setState(1);

        View rootView = ctx.getLayoutInflater().inflate(R.layout.fragment_game, contView);
        findViews(rootView);
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
//            if (playerId.equals(game.getPlayer1Id())) {
//                info.setText(String.format(ResourceUtils.getString(R.string.find_char),
//                        game.getCurrentMagicPot().getValue()));
//                utl.animate_land(info);
//            } else if (playerId.equals(game.getPlayer2Id())) {
//                info.setText(game.getPlayer2().getName() + "'s " + ResourceUtils.getString(R.string.turn));
//                utl.animate_land(info);
//            }
        };
        List<Pot> pots = game.generatePots(true);

        GenricCallback setNewPot = () -> {

            pots.clear();
            pots.addAll(game.generatePots(true));
            game.setCurrentMagicPot(utl.randomElement(pots));
        };

        GenricObjectCallback<Pot> onTapped =
                new GenricObjectCallback<Pot>() {
                    @Override
                    public void onEntity(Pot data) {
                        game.registerTap();
                        player2Listener.setOnlineTill(System.currentTimeMillis() + utl.randomInt(40000,60000));
                        long p1time = game.elapsedSinceLastRound;
                        long p2time = player2Listener.getTimeTaken(p1time);
                        game.setPlayer2Time(game.getPlayer2Time() + p2time);
                        String sign = p1time < p2time ? "<" :">";
                        boolean isPlayer1Won = p1time < p2time;
                        if(isPlayer1Won){
                            game.setPlayer1wins(game.getPlayer1wins() + 1);
                        }
                        else{
                            game.setPlayer2wins(game.getPlayer2wins() + 1);
                        }
                        player2Listener.waitForPlayer2(isPlayer1Won);

                        contPlayers.postDelayed(() -> {

                            if (p1time < p2time) {
                                YoYo.with(Techniques.Bounce)
                                        .duration(700)
                                        .repeat(5)
                                        .playOn(player1Image);
                            } else {
                                YoYo.with(Techniques.Bounce)
                                        .duration(700)
                                        .repeat(5)
                                        .playOn(player2Image);
                            }

                            timerText.setText(
                                    Html.fromHtml(utl.getHtml(ctx, String.format("%.3f", p1time / 1000f), R.color.dotpotblue)
                                            + " " + sign + " " +
                                            utl.getHtml(ctx, String.format("%.3f", p2time / 1000f), R.color.material_orange_700)
                                    ));

                                game.getOnGameScoreUpdate().onStart();

                        }, utl.randomInt(1000, 2500));
                        playerTimeoutTimer.stop();
                        playerTimeoutTimer2.cancel();
                        waitForNextRoundTimer.cancel();
                        if (game.isOver()) {
                            timerText.setVisibility(View.GONE);
                            onGameConclude.onEntity(contView);
                        } else {
                            playSOund.onStart(game.getPlayer1Id(), 1);
                            onNewTurn.onStart("", 0);
                            info.setText(R.string.get_ready);
                            waitForNextRoundTimer.start();
                        }
                    }
                };


        playerTimeoutTimer2 = new CountDownTimer(MAX_USER_WAIT, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                game.elapsedSinceLastRound = MAX_USER_WAIT - millisUntilFinished;
                float f = (MAX_USER_WAIT - millisUntilFinished) / 1000.0f;
                timerText.setText(String.format("%.3f", f));


                long progress = millisUntilFinished * 100 / (MAX_USER_WAIT);
                circularProgressbar.setProgress((int) progress);
            }

            @Override
            public void onFinish() {
                YoYo.with(Techniques.Flash)
                        .duration(700)
                        .playOn(timerText);
            }
        };
        GenricCallback resetplayerTimeoutTimer2 = () -> {

            playerTimeoutTimer2.cancel();
            playerTimeoutTimer2.onFinish();
            playerTimeoutTimer2.start();
        };
        waitForNextRoundTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long statusCode) {
                long progress = statusCode * 100 / (MAX_USER_WAIT);
                circularProgressbar.setProgress((int) progress);
            }

            @Override
            public void onFinish() {
                setNewPot.onStart();
                resetplayerTimeoutTimer2.onStart();
                onNewTurn.onStart(game.getPlayer1Id(), 1);
                playerTimeoutTimer.start(MAX_USER_WAIT);
                String findChar = utl.getHtml(ctx,ResourceUtils.getString(R.string.find_char),R.color.offwhite)
                        + "<b>"+utl.getHtml(ctx," "+game.getCurrentMagicPot().getValue(),R.color.colorGoldenWin)+"</b>"
                        + utl.getHtml(ctx," !!!",R.color.offwhite);
                info.setText(Html.fromHtml(findChar));
                YoYo.with(Techniques.Bounce)
                        .repeat(3)
                        .duration(700)
                        .playOn(info);
//                info.setBackgroundTintList(ContextCompat.getColorStateList(ctx, R.color.colorPrimary));
//                createGameView(false, true, contView, ctx.getLayoutInflater(), contPots, pots, onNewTurn, playerTimeoutTimer);
            }
        };


        playerTimeoutTimer = new TickerAnimator((message, statusCode) -> {

            AutoTransition autoTransition = new AutoTransition();
            autoTransition.removeTransition(autoTransition.getTransitionAt(0));
            autoTransition.removeTransition(autoTransition.getTransitionAt(1));
//            autoTransition.addTransition(new ChangeBounds());
            autoTransition.setDuration(playerTimeoutTimer.getINTERVAL() / 4);

            ChangeBounds myTransition = new ChangeBounds();
            myTransition.setDuration(1000L);

            TransitionManager.go(new Scene(contPots), autoTransition);
            Collections.shuffle(pots);
            createGameView(true,
                    true,
                    contView,
                    ctx.getLayoutInflater(),
                    contPots,
                    pots,
                    onNewTurn,
                    onTapped);
        }, () -> {
        }, new TextView(ctx)) {

            @Override
            public void start(int count) {
                super.start(count);

            }

            @Override
            public void stop() {
                super.stop();
            }

            @Override
            public void reset() {
                setNewPot.onStart();
                resetplayerTimeoutTimer2.onStart();
                super.reset();
            }

            @Override
            public void onCompleted() {
                if (game.getState() != 1) {
                    return;
                }

                onTapped.onEntity(null);

//
//                if (game.getTurnOfPlayerId().equals(game.getPlayer1Id())) {
//                    player2Listener.sendTapOnPotToPlayer2(null);
//                    onNewTurn.onStart(game.getPlayer2Id(), 2);
//                } else {
//                    onNewTurn.onStart(game.getPlayer1Id(), 1);
//                }

            }
        };
        playerTimeoutTimer.setINTERVAL(3000);

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


        createGameView(true,
                false,
                contView,
                ctx.getLayoutInflater(),
                contPots,
                pots,
                onNewTurn,
                onTapped);

        utl.animate_land(info);
        info.setText(R.string.get_ready);
        onNewTurn.onStart("", 0);
        waitForNextRoundTimer.start();

    }

    protected void createGameView(boolean isPreview,
                                  boolean startGame,
                                  LinearLayout contView,
                                  LayoutInflater inflater,
                                  ViewGroup layout,
                                  List<Pot> titles,
                                  GenricDataCallback onNewTurn,
                                  GenricObjectCallback<Pot> tickerAnimator) {
        layout.removeAllViews();


        for (Pot title : titles) {

            layout.addView(title.inflate(isPreview, startGame, inflater, layout, explosionField, new GenricObjectCallback<Pot>() {
                @Override
                public void onEntity(Pot pot) {


                    tickerAnimator.onEntity(pot);
                    onNewTurn.onStart(game.getPlayer1Id(), 1);
//                        if (pot.getOwnedByUserId().equals(game.getPlayer2Id())) {
//                            onNewTurn.onStart(game.getPlayer1Id(), 1);
//                        } else {
//                            playSOund.onStart(game.getPlayer1Id(), 1);
//                            player2Listener.sendTapOnPotToPlayer2(pot);
//                            onNewTurn.onStart(game.getPlayer2Id(), 2);
//                        }

                }
            }));
        }

    }

    public void cancelAllTimers(){
        waitForNextRoundTimer.cancel();
        playerTimeoutTimer.stop();
        playerTimeoutTimer2.cancel();
    }

}

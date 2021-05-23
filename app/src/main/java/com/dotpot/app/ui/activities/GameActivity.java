package com.dotpot.app.ui.activities;

import android.animation.Animator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.dotpot.app.R;
import com.dotpot.app.binding.WalletViewModel;
import com.dotpot.app.interfaces.AbstractAnimatorListener;
import com.dotpot.app.interfaces.GenricCallback;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Game;
import com.dotpot.app.models.game.Player2Listener;
import com.dotpot.app.services.GameService;
import com.dotpot.app.services.RestAPI;
import com.dotpot.app.ui.BaseActivity;
import com.dotpot.app.ui.fragments.AddCreditFragment;
import com.dotpot.app.utils.ObjectTransporter;
import com.dotpot.app.utils.ResourceUtils;
import com.dotpot.app.utils.TickerAnimator;
import com.dotpot.app.utl;
import com.dotpot.app.views.RoundRectCornerImageView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.squareup.picasso.Picasso;

public class GameActivity extends BaseActivity {

    Game game;
    Player2Listener player2Listener;
    ConstraintLayout container;
    long delay = 600;
    GenricCallback onReplayStart = () -> {

        AddCreditFragment.checkWalletAndStartGame(game.getAmount(), new GenricObjectCallback<Game>() {
            @Override
            public void onEntity(Game data) {
                if (data != null) {
                    inAppNavService.startGame(data);
                    finish();
                }
            }

            @Override
            public void onError(String message) {
                utl.snack(act, message);
            }
        }, new GenricObjectCallback<Game>() {

            @Override
            public void onError(String message) {
                utl.diagBottom(ctx, getString(R.string.insufficient_credits_header),
                        getString(R.string.insufficient_credits), true, getString(R.string.finish), new GenricCallback() {
                            @Override
                            public void onStart() {
                                finish();
                            }
                        });
            }
        }, game.getPlayer2Id());
    };
    boolean destroyedViews = false;
    /* ============== PRE-GAME : SELECT PLAYER ========= */
    private int MAX_USER_WAIT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        LinearLayout contView = findViewById(R.id.contView);
        container = findViewById(R.id.container);
        MAX_USER_WAIT = (int) mFirebaseRemoteConfig.getLong("max_user_waiting") * 1000;


        String gameId = getIntent().getStringExtra("gameId");
        game = (Game) ObjectTransporter.getInstance().remove(gameId);
        if (game == null) {
            utl.toast(ctx, getstring(R.string.error_msg_try_again));
            finish();
            return;
        }

        setupPreGame(contView);

    }



    /* -------------- PRE-GAME : SELECT PLAYER --------- */


    /* ============== IN-GAME ========= */

    @Override
    public void onBackPressed() {

        if (game.getState() < 2)
            utl.diagBottom(ctx, "", getString(R.string.are_you_sure_back), true, getString(R.string.confirm), () -> {
                game.setState(2);
                finish();
            });
        else
            finish();

    }

    private void setupPreGame(LinearLayout contView) {
        if (contView.getChildCount() > 0)
            contView.removeAllViews();
        game.setState(0);
        View rootView = getLayoutInflater().inflate(R.layout.fragment_pregame, contView);
        LinearLayout searchingCont = (LinearLayout) rootView.findViewById(R.id.searchingCont);
        ImageView animLogo = (ImageView) rootView.findViewById(R.id.animLogo);
        TextView timerText = (TextView) rootView.findViewById(R.id.timerText);
        TextView searchingText = (TextView) rootView.findViewById(R.id.searchingText);
        ConstraintLayout contPlayers = (ConstraintLayout) rootView.findViewById(R.id.contPlayers);
        TextView vsText = (TextView) rootView.findViewById(R.id.vsText);
        RoundRectCornerImageView player2Image = (RoundRectCornerImageView) rootView.findViewById(R.id.player2Image);
        TextView player2Name = (TextView) rootView.findViewById(R.id.player2Name);
        RoundRectCornerImageView player1Image = (RoundRectCornerImageView) rootView.findViewById(R.id.player1Image);
        TextView player1Name = (TextView) rootView.findViewById(R.id.player1Name);
        Button startGame = (Button) rootView.findViewById(R.id.startGame);
        if (!utl.isEmpty(user.getImage()))
            Picasso.get().load(user.getImage()).error(R.drawable.account)
                    .placeholder(R.drawable.account)
                    .into(player1Image);

        GenricCallback moveAnimatePlayers = () -> {

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
        final int count =Math.max(6,(utl.randomInt(MAX_COUNT - 10, MAX_COUNT)));
        utl.animate_avd(animLogo);

        TickerAnimator tickerAnimator = new TickerAnimator((message, time) -> {

            timerText.setText(String.format("%d", ((time) / 1000) + 1));

            timerText.setScaleX(1.5f);
            timerText.setScaleY(1.5f);

            timerText.animate().setDuration(1000).scaleX(1);
            timerText.animate().setDuration(1000).scaleY(1);

            if (!player2Name.getText().toString().equals(game.getPlayer2().getName())) {

                player2Image.setVisibility(View.VISIBLE);
                if (!utl.isEmpty(game.getPlayer2().getImage()))
                    Picasso.get().load(game.getPlayer2().getImage()).error(R.drawable.account)
                            .placeholder(R.drawable.account)
                            .into(player2Image);
                player2Name.setText(game.getPlayer2().getName());


                player2Listener = Player2Listener.builder()
                        .game(game)
                        .player2(game.getPlayer2()).build();

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
//            TransitionManager.beginDelayedTransition(container);
            GameService gameService =
                    new GameService(act, game, delay, player2Listener, MAX_USER_WAIT, new GenricObjectCallback<LinearLayout>() {
                        @Override
                        public void onEntity(LinearLayout data) {
                            setupConcludeGame(data);
                        }
                    });
            gameService.setupInGame(contView);
        }, timerText);


        tickerAnimator.start(MAX_COUNT * 1000);

    }

    /* -------------- IN-GAME --------- */


    /* ============== FINISH-GAME ========= */
    private void setupConcludeGame(LinearLayout contView) {
//        if (contView.getChildCount() > 0)
//            contView.removeAllViews();

        game.setState(2);


        View rootView = getLayoutInflater().inflate(R.layout.fragment_game, contView);

        Button startGame = (Button) rootView.findViewById(R.id.startGame);
        ConstraintLayout contPlayers = (ConstraintLayout) rootView.findViewById(R.id.contPlayers);
        TextView player1Name = (TextView) rootView.findViewById(R.id.player1Name);
        TextView vsText = (TextView) rootView.findViewById(R.id.vsText);
        ProgressBar circularProgressbar = (ProgressBar) rootView.findViewById(R.id.circularProgressbar);
        TextView timerText = (TextView) rootView.findViewById(R.id.timerText);
        TextView player2Name = (TextView) rootView.findViewById(R.id.player2Name);
        TextView player2score = (TextView) rootView.findViewById(R.id.player2score);
        RoundRectCornerImageView player2Image = (RoundRectCornerImageView) rootView.findViewById(R.id.player2Image);
        TextView player2Emo = (TextView) rootView.findViewById(R.id.player2Emo);
        TextView player1score = (TextView) rootView.findViewById(R.id.player1score);
        RoundRectCornerImageView player1Image = (RoundRectCornerImageView) rootView.findViewById(R.id.player1Image);
        TextView player1Emo = (TextView) rootView.findViewById(R.id.player1Emo);
        TextView info = (TextView) rootView.findViewById(R.id.info);
        ConstraintLayout contMid = (ConstraintLayout) rootView.findViewById(R.id.contMid);
        TextView resultText = (TextView) rootView.findViewById(R.id.resultText);
        TextView resultTextSub = (TextView) rootView.findViewById(R.id.resultTextSub);
        GridLayout contPots = (GridLayout) rootView.findViewById(R.id.contPots);
        ConstraintLayout contEmos = (ConstraintLayout) rootView.findViewById(R.id.contEmos);
        ExtendedFloatingActionButton pokeBtn = (ExtendedFloatingActionButton) rootView.findViewById(R.id.pokeBtn);
        RecyclerView listEmos = (RecyclerView) rootView.findViewById(R.id.listEmos);
        ImageView resultCup = rootView.findViewById(R.id.resultCup);

        GenricObjectCallback<String> onReplayRequested = new GenricObjectCallback<String>() {
            @Override
            public void onEntity(String data) {
                pokeBtn.setText(R.string.accept_rematch);
                resultTextSub.setTextColor(getcolor(R.color.colorGoldenWin));

                CountDownTimer ctr = new CountDownTimer(60000, 2000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        try {
                            YoYo.with(Techniques.Bounce)
                                    .duration(500)
                                    .playOn(pokeBtn);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFinish() {

                    }
                }.start();

                YoYo.with(Techniques.Tada).repeatMode(YoYo.INFINITE)
                        .duration(1000)
                        .playOn(resultTextSub);
//                resultTextSub.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.zoominout));
//                pokeBtn.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.zoominout));


                pokeBtn.setOnClickListener(v -> {
                    try {

                        resultCup.setOnClickListener(null);
                        pokeBtn.setOnClickListener(null);

                        pokeBtn.setText(ResourceUtils.getString(R.string.loading));
                        onReplayStart.onStart();
                        ctr.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
                resultTextSub.setText(String.format(getString(R.string.rematch_requested), game.getPlayer2().getName()));
//                utl.diagBottom(ctx, getString(R.string.rematch), String.format(getString(R.string.rematch_requested), game.getPlayer2().getName()), true, getString(R.string.accept), R.drawable.replay, () -> {
//                });

            }
        };

        player2Listener.setOnReplayRequestFromPlayer2(onReplayRequested);

//        List<View> potsView = new ArrayList<>();
//        for(int i=0;i<contPots.getChildCount();i++){
//            View pot = contPots.getChildAt(i);
//            potsView.add(pot);
//        }
//        final Integer[] i = {0};
//        CountDownTimer countDownTimer = new CountDownTimer(2000,150) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                if(i[0] < potsView.size()){
//                    explosionField.explode(potsView.get(i[0]));
//                    i[0]++;
//                }
//            }
//
//            @Override
//            public void onFinish() {
//                potsView.clear();
//            }
//        };

//        explosionField.clear();
//        explosionField.explode(contPots);
//

        info.setVisibility(View.GONE);
        circularProgressbar.setVisibility(View.GONE);
        View loader = rootView.findViewById(R.id.loader);
        loader.animate().alpha(1f).setDuration(500).start();


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
                game.setWinnerId(data.getWinnerId());
                contPots.animate().alpha(0.0f).setListener(new AbstractAnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }
                }).setDuration(delay).start();

                loader.animate().alpha(0f).setDuration(500).start();
//                utl.snack(act, "Game Finished !!! " + data.isPlayer1Won());
                RestAPI.getInstance().invalidateCacheWalletAndTxns();
                WalletViewModel.getInstance().refresh("");

                resultTextSub.setVisibility(View.VISIBLE);
                resultText.setVisibility(View.VISIBLE);

                resultCup.setVisibility(View.VISIBLE);
                pokeBtn.setVisibility(View.VISIBLE);
                pokeBtn.setOnClickListener(v -> {
                    pokeBtn.setTag("loading");
                    loader.animate().alpha(1f).setDuration(500).start();
                    resultTextSub.setText(String.format(getString(R.string.waiting_for_player), game.getPlayer2().getName()));
                    player2Listener.sendReplayRequest(onReplayStart);
                });
                if (game.isPlayer1Won()) {
//                    YoYo.with(Techniques.Bounce)
//                            .duration(100)
//                            .playOn(resultCup);
                    resultCup.setImageResource(R.drawable.win);
                    if (utl.randomDecision(70)) {
                        player2Listener.emoStorm(true);
                    }
                    resultText.setText(String.format(getString(R.string.you_won), getString(R.string.currency), data.getAward()));
                    resultTextSub.setText(String.format(getString(R.string.won_info), game.getPlayer2().getName(), Math.abs(game.getPlayer1wins() - game.getPlayer2wins())));
                } else {
                    if (utl.randomDecision(70)) {
                        player2Listener.emoStorm(false);
                    }
                    resultText.setTextColor(ResourceUtils.getColor(R.color.colorTextPrimary));
//                    YoYo.with(Techniques.Pulse)
//                            .duration(1000)
//                            .playOn(resultCup);
                    resultCup.setImageResource(R.drawable.replay);
                    resultCup.setOnClickListener(v -> {
                        pokeBtn.callOnClick();
                    });
                    resultText.setText(getString(R.string.you_lost));
                    resultTextSub.setText(String.format(getString(R.string.defeated_info), game.getPlayer2().getName(), Math.abs(game.getPlayer2wins() - game.getPlayer1wins())));
                }

                if (game.isRematch()) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if(pokeBtn.getTag()==null)
                            onReplayRequested.onEntity("Rematch ?");
                        }
                    }, utl.randomInt(1000, MAX_USER_WAIT));
                }

                resultCup.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.zoominout));


            }

            @Override
            public void onError(String message) {
                utl.snack(act, "Error !!! " + message);
            }
        });

    }

    @Override
    protected void onDestroy() {
        destroyedViews = true;
        super.onDestroy();
    }



    /* -------------- FINISH-GAME --------- */


}

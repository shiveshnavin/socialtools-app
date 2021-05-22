package com.dotpot.app.models.game;

import android.os.CountDownTimer;
import android.os.Handler;

import com.dotpot.app.interfaces.GenricCallback;
import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Game;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.utl;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Player2Listener {

    public List<String> emoList;

    GenricUser player2;
    Game game;
    GenricObjectCallback<Pot> onTapPotFromPlayer2;
    GenricObjectCallback<String> onEmoFromPlayer2;
    GenricObjectCallback<String> onReplayRequestFromPlayer2;
    private int MAX_USER_WAIT;
    private boolean convoIsIcedBroke = true;

    public void sendTapOnPotToPlayer2(@Nullable Pot pot) {
        waitForPlayer2();
    }

    long lastEmoFromP1 = 0;
    long onlineTill;
    public void sendEmoToPlayer2(String emo) {
        emoList = getEmos();
        if(System.currentTimeMillis() > onlineTill){
            return;
        }
        if(System.currentTimeMillis() - lastEmoFromP1 < 2000){
            if(utl.randomDecision(50)){
                return;
            }
        }
        lastEmoFromP1 = System.currentTimeMillis();
        int delay = utl.randomInt(3000, (MAX_USER_WAIT - 1000));
        boolean send = utl.randomDecision(50);
        if (send)
            new Handler().postDelayed(() -> {
                if (emoList.indexOf(emo) <= emoList.size() / 2) {
                    int random = utl.randomInt(0, emoList.size() / 2);
                    onEmoFromPlayer2.onEntity(emoList.get(random));
                } else {
                    int random = utl.randomInt(emoList.size() / 2, emoList.size() - 1);
                    onEmoFromPlayer2.onEntity(emoList.get(random));
                }
            }, delay);
    }

    public void emoStorm(boolean isCurses){

        List<String> ems = (isCurses ? badEmos():getEmos());
        CountDownTimer countDownTimer = new CountDownTimer(utl.randomInt(MAX_USER_WAIT/2,(int)(MAX_USER_WAIT*1.5)),300) {
            @Override
            public void onTick(long millisUntilFinished) {
                onEmoFromPlayer2.onEntity((ems.get(utl.randomInt(0,ems.size()-1))));
            }

            @Override
            public void onFinish() {

            }
        };
        countDownTimer.start();
    }


    public void sendReplayRequest(GenricCallback onReplayStart) {

        setOnReplayRequestFromPlayer2(new GenricObjectCallback<String>() {
            @Override
            public void onEntity(String data) {

            }
        });
        new Handler().postDelayed(()->{
            onReplayStart.onStart();
        },utl.randomInt(4000,MAX_USER_WAIT));
    }

    void waitForPlayer2() {
        List<Pot> unTappedPots = game.getPots().stream().filter(pot -> !pot.isOwned()).collect(Collectors.toList());
        MAX_USER_WAIT = (int) FirebaseRemoteConfig.getInstance().getLong("max_user_waiting") * 1000;

        Random rand = new Random();
        if (unTappedPots.isEmpty()) {
            return;
        }

        if(!convoIsIcedBroke && unTappedPots.size() <= game.getPots().size()/2){
            convoIsIcedBroke = true;
            new Handler().postDelayed(()->{
                onEmoFromPlayer2.onEntity(getEmos().get(utl.randomInt(0,getEmos().size()-1)));
            },1000);
        }
        Pot randomElement = unTappedPots.get(rand.nextInt(unTappedPots.size()));
        onlineTill = System.currentTimeMillis() + utl.randomInt(30000 , 60000);
        int delay = utl.randomInt(1500, (MAX_USER_WAIT - 1000));
        new Handler().postDelayed(() -> {
            if (game.getTurnOfPlayerId().equals(game.getPlayer2Id()))
                onTapPotFromPlayer2.onEntity(randomElement);
        }, delay);

    }

    public List<String> goodEmos(){
        return getEmos().subList(0,emoList.size()/2);
    }
    public List<String> badEmos(){
        return getEmos().subList(emoList.size()/2,emoList.size());
    }

    public List<String> getEmos() {
        if (emoList == null || emoList.isEmpty()) {
            emoList = Arrays.asList(
                    "\uD83D\uDE0D",
                    "\uD83E\uDD2D",
                    "\uD83D\uDE02",
                    "\uD83D\uDD95",
                    "\uD83D\uDE0E",
                    "\uD83E\uDD2C");
        }
        return emoList;
    }

}

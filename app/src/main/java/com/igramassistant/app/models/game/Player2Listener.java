package com.igramassistant.app.models.game;

import android.os.CountDownTimer;
import android.os.Handler;

import com.igramassistant.app.interfaces.GenricCallback;
import com.igramassistant.app.interfaces.GenricObjectCallback;
import com.igramassistant.app.models.Game;
import com.igramassistant.app.models.GenricUser;
import com.igramassistant.app.utl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Player2Listener {

    public List<String> emoList;

    Handler handler;
    GenricUser player2;
    Game game;
    GenricObjectCallback<Pot> onTapPotFromPlayer2;
    GenricObjectCallback<String> onEmoFromPlayer2;
    GenricObjectCallback<String> onReplayRequestFromPlayer2;
    private int MAX_USER_WAIT;
    private boolean convoIsIcedBroke = true;

    public void sendTapOnPotToPlayer2(@Nullable Pot pot) {
        waitForPlayer2(false);
    }

    long lastEmoFromP1 = 0;
    long onlineTill;
    public void sendEmoToPlayer2(String emo) {
        emoList = getEmos();
        if(System.currentTimeMillis() > onlineTill){
            return;
        }
        if(System.currentTimeMillis() - lastEmoFromP1 < 1000){
            if(utl.randomDecision(50)){
                return;
            }
        }

        lastEmoFromP1 = System.currentTimeMillis();
        int delay = utl.randomInt(2000, 6000);
        boolean send = utl.randomDecision(70);
        if (send)
            handler.postDelayed(() -> {

                if(game.getState()==1){
                    if (goodEmos().contains(emo)) {
                        int random = utl.randomInt(0, goodEmos().size()-1);
                        onEmoFromPlayer2.onEntity(goodEmos().get(random));
                    } else {
                        int random = utl.randomInt(0, badEmos().size()-1);
                        onEmoFromPlayer2.onEntity( badEmos().get(random));
                    }
                }
                else {
                    if(game.isPlayer1Won()){
                        int random = utl.randomInt(0, badEmos().size()-1);
                        onEmoFromPlayer2.onEntity( badEmos().get(random));
                    }
                    else {
                        int random = utl.randomInt(0, emoList.size()-1);
                        onEmoFromPlayer2.onEntity( emoList.get(random));
                    }
                }


            }, delay);
    }

    public void emoStorm(boolean isCurses){

        List<String> ems = (isCurses ? badEmos():getEmos());
        String mEmo = ems.get(utl.randomInt(0,getEmos().size()-1));
        int times = utl.randomInt(1,10);

        CountDownTimer countDownTimer = new CountDownTimer(times * 400,400) {
            @Override
            public void onTick(long millisUntilFinished) {
                onEmoFromPlayer2.onEntity(mEmo);
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
        handler.postDelayed(()->{
            onReplayStart.onStart();
        },utl.randomInt(4000,MAX_USER_WAIT));
    }

    public void waitForPlayer2(boolean isPlayer1Won) {
//        List<Pot> unTappedPots = game.getPots().stream().filter(pot -> !pot.isOwned()).collect(Collectors.toList());
//        MAX_USER_WAIT = (int) FirebaseRemoteConfig.getInstance().getLong("max_user_waiting") * 1000;
//
//        Random rand = new Random();
//        if (unTappedPots.isEmpty()) {
//            return;
//        }

        if(utl.randomDecision(40)){
            if(!game.isOver()){
                handler.postDelayed(()->{
                    List<String > eml = (isPlayer1Won?badEmos():getEmos());
                    String mEmo = eml.get(utl.randomInt(0,getEmos().size()-1));
                    int times = utl.randomInt(1,4);
                    CountDownTimer countDownTimer = new CountDownTimer(times * 400,400) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            onEmoFromPlayer2.onEntity(mEmo);
                        }

                        @Override
                        public void onFinish() {

                        }
                    };
                    countDownTimer.start();
                },utl.randomInt(3000,6000));
            }
        }


//        Pot randomElement = unTappedPots.get(rand.nextInt(unTappedPots.size()));
//        onlineTill = System.currentTimeMillis() + utl.randomInt(30000 , 60000);
//        int delay = utl.randomInt(1500, (MAX_USER_WAIT - 1000));
//        new Handler().postDelayed(() -> {
//            if (game.getTurnOfPlayerId().equals(game.getPlayer2Id()))
//                onTapPotFromPlayer2.onEntity(randomElement);
//        }, delay);

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
                    "\uD83E\uDD2C",
                    "\uD83D\uDE0E");
        }
        return emoList;
    }

    List<String> roundWinners ;
    public List<String> findRoundWinners(boolean ip1w,long rounds){
        if(roundWinners!=null && roundWinners.size()>0)
            return roundWinners;
        roundWinners = new ArrayList<>();
        long half=rounds / 2 + 1;
        for(int i=0;i<half;i++){
            roundWinners.add(ip1w?game.getPlayer1Id():game.getPlayer2Id());
        }
        for(long i=half;i<rounds;i++){
            roundWinners.add(!ip1w?game.getPlayer1Id():game.getPlayer2Id());
        }
        Collections.shuffle(roundWinners);
        return roundWinners;
    }

    public long getTimeTaken(long p1Time){
        boolean isP1Won = findRoundWinners(game.isPlayer1Won(),
                game.getMAX_GAME_ROUNDS())
                .get((int)game.getCurrentRound()-1)
                .equals(game.getPlayer1Id());

            if(isP1Won){
                return utl.randomInt(Math.min(4000,(int)p1Time+500) , Math.min(6000,(int)p1Time+2000));
            }
            else {
                return utl.randomInt(100 , Math.min((int)p1Time-100,3000));
            }
    }

}

package com.dotpot.app.models.game;

import android.os.Handler;

import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.Game;
import com.dotpot.app.models.GenricUser;
import com.dotpot.app.utl;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Player2Listener {

    GenricUser player2;
    Game game;
    GenricObjectCallback<Pot> onTapPotFromPlayer2;
    GenricObjectCallback<String> onEmoFromPlayer2;
    GenricObjectCallback<String> onReplayRequestFromPlayer2;
    private int MAX_USER_WAIT;

    public void sendTapOnPotToPlayer2(Pot pot){
        waitForPlayer2();
    }

    public void sendEmoToPlayer2(String emo){
        //onEmoFromPlayer2.onEntity(emo);
    }

    public void sendReplayRequest(){
        onReplayRequestFromPlayer2.onEntity("Replay ?");
    }

    void waitForPlayer2() {
        List<Pot> unTappedPots = game.getPots().stream().filter(pot -> !pot.isOwned()).collect(Collectors.toList());
        MAX_USER_WAIT = (int) FirebaseRemoteConfig.getInstance().getLong("max_user_waiting") * 1000;

        Random rand = new Random();
        if (unTappedPots.isEmpty()) {
            return;
        }
        Pot randomElement = unTappedPots.get(rand.nextInt(unTappedPots.size()));

        int delay = utl.randomInt(1500, (MAX_USER_WAIT - 1000));
        new Handler().postDelayed(() -> {
            onTapPotFromPlayer2.onEntity(randomElement);
        }, delay);

    }

}

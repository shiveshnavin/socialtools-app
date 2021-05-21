package com.dotpot.app.models.game;

import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.GenricUser;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Player2Listener {

    GenricUser player2;
    GenricObjectCallback<Pot> onTapPotFromPlayer2;
    GenricObjectCallback<String> onEmoFromPlayer2;
    GenricObjectCallback<String> onReplayRequestFromPlayer2;

    public void sendTapOnPot(Pot pot){
        onTapPotFromPlayer2.onEntity(pot);
    }

    public void sendEmoToPlayer2(String emo){
        //onEmoFromPlayer2.onEntity(emo);
    }

    public void sendReplayRequest(){
        onReplayRequestFromPlayer2.onEntity("Replay ?");
    }

}

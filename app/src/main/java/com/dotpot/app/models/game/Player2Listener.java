package com.dotpot.app.models.game;

import com.dotpot.app.interfaces.GenricObjectCallback;
import com.dotpot.app.models.GenricUser;

import lombok.Builder;

@Builder
public class Player2Listener {

    GenricUser player2;
    GenricObjectCallback<Pot> onTapPot;
    GenricObjectCallback<String> onSendEmo;
    GenricObjectCallback<String> onReplayRequest;

    public void sendTapOnPot(Pot pot){
        onTapPot.onEntity(pot);
    }

    public void sendEmo(String emo){
        onSendEmo.onEntity(emo);
    }

    public void sendReplayRequest(){
        onReplayRequest.onEntity("Replay ?");
    }

}

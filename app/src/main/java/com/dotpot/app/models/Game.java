package com.dotpot.app.models;

import com.dotpot.app.interfaces.GenricCallback;
import com.dotpot.app.models.game.Pot;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Game {

    private String id;
    private int userseq1;
    private String player1Id;
    private String player2Id;
    private String winnerId;
    private String gameType;
    private String status;
    private float amount;
    private int award;
    private int possibleAward;
    private String currency;
    private int player1wins=0;
    private int player2wins=0;
    private float player1Time=0;
    private float player2Time=0;
    private long timeStamp;
    private boolean rematch = true;

    public static String possibleAwards(Float amount) {
        return ""+ (amount * FirebaseRemoteConfig.getInstance().getDouble("reward_ratio"));
    }

    public boolean isPlayer1Won(){
        return getPlayer1Id().equals(getWinnerId());
    }

    private GenricUser player2;
    private transient List<Pot> pots;
    private transient int state; // 0 = loading , 1 = started , 2 = finished
    private transient volatile String turnOfPlayerId;
    private GenricCallback onGameScoreUpdate;
    private transient Pot currentMagicPot;
    public transient long elapsedSinceLastRound;
    long MAX_GAME_ROUNDS;
    long currentRound=0;


    public List<Pot> generatePots(boolean forceRefresh){
        if(!forceRefresh && pots !=null && !pots.isEmpty())
            return pots;

        pots = new ArrayList<>();
        pots.add(Pot.builder().game(this).value(10).build());
        pots.add(Pot.builder().game(this).value(20).build());
        pots.add(Pot.builder().game(this).value(25).build());
        pots.add(Pot.builder().game(this).value(40).build());
        pots.add(Pot.builder().game(this).value(55).build());
        pots.add(Pot.builder().game(this).value(65).build());
        pots.add(Pot.builder().game(this).value(95).build());
        pots.add(Pot.builder().game(this).value(105).build());
        pots.add(Pot.builder().game(this).value(145).build());

        return pots;
    }

    public boolean registerTap(){
        currentRound++;
        setPlayer1Time(getPlayer1Time() + elapsedSinceLastRound);
        return true;
    }

    public boolean isOver() {
       return   currentRound  >= MAX_GAME_ROUNDS;
//        return pots.stream().allMatch(Pot::isOwned);
    }

    public boolean isPlayer1Turn() {
        return turnOfPlayerId.equals(getPlayer1Id());
    }
}

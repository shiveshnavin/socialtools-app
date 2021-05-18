package com.dotpot.app.models;

import com.dotpot.app.interfaces.GenricCallback;
import com.dotpot.app.models.game.Pot;

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
    private int player1Time;
    private int player2Time;
    private long timeStamp;

    public boolean isPlayer1Won(){
        return getPlayer1Id().equals(getWinnerId());
    }

    private transient GenricUser player2;
    private transient List<Pot> pots;
    private transient int state; // 0 = loading , 1 = started , 2 = finished
    private transient volatile String turnOfPlayerId;
    private GenricCallback onGameScoreUpdate;


    public List<Pot> generatePots(){
        if(pots !=null && !pots.isEmpty())
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


    public boolean isOver() {
        return pots.stream().allMatch(Pot::isOwned);
    }

    public boolean isPlayer1Turn() {
        return turnOfPlayerId.equals(getPlayer1Id());
    }
}

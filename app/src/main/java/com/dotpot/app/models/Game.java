package com.dotpot.app.models;

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
    private int player1wins;
    private int player2wins;
    private int player1Time;
    private int player2Time;
    private long timeStamp;

    private transient GenricUser player2;

    public boolean isPlayer1Won(){
        return getPlayer1Id().equals(getWinnerId());
    }

}

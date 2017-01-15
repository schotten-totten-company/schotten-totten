package com.boardgames.bastien.schotten_totten.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 29/11/2016.
 */

public class Player {

    private final String name;

    private final Hand hand;

    private boolean isPlaying;

    private final PlayerType playerType;

    public Player(final String name, final PlayerType playerType) {
        this.name = name;
        this.hand = new Hand();
        this.isPlaying = false;
        this.playerType = playerType;
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public void setPlaying() {
        this.isPlaying = true;
    }

    public void setNotPlaying() {
        this.isPlaying = false;
    }

}

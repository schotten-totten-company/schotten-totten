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

    private final List<Milestone> capturedMilestones;

    public Player(final String name, final PlayerType playerType) {
        this.name = name;
        this.capturedMilestones = new ArrayList<>();
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

    public List<Milestone> getCapturedMilestones() {
        return capturedMilestones;
    }

    public void setPlaying() {
        this.isPlaying = true;
    }

    public void setNotPlaying() {
        this.isPlaying = false;
    }

}

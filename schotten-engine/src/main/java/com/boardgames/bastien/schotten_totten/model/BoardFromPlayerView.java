package com.boardgames.bastien.schotten_totten.model;

import java.util.List;

public class BoardFromPlayerView {
    private Hand hand;
    private List<Milestone> milestones;
    private PlayerType player; //player to play next move


    public BoardFromPlayerView(Hand hand, List<Milestone> milestones, PlayerType player) {
        this.hand = hand;
        this.milestones = milestones;
        this.player = player;
    }

    public Hand getHand() {
        return hand;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public PlayerType getPlayer() {
        return this.player;
    }
}

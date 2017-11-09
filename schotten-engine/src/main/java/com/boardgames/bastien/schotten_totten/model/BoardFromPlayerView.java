package com.boardgames.bastien.schotten_totten.model;

import java.util.List;

public class BoardFromPlayerView {
    private Hand hand;
    private List<Milestone> milestones;


    public BoardFromPlayerView(Hand hand,List<Milestone> milestones) {
        this.hand = hand;
        this.milestones = milestones;
    }

    public Hand getHand() {
        return hand;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }
}

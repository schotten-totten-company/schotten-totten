package com.boardgames.bastien.schotten_totten.model;

import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 29/11/2016.
 */

public class GameBoard {

    public final int MAX_MILESTONES = 9;

    private final Deck deck;

    private final List<Milestone> milestones;

    public GameBoard() throws EmptyDeckException {

        // create deck
        this.deck = new Deck();

        // create milestones
        this.milestones = new ArrayList<>(MAX_MILESTONES);
        for (int i = 1; i <= MAX_MILESTONES; i++) {
            milestones.add(new Milestone(i));
        }
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

}

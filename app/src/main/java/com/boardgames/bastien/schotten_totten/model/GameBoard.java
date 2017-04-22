package com.boardgames.bastien.schotten_totten.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 29/11/2016.
 */

public class GameBoard implements Serializable {

    public final int MAX_MILESTONES = 9;

    private final Deck deck;

    private final List<Milestone> milestones;

    private Card lastPlayedCard;
    public GameBoard() {

        // create deck
        this.deck = new Deck();
        this.deck.shuffle();

        // create milestones
        this.milestones = new ArrayList<>(MAX_MILESTONES);
        for (int i = 0; i < MAX_MILESTONES; i++) {
            milestones.add(new Milestone(i));
        }
    }

    public void updateLastPlayedCard(final Card c) {
        this.lastPlayedCard = c;
    }
    public Card getLastPlayedCard() {
        return this.lastPlayedCard;
    }

    public List<Card> getPlayedCards() {
        final List<Card> playedCards = new ArrayList();
        for (final Milestone milestone : milestones) {
            for (final Card card : milestone.getPlayer1Side()) {
                playedCards.add(card);
            }
            for (final Card card : milestone.getPlayer2Side()) {
                playedCards.add(card);
            }
        }
        return playedCards;
    }
    public Deck getDeck() {
        return deck;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

}

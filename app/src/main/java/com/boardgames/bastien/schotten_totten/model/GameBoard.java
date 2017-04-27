package com.boardgames.bastien.schotten_totten.model;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 29/11/2016.
 */

public class GameBoard implements Serializable {

    public final int MAX_MILESTONES = 9;

    private final Deck deck;

    private final List<Card> allTheCards = new ArrayList(new Deck().getDeck());

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

    private List<Card> getPlayedCards() {
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

    public List<Card> getCardsNotYetPlayed() {
        //get not yet played cards
        final List<Card> cardsNotYetPlayed = new ArrayList(allTheCards);
        CollectionUtils.filter(cardsNotYetPlayed, new Predicate<Card>() {
            @Override
            public boolean evaluate(final Card cardToFilter) {
                for (final Card playedCard : getPlayedCards()) {
                    if (cardToFilter.getNumber().equals(playedCard.getNumber())
                            && cardToFilter.getColor().equals(playedCard.getColor())) {
                        return false;
                    }
                }
                return true;
            }
        });
        return cardsNotYetPlayed;
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

}

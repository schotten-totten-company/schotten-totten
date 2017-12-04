package com.utils.bastien.schotten_totten.model;

import com.utils.bastien.schotten_totten.exceptions.HandFullException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 28/11/2016.
 */

public class Hand implements Serializable {

   private final List<Card> cards;

    @JsonIgnore
    public final int MAX_HAND_SIZE = 6;

    public Hand() {
        cards = new ArrayList<>();
    }

    public Hand(final List<Card> cards) {
        this.cards = cards;
    }

    public void addCard(final Card c, final int index) throws HandFullException {
        if (cards.size() < MAX_HAND_SIZE) {
            cards.add(index, c);
        } else {
            throw new HandFullException(MAX_HAND_SIZE);
        }
    }

    @JsonIgnore
    public Card playCard(final int i) {
        return cards.remove(i);
    }

    @JsonIgnore
    public int getHandSize() {
        return cards.size();
    }

    public List<Card> getCards() {
        return cards;
    }
}

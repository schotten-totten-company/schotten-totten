package com.boardgames.bastien.schotten_totten;

import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 28/11/2016.
 */

public class Hand {

    private final List<Card> cards;

    private final int MAX_HAND_SIZE = 6;

    public Hand() {
        cards = new ArrayList<Card>();
    }

    public void addCard(final Card c) throws HandFullException {
        if (cards.size() < MAX_HAND_SIZE) {
            cards.add(c);
        } else {
            throw new HandFullException(MAX_HAND_SIZE);
        }

    }

    public Card pickUpCard(final int i) throws CardInitialisationException {
        final Card cardToPickUp = new Card(cards.get(i).getNumber(), cards.get(i).getColor());
        cards.remove(i);
        return cardToPickUp;
    }

    public int getHandSize() {
        return cards.size();
    }
}

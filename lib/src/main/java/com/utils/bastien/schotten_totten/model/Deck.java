package com.utils.bastien.schotten_totten.model;

import com.utils.bastien.schotten_totten.exceptions.EmptyDeckException;

import java.io.Serializable;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Random;
import java.util.Stack;

/**
 * Created by Bastien on 28/11/2016.
 */

public class Deck implements Serializable {

    private final Stack<Card> deckCards = new Stack<>();

    public Stack<Card> getDeck() {
        return this.deckCards;
    }

    public Deck() {
        for (final Card.COLOR c : Card.COLOR.values()) {
            for (final Card.NUMBER n : Card.NUMBER.values()) {
                this.deckCards.push(new Card(n, c));
            }
        }
    }

    public Card drawCard() throws EmptyDeckException {
        try {
            return this.deckCards.pop();
        } catch (final EmptyStackException e) {
            throw new EmptyDeckException();
        }
    }

    public void shuffle() {
        Collections.shuffle(this.deckCards, new Random(System.currentTimeMillis()));
    }
}

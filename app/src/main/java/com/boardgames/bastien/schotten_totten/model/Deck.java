package com.boardgames.bastien.schotten_totten.model;

import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;

import java.util.Collections;
import java.util.EmptyStackException;
import java.util.Random;
import java.util.Stack;

/**
 * Created by Bastien on 28/11/2016.
 */

public class Deck {

    private final Stack<Card> deckCards = new Stack<>();

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

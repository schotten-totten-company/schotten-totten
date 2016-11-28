package com.boardgames.bastien.schotten_totten;

import android.graphics.Color;

import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;

import java.util.EmptyStackException;
import java.util.Stack;

/**
 * Created by Bastien on 28/11/2016.
 */

public class Deck {

    private final Stack<Card> deckCards = new Stack<>();

    public Deck() throws CardInitialisationException {
        for (final Integer c : Utils.getAllowedColors()) {
            for (int i = 1; i <=9; i++) {
                this.deckCards.push(new Card(i, c));
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

    public void Shuffle() {
        //
    }
}

package com.boardgames.bastien.schotten_totten;

/**
 * Created by Bastien on 28/11/2016.
 */

import android.graphics.Color;

import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;

public class Card {

    private final int number;

    private final Integer color;

    public Card(final int n, final Integer c) throws CardInitialisationException {

        if (n < 1 || !Utils.getAllowedColors().contains(c)) {
            final String message = "Number must be set between 1 and 9, colors must be set as BLUE, or CYAN, or RED, or GREEN, or YELLOW, or GRAY";
            throw new CardInitialisationException(message);
        }
        this.number = n;
        this.color = c;
    }

    public int getNumber() {
        return number;
    }

    public Integer getColor() {
        return color;
    }
}

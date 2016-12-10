package com.boardgames.bastien.schotten_totten.model;

/**
 * Created by Bastien on 28/11/2016.
 */

import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;

public class Card {

    public enum COLOR {
        BLUE,
        CYAN,
        RED,
        YELLOW,
        GREEN,
        GREY;
    }

    public enum NUMBER {
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE;

        @Override
        public String toString() {
            return String.valueOf(ordinal() + 1);
        }
    }

    private final NUMBER number;

    private final COLOR color;

    public Card(final NUMBER n, final COLOR c) {
        this.number = n;
        this.color = c;
    }

    public NUMBER getNumber() {
        return number;
    }

    public COLOR getColor() {
        return color;
    }
}

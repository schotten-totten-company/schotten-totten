package com.boardgames.bastien.schotten_totten.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;

/**
 * Created by Bastien on 28/11/2016.
 */

@JsonDeserialize(using = CardDeserializer.class)
public class Card implements Serializable {

    public enum COLOR {
        BLUE,
        CYAN,
        RED,
        YELLOW,
        GREEN,
        GREY;

        @Override
        public String toString() {
            return String.valueOf(ordinal() + 1);
        }
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

    private NUMBER number;

    private COLOR color;

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

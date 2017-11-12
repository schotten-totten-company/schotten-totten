package com.boardgames.bastien.schotten_totten.exceptions;

import com.boardgames.bastien.schotten_totten.model.PlayingPlayerType;

/**
 * Created by Bastien on 19/10/2017.
 */

public class NotYourTurnException extends Exception {

    public NotYourTurnException(final PlayingPlayerType type) {
        super(type.toString() + ", it is not your turn to play!");
    }
}

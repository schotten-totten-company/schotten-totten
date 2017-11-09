package com.boardgames.bastien.schotten_totten.exceptions;

/**
 * Created by Bastien on 05/03/2017.
 */

public class GameCreationException extends Exception {

    public GameCreationException(final Exception e) {
        super("An error occurred during the creation of the game", e);
    }
}

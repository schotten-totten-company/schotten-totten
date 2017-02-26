package com.boardgames.bastien.schotten_totten.exceptions;

/**
 * Created by Bastien on 28/11/2016.
 */

public class EmptyDeckException extends Exception {

    public EmptyDeckException() {
        super("The deck is empty.");
    }
}

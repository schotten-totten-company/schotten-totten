package com.boardgames.bastien.schotten_totten.controllers;

import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;

import java.util.concurrent.Callable;

/**
 * Created by Bastien on 19/10/2017.
 */

public class LanGameManager extends SimpleGameManager {

    public LanGameManager(final String player1Name, final String player2Name) throws GameCreationException {
        super(player1Name, player2Name, System.currentTimeMillis() + "");
    }

}

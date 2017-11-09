package com.boardgames.bastien.schotten_totten.controllers;

import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.model.Game;

/**
 * Created by Bastien on 19/10/2017.
 */

public abstract class AbstractGameManager implements GameManagerInterface {

    protected final Game game;
    protected final long gameUid;

    public AbstractGameManager(final String player1Name, final String player2Name, final long uid) throws GameCreationException {
        game = new Game(player1Name, player2Name);
        this.gameUid = uid;
    }

    public AbstractGameManager(Game game) {
        this.game = game;
        this.gameUid = -1;
    }

    public abstract Game getGame();

}

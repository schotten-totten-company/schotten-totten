package com.boardgames.bastien.schotten_totten.server;

import com.boradgames.bastien.schotten_totten.core.controllers.SimpleGameManager;
import com.boradgames.bastien.schotten_totten.core.model.Game;

/**
 * Created by Bastien on 04/01/2018.
 */

public class OnlineGameManager extends SimpleGameManager {

    public OnlineGameManager(Game game, String uid) {
        super(game, uid);
    }

    public Game getGame() {
        return this.game;
    }
}

package com.boardgames.bastien.schotten_totten.server;

import com.boradgames.bastien.schotten_totten.core.controllers.AbstractGameManager;

/**
 * Created by Bastien on 19/10/2017.
 */

public class LanGameManager {

    private AbstractGameManager gameManager;

    public void start() {
    }

    public boolean isActive() {
        return true;
    }

    public void stop() {
        //SpringApplication.exit(context);
    }

}

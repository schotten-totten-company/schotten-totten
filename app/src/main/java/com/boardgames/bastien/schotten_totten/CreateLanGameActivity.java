package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;

import com.boardgames.bastien.schotten_totten.server.LanGameServer;

public class CreateLanGameActivity extends ServerGameActivity {

    private final LanGameServer lanGameServer = new LanGameServer(8080);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            if (!lanGameServer.isAlive()) {
                lanGameServer.start();
            }

        } catch (final Exception e) {
            showErrorMessage(e);
        }

    }

}

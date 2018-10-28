package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.server.LanGameServer;
import com.boardgames.bastien.schotten_totten.server.OnlineGameManager;
import com.boardgames.bastien.schotten_totten.server.RestGameClient;
import com.boradgames.bastien.schotten_totten.core.exceptions.NoPlayerException;
import com.boradgames.bastien.schotten_totten.core.model.Player;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.net.ConnectException;
import java.util.concurrent.Executors;

public class ServerGameActivity extends GameActivity {

    protected PlayingPlayerType type;
    protected String gameName;
    protected RestGameClient gameClient;
    protected String serverUrl;
    private final LanGameServer lanGameServer = new LanGameServer(8080);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.type = getIntent().getStringExtra("type").equals(PlayingPlayerType.ONE.toString())
                ? PlayingPlayerType.ONE : PlayingPlayerType.TWO;
        this.gameName = getIntent().getStringExtra("gameName");
        this.serverUrl = getIntent().getStringExtra("serverUrl");

        try {

            gameClient = new RestGameClient(this.serverUrl, this.gameName);
            if (this.serverUrl.contains("localhost")) {
                if (!lanGameServer.isAlive()) {
                    lanGameServer.start();
                }
                while (!lanGameServer.isAlive()) {
                    try {
                        Thread.sleep(10);
                    } catch (final InterruptedException e) {
                        showErrorMessage(e);
                    }
                }
                if (!lanGameServer.isAlive()) {
                    throw new ConnectException(this.serverUrl + " cannot start.");
                }
                gameClient.createGame();
            }

            this.gameManager =
                    new OnlineGameManager(gameClient.getGame(), this.gameName);
            initUI(type);
            updateTextField(type.toString());
            if (!this.gameManager.getPlayingPlayer().getPlayerType().equals(type)) {
                disableClick();
                Executors.newSingleThreadExecutor().submit(new GameClientThread());
            }
        } catch (final Exception e) {
            showErrorMessage(e);
        }
    }

    protected class GameClientThread implements Runnable {
        @Override
        public void run() {
            while(!gameClient.getPlayingPlayer().getPlayerType().equals(type)) {
                try {
                    Thread.sleep(5000);
                } catch (final InterruptedException e) {
                    showErrorMessage(e);
                }
            }
            // get game from server
            gameManager =
                    new OnlineGameManager(gameClient.getGame(), gameName);
            // update ui
            runOnUiThread(new Runnable() {
                public void run() {
                    updateUI(type);
                    // check victory
                    try {
                        endOfTheGame(gameManager.getWinner());
                    } catch (final NoPlayerException e) {
                        // nothing to do, just continue to play
                        Toast.makeText(ServerGameActivity.this,
                                getString(R.string.it_is_your_turn), Toast.LENGTH_LONG).show();
                    }
                }
            });
            enableClick();
        }
    }

    @Override
    protected void endOfTurn() {
        disableClick();
        passButton.setVisibility(View.INVISIBLE);
        gameManager.swapPlayers();
        // update game on server
        gameClient.updateGame(((OnlineGameManager)this.gameManager).getGame());
        // wait for your turn
        Executors.newSingleThreadExecutor().submit(new GameClientThread());
        updateTextField(type.toString());
    }

    @Override
    protected void endOfTheGame(final Player winner) {
        super.endOfTheGame(winner);
        if (winner.getPlayerType().equals(type)) {
            gameManager.swapPlayers();
        } else {
            ((RestGameClient)gameManager).deleteGame();
        }
    }

    @Override
    protected void updateTextField(final String updatePointOfViewPlayerName) {
        final Player playingPlayer = gameManager.getPlayingPlayer();
        final PlayingPlayerType playingPlayerType = playingPlayer.getPlayerType();
        final String message = playingPlayerType.equals(type) ?
                playingPlayer.getName() + getString(R.string.it_is_your_turn_message) :
                getString(R.string.not_your_turn_message) ;
        ((TextView) findViewById(R.id.textView)).setText(message);
    }

    @Override
    public void finish() {
        this.lanGameServer.stop();
        super.finish();
    }
}

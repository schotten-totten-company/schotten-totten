package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.server.RestGameClient;
import com.boradgames.bastien.schotten_totten.core.exceptions.NoPlayerException;
import com.boradgames.bastien.schotten_totten.core.model.Player;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.util.concurrent.Executors;

public class ServerGameActivity extends GameActivity {

    private PlayingPlayerType type;
    private String gameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.type = getIntent().getStringExtra("type").equals(PlayingPlayerType.ONE.toString())
                ? PlayingPlayerType.ONE : PlayingPlayerType.TWO;
        this.gameName = getIntent().getStringExtra("gameName");

        try {
            this.gameManager = new RestGameClient("https://schotten-totten.herokuapp.com", this.gameName);
            initUI(type);
            updateTextField(type);
            if (!this.gameManager.getPlayingPlayer().getPlayerType().equals(type)) {
                disableClick();
                Executors.newSingleThreadExecutor().submit(new GameClientThread());
            }
        } catch (final Exception e) {
            showErrorMessage(e);
        }
    }

    private class GameClientThread implements Runnable {
        @Override
        public void run() {
            while(!gameManager.getPlayingPlayer().getPlayerType().equals(type)) {
                try {
                    Thread.sleep(5000);
                } catch (final InterruptedException e) {
                    showErrorMessage(e);
                }
            }
            enableClick();
            // update ui
            runOnUiThread(new Runnable() {
                public void run() {
                    updateUI(gameManager.getPlayingPlayer().getPlayerType());
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
        }
    }

    @Override
    protected void endOfTurn() {
        updateUI(type);
        gameManager.swapPlayers();
        disableClick();
        Executors.newSingleThreadExecutor().submit(new GameClientThread());
        updateTextField(type);
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
    protected void updateTextField(final PlayingPlayerType updatePointOfView) {
        final PlayingPlayerType playingPlayerType = gameManager.getPlayingPlayer().getPlayerType();
        final String message = playingPlayerType.equals(updatePointOfView) ?
                gameManager.getPlayingPlayer().getName() + getString(R.string.it_is_your_turn_message) :
                getString(R.string.not_your_turn_message) ;
        ((TextView) findViewById(R.id.textView)).setText(message);
    }
}

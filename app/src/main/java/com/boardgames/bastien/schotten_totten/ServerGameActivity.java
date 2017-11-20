package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Player;
import com.boardgames.bastien.schotten_totten.model.PlayingPlayerType;
import com.boardgames.bastien.schotten_totten.server.GameClientInterface;
import com.boardgames.bastien.schotten_totten.server.GameDoNotExistException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ServerGameActivity extends GameActivity {

    private final GameClientInterface client = null;// new GameClient();
    private PlayingPlayerType type;
    private String gameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getStringExtra("type").equals(PlayingPlayerType.ONE.toString())
                ? PlayingPlayerType.ONE : PlayingPlayerType.TWO;
        gameName = getIntent().getStringExtra("gameName");

        try {
            this.gameManager = client.getGame(gameName).get();
            initUI(type);
            updateTextField(type);
            if (!this.gameManager.getPlayingPlayer().getPlayerType().equals(type)) {
                disableClick();
                Executors.newSingleThreadExecutor().submit(new GameClientThread());
            }
        } catch (final ExecutionException e) {
            if (e.getCause() instanceof GameDoNotExistException) {
                showAlertMessage(getString(R.string.warning_title),
                        gameName + getString(R.string.game_do_not_exist),true, true);
            } else {
                showErrorMessage(e);
            }
        } catch (final Exception e) {
            showErrorMessage(e);
        }
    }

    @Override
    protected void endOfTurn() {
        updateUI(type);
        disableClick();
        try {
            client.updateGame(gameName, gameManager);
            Executors.newSingleThreadExecutor().submit(new GameClientThread());
        } catch (final ExecutionException | InterruptedException e) {
            showErrorMessage(e);
        }
        updateTextField(type);
    }

    private class GameClientThread implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception {
            while(!client.getGame(gameName).get().getPlayingPlayer().getPlayerType().equals(type)) {
                Thread.sleep(2500);
            }
            gameManager = client.getGame(gameName).get();
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
            return true;
        }
    }

    @Override
    protected void endOfTheGame(final Player winner) {
        super.endOfTheGame(winner);
        if (winner.getPlayerType().equals(type)) {
            //gameManager.swapPlayingPlayer();
            try {
                client.updateGame(gameName, gameManager);
                Executors.newSingleThreadExecutor().submit(new GameClientThread());
            } catch (final ExecutionException | InterruptedException e) {
                showErrorMessage(e);
            }
        } else {
            try {
                client.deleteGame(gameName);
                Executors.newSingleThreadExecutor().submit(new GameClientThread());
            } catch (final ExecutionException | InterruptedException e) {
                showErrorMessage(e);
            }
        }
    }

    @Override
    protected void updateTextField(final PlayingPlayerType updatePointOfView) {
        final PlayingPlayerType playingMilestonePlayerType = gameManager.getPlayingPlayer().getPlayerType();
        final String message = playingMilestonePlayerType.equals(updatePointOfView) ?
                gameManager.getPlayingPlayer().getName() + getString(R.string.it_is_your_turn_message) :
                getString(R.string.not_your_turn_message) ;
        ((TextView) findViewById(R.id.textView)).setText(message);
    }
}

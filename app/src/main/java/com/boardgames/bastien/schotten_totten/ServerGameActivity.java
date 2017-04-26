package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;

import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Player;
import com.boardgames.bastien.schotten_totten.model.PlayerType;
import com.boardgames.bastien.schotten_totten.server.GameClient;
import com.boardgames.bastien.schotten_totten.server.GameDoNotExistException;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class ServerGameActivity extends GameActivity {

    private final GameClient client = new GameClient();
    private PlayerType type;
    private String gameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getIntent().getStringExtra("type").equals(PlayerType.ONE.toString())
                ? PlayerType.ONE : PlayerType.TWO;
        gameName = getIntent().getStringExtra("gameName");

        try {
            this.game = client.getGame(gameName);
            initUI(this.game.getPlayer(type).getHand());
            updateTextField();
            if (!this.game.getPlayingPlayerType().equals(type)) {
                disableClick();
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
    protected void endOfTurn() throws NoPlayerException {
        updateUI();
        disableClick();
        game.swapPlayingPlayerType();
        try {
            client.updateGame(gameName, game);
            Executors.newSingleThreadExecutor().submit(new GameClientThread());
        } catch (final ExecutionException | InterruptedException e) {
            showErrorMessage(e);
        }
        updateTextField();
    }

    private class GameClientThread implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception {
            while(!client.getGame(gameName).getPlayingPlayerType().equals(type)) {
                Thread.sleep(5000);
            }
            game = client.getGame(gameName);
            enableClick();
            updateUI();
            return true;
        }
    }

    @Override
    protected void endOfTheGame(final Player winner) throws NoPlayerException {
        super.endOfTheGame(winner);
        try {
            client.deleteGame(gameName);
            Executors.newSingleThreadExecutor().submit(new GameClientThread());
        } catch (final ExecutionException | InterruptedException e) {
            showErrorMessage(e);
        }
    }
}

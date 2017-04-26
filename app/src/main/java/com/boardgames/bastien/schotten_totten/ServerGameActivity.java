package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;

import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.PlayerType;
import com.boardgames.bastien.schotten_totten.server.GameClient;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
            initUI(this.game.getPlayer(PlayerType.ONE).getHand());

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
            final Future<Game> future =
                    Executors.newSingleThreadExecutor().submit(new GameClientThread());
            game = future.get();

        } catch (final ExecutionException | InterruptedException e) {
            showErrorMessage(e);
        }
        enableClick();
        updateUI();
    }

    private class GameClientThread implements Callable<Game> {
        @Override
        public Game call() throws Exception {
            while(!client.getGame(gameName).getPlayingPlayerType().equals(type)) {
                Thread.sleep(5000);
            }
            return client.getGame(gameName);
        }
    }
}

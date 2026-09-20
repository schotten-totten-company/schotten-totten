package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.boardgames.bastien.schotten_totten.server.LanGameServer;
import com.boardgames.bastien.schotten_totten.server.OnlineGameManager;
import com.boardgames.bastien.schotten_totten.server.RestGameClient;
import com.boradgames.bastien.schotten_totten.core.exceptions.NoPlayerException;
import com.boradgames.bastien.schotten_totten.core.model.Game;
import com.boradgames.bastien.schotten_totten.core.model.Player;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerGameActivity extends GameActivity {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());
    protected PlayingPlayerType type;
    protected String gameName;
    protected RestGameClient gameClient;
    protected String serverUrl;
    private final LanGameServer lanGameServer = new LanGameServer(8080);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.type = Objects.equals(getIntent().getStringExtra(getString(R.string.type_key)), PlayingPlayerType.ONE.toString())
                ? PlayingPlayerType.ONE : PlayingPlayerType.TWO;
        this.gameName = getIntent().getStringExtra(getString(R.string.game_name_key));
        this.serverUrl = getIntent().getStringExtra(getString(R.string.server_url_key));
        this.gameClient = new RestGameClient(serverUrl, gameName);

        try {
            if (this.serverUrl.contains(getString(R.string.localhost))) {
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
                    throw new ConnectException(this.serverUrl + getString(R.string.server_cannot_start_message));
                }
                final CreateOnlineGameBackgroundTask task =
                        new CreateOnlineGameBackgroundTask(ServerGameActivity.this, this.serverUrl, this.gameName);
                task.execute();
            }

            final Game g = Executors.newSingleThreadExecutor().submit(() -> gameClient.getGame()).get();

            this.gameManager = new OnlineGameManager(g, this.gameName);
            initUI(type);
            updateTextField(type.toString());
            if (!this.gameManager.getPlayingPlayer().getPlayerType().equals(type)) {
                disableClick();
                waitForOtherPlayerToPlay();
            }
        } catch (final IOException | ExecutionException | InterruptedException e) {
            showErrorMessage(e);
        }
    }

    @Override
    protected void cardPlayedLeadingToTheEndOfTheTurn(final PlayingPlayerType updatePointOfView) {
        disableClick();
        passButton.setVisibility(View.INVISIBLE);
        runOnUiThread(() -> {
            updateUI(updatePointOfView);
        });
        endOfTurn();
    }

    private void waitForOtherPlayerToPlay() {
        CompletableFuture.supplyAsync(() -> {
            while(!gameClient.getPlayingPlayer().getPlayerType().equals(type)) {
                try {
                    Thread.sleep(3000);
                } catch (final InterruptedException e) {
                    showErrorMessage(e);
                }
            }
            // get game from server
            gameManager = new OnlineGameManager(gameClient.getGame(), gameName);
            // check victory
            try {
                endOfTheGame(gameManager.getWinner());
            } catch (final NoPlayerException e) {
                // nothing to do, just continue to play
                Toast.makeText(ServerGameActivity.this,
                        getString(R.string.it_is_your_turn), Toast.LENGTH_LONG).show();
            }
            return null;
        }, EXECUTOR).thenAccept(result -> {
            MAIN_HANDLER.post(() -> {
                updateTextField(type.toString());
                updateUI(type);
                enableClick();
            });
        }).exceptionally(throwable -> {
            MAIN_HANDLER.post(() -> {
                showErrorMessage((Exception) throwable);
            });
            return null;
        });
    }
    @Override
    protected void endOfTurn() {
        gameManager.swapPlayers();

        // update game on server
        gameClient.updateGame(((OnlineGameManager)this.gameManager).getGame());

        // wait for other player to play
        waitForOtherPlayerToPlay();

    }

    @Override
    protected void endOfTheGame(final Player winner) {
        super.endOfTheGame(winner);
        gameManager.swapPlayers();
        // update game on server
        gameClient.updateGame(((OnlineGameManager)this.gameManager).getGame());
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
    public AlertDialog.Builder generateBackPressedBuilder() {
        final AlertDialog.Builder builder = new AlertDialog.Builder((new ContextThemeWrapper(this, R.style.CustomAlertDialog)));
        builder.setTitle(getString(R.string.quit_title));

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.yes), (dialog, which) -> {
            dialog.dismiss();
            lanGameServer.closeAllConnections();
            lanGameServer.stop();
            // wait 4 seconds, thus the other player is notified
            final WaitingBackgroundTask task =
                    new WaitingBackgroundTask(ServerGameActivity.this, 3333);
            task.execute();
        });
        builder.setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel());

        return builder;
    }

    @Override
    public void finish() {
        // player 1 delete the game
        try {
            gameManager.getWinner();
            if (type.equals(PlayingPlayerType.ONE)) {
                gameClient.deleteGame();
            }
        } catch (final NoPlayerException e) {
            // nothing to do
        }
        if (lanGameServer.isAlive()) {
            this.lanGameServer.stop();
        }
        super.finish();
    }
}

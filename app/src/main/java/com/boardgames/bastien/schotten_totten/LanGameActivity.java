package com.boardgames.bastien.schotten_totten;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.Player;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public abstract class LanGameActivity extends GameActivity {

    protected String playerName;
    protected String distantIp;
    protected String localIp;
    protected int localPort;
    protected int distantPort;
    protected ProgressDialog waitingDialog;
    protected PlayerType playerType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        waitingDialog = new ProgressDialog(LanGameActivity.this);
        waitingDialog.setCancelable(false);// set to true for dev
        findViewById(R.id.gameLayout).setVisibility(View.INVISIBLE);
    }

//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(OnlineGameActivity.this, LauncherActivity.class));
//    }

    @Override
    protected void updateTextField() throws NoPlayerException {
        final PlayerType playingPlayerType = game.getPlayingPlayer().getPlayerType();
        final String message = playingPlayerType.equals(playerType) ?
                playerName + getString(R.string.it_is_your_turn_message) :
                getString(R.string.not_your_turn_message) ;
        ((TextView) findViewById(R.id.textView)).setText(message);
    }

    protected void updateBoardUI() throws NoPlayerException {
        final PlayerType playingPlayerType = game.getPlayingPlayer().getPlayerType();
        final String playingPlayerOpponentName = playingPlayerType.equals(playerType) ?
                getString(R.string.not_your_turn_message) :
                playerName + getString(R.string.it_is_your_turn_message) ;
        runOnUiThread(new Runnable() {
            public void run() {
                // update playing player text
                ((TextView) findViewById(R.id.textView)).setText(playingPlayerOpponentName);
                // update board
                for (int i = 0; i < game.getGameBoard().getMilestones().size(); i++) {
                    updateMilestoneView(i);
                }
            }
        });
    }

    private class GameSender implements Runnable {

        @Override
        public void run() {
            // Create the socket
            try (final Socket clientSocketToPass = new Socket(distantIp, distantPort)){
                // Create the input & output streams to the server
                final ObjectOutputStream outToServer =
                        new ObjectOutputStream(clientSocketToPass.getOutputStream());
                outToServer.writeObject(game);
                clientSocketToPass.close();
            } catch (final Exception e) {
                superCatch(e);
            }
        }
    }

    protected String getIPAddress() throws UnknownHostException {
        try {
            final List<NetworkInterface> interfaces =
                    Collections.list(NetworkInterface.getNetworkInterfaces());

            // find vpn
            for (final NetworkInterface i : interfaces) {
                if (i.getName().equals("ppp0")) {
                    for (final InetAddress a : Collections.list(i.getInetAddresses())) {
                        if (!a.isLoopbackAddress()
                                && a.getHostAddress().matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                            return a.getHostAddress();
                        }
                    }
                }
            }
            // find wifi
            for (final NetworkInterface i : interfaces) {
                if (i.getName().equals("wlan0")) {
                    for (final InetAddress a : Collections.list(i.getInetAddresses())) {
                        if (!a.isLoopbackAddress()
                                && a.getHostAddress().matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                            return a.getHostAddress();
                        }
                    }
                }
            }

        } catch (final Exception ex) {
            showErrorMessage(ex);
        }
        throw new UnknownHostException();
    }

    protected void superCatch(final Exception e) {
        runOnUiThread(new Runnable() {
            public void run() {
                showErrorMessage(e);
            }
        });
    }

    protected void runTheGame(final ServerSocket serverSocket) {
        boolean isGameFinished = false;
        while (!isGameFinished) {
            try (final Socket clientSocket = serverSocket.accept()) {
                // Create the Client Socket
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(LanGameActivity.this,
                                "your turn to play", Toast.LENGTH_LONG).show();
                    }
                });

                // recieve the game
                game = (Game) new ObjectInputStream(clientSocket.getInputStream()).readObject();
                clientSocket.close();

                // update ui
                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            updateUI();
                        } catch (NoPlayerException e) {
                            superCatch(e);
                        }
                    }
                });
                enableClick();

            } catch (final Exception e) {
                superCatch(e);
            }
            // check victory
            try {
                final Player winner = game.getWinner();
                runOnUiThread(new Runnable() {
                    public void run() {
                        showAlertMessage(getString(R.string.end_of_the_game_title),
                                getString(R.string.end_of_the_game_message) + winner.getName(), true, false);
                    }
                });
                isGameFinished = true;
            } catch (final NoPlayerException e) {
                isGameFinished = false;
            }
        }
    }

    @Override
    protected void endOfTheGame(final Player winner) throws NoPlayerException {
        endOfTurn();
        super.endOfTheGame(winner);
    }

    @Override
    protected void endOfTurn() throws NoPlayerException {
        updateBoardUI();
        // disable click
        disableClick();
        passButton.setVisibility(View.INVISIBLE);
        // swap
        game.swapPlayingPlayerType();
        // send game
        Executors.newSingleThreadExecutor().submit(new GameSender());
    }
}

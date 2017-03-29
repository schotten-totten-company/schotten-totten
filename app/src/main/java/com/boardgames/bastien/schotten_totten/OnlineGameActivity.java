package com.boardgames.bastien.schotten_totten;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.Player;

import java.io.IOException;
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

public abstract class OnlineGameActivity extends GameActivity {

    protected String playerName;
    protected String distantIp;
    protected String localIp;
    protected int localPort;
    protected int distantPort;
    protected ServerSocket gameServer;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OnlineGameActivity.this, LauncherActivity.class));
    }

    protected void updateUI() throws NoPlayerException {
        final String playingPlayerName = game.getPlayingPlayer().getName();
        runOnUiThread(new Runnable() {
            public void run() {
                // update playing player text
                ((TextView) findViewById(R.id.textView)).setText(playingPlayerName);
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
            // pass
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

    protected class GameServer implements Runnable {

        @Override
        public void run() {

            try {
                boolean isGameFinished = false;
                while (isGameFinished) {
                    try (final Socket clientSocket = gameServer.accept()) {
                        // Create the Client Socket
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(OnlineGameActivity.this,
                                        "your turn to play", Toast.LENGTH_LONG).show();
                            }
                        });
                        // Create input and output streams to client
                        final ObjectInputStream inFromClient =
                                new ObjectInputStream(clientSocket.getInputStream());

                        // recieve the game
                        game = (Game) inFromClient.readObject();
                        clientSocket.close();

                        // update ui
                        updateUI();
                        enableClick();

                    } catch (final Exception e) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                showErrorMessage(e);
                            }
                        });
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
                try {
                    gameServer.close();
                    showErrorMessage(e);
                } catch (final IOException ex) {
                    showErrorMessage(ex);
                }

            }
        });
    }

    @Override
    protected void endOfTurn() throws NoPlayerException {
        // send game
        Executors.newSingleThreadExecutor().submit(new GameSender());
        // disable click
        disableClick();
    }
}

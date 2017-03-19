package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.model.TicTacToe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public abstract class OnlineTestGameActivity extends AppCompatActivity {

    protected TicTacToe game;
    protected String distantIp;
    protected String localIp;
    protected String playerName;
    protected int localPort;
    protected int distantPort;
    protected String symbol;

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OnlineTestGameActivity.this, LauncherActivity.class));
    }

    protected void updateBoard() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final String content = game.getGameState().get(10*i+j);
                final TextView button = getImageButton(i, j);
                button.setAlpha((float)1.0);
                switch (content) {
                    case "X":
                        button.setText("[ X ]");
                        button.setOnClickListener(null);
                        break;
                    case "O":
                        button.setText("[ O ]");
                        button.setOnClickListener(null);
                        break;
                    default:
                        button.setText("[  ]");
                        button.setOnClickListener(gridListener);
                        break;
                }
            }
        }
    }

    protected void removeListeners() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                final TextView button = getImageButton(i, j);
                button.setOnClickListener(null);
                button.setAlpha((float)0.5);
            }
        }
    }

    private TextView getImageButton(final int i, final int j) {
        final int id = getResources().getIdentifier("grid" + i + j, "id", getPackageName());
        return (TextView)findViewById(id);
    }

    private View.OnClickListener gridListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int index = Integer.valueOf(
                    getResources().getResourceEntryName(v.getId()).substring(4, 6));

            // play
            game.play(index, symbol);
            updateBoard();

            // disable actions
            removeListeners();
            // switch player
            game.switchPlayer();
            ((TextView)findViewById(R.id.playingPlayerText)).setText(
                    game.getPlayingPlayer() + " turn.");

            Executors.newSingleThreadExecutor().submit(new GameSender());

            // check victory
            if (game.isFinished().equals(symbol)) {
                showAlertMessage(getString(R.string.end_of_the_game_title), "You wins !!!", true);
            }
        }
    };

    protected void showErrorMessage(final Exception e) {
        final StringWriter message = new StringWriter();
        e.printStackTrace(new PrintWriter(message));
        showAlertMessage("Error : " + e.getMessage(), message.toString(), true);
    }

    private void showAlertMessage(final String message) {
        showAlertMessage(getString(R.string.warning_title), message, false);
    }

    private void showAlertMessage(final String title, final String message, final boolean finish) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (finish) {
                            finish();
                        }
                    }
                });
        alertDialog.show();

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
                runOnUiThread(new Runnable() {
                    public void run() {
                        showErrorMessage(e);
                    }
                });
            }
        }
    }

    protected class GameServer implements Runnable {

        private final ServerSocket gameServer;

        public GameServer(final ServerSocket server) {
            gameServer = server;
        }

        @Override
        public void run() {

            try (final Socket clientSocket = gameServer.accept()) {
                while (game.isFinished().isEmpty()) {
                    // Create the Client Socket
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(OnlineTestGameActivity.this,
                                    "client connected", Toast.LENGTH_LONG).show();
                        }
                    });
                    // Create input and output streams to client
                    final ObjectInputStream inFromClient =
                            new ObjectInputStream(clientSocket.getInputStream());
                    game = (TicTacToe) inFromClient.readObject();
                    clientSocket.close();

                    runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView)findViewById(R.id.playingPlayerText)).setText(
                                    game.getPlayingPlayer() + " turn.");
                            updateBoard();
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        // check victory
                        if (game.isFinished().equals(symbol)) {
                            showAlertMessage(getString(R.string.end_of_the_game_title), "You wins !!!", true);
                        } else {
                            showAlertMessage(getString(R.string.end_of_the_game_title), "You loose !!!", true);
                        }
                    }
                });

                gameServer.close();
            } catch (final Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        showErrorMessage(e);
                    }
                });
            } finally {

            }
        }
    }

    protected String getIPAddress(boolean useIPv4) throws UnknownHostException {
        try {
            final List<NetworkInterface> interfaces =
                    Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (final Exception ex) {
            showErrorMessage(ex);
        }
        throw new UnknownHostException();
    }
}

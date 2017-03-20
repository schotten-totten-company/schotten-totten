package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.model.TicTacToe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class CreateOnlineTestGameActivity extends OnlineTestGameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playerName = "P1";
        localPort = 8011;
        distantPort = 8022;
        symbol = "X";

        try {
            localIp = getIPAddress(true);
            // set layout
            setContentView(R.layout.activity_online_test);
            ((TextView)findViewById(R.id.playingPlayerText)).setText(localIp + " is waiting ...");
            // init game (wait for client)
            Executors.newSingleThreadExecutor().submit(new GameInitServer());

        } catch (final Exception e) {
            showErrorMessage(e);
        }

    }


    public class GameInitServer implements Runnable {

        @Override
        public void run() {

            try {
                gameServer = new ServerSocket(localPort);
                // Create the Client Socket
                try (final Socket clientSocket = gameServer.accept()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(CreateOnlineTestGameActivity.this,
                                    "client connected", Toast.LENGTH_LONG).show();
                        }
                    });
                    // Create input and output streams to client
                    final ObjectInputStream inFromClient =
                            new ObjectInputStream(clientSocket.getInputStream());
                    final ObjectOutputStream outToClient =
                            new ObjectOutputStream(clientSocket.getOutputStream());
                    final String ipAndName = (String) inFromClient.readObject();
                    distantIp = ipAndName.split("@")[1];
                    game = new TicTacToe(playerName, ipAndName.split("@")[0]);
                    outToClient.writeObject(game);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            ((TextView) findViewById(R.id.playingPlayerText)).setText(
                                    game.getPlayingPlayer() + " turn.");
                            updateBoard();
                        }
                    });
                    // launch game server
                    Executors.newSingleThreadExecutor().submit(new GameServer());
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            showErrorMessage(e);
                        }
                    });
                }
            } catch (final Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        showErrorMessage(e);
                    }
                });
            }
        }
    }
}

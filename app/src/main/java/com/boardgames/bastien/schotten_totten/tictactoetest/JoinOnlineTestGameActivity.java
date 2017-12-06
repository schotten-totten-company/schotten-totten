package com.boardgames.bastien.schotten_totten.tictactoetest;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.R;
import com.boradgames.bastien.schotten_totten.core.model.TicTacToe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class JoinOnlineTestGameActivity extends OnlineTestGameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playerName = "P2";
        localPort = 8022;
        distantPort = 8011;
        symbol = "O";

        try {

            localIp = getIPAddress(true);
            distantIp = getIntent().getStringExtra("distantIp");
            Executors.newSingleThreadExecutor().submit(new GameInitClient());
            setContentView(R.layout.activity_online_test);
            ((TextView)findViewById(R.id.playingPlayerText)).setText("try to connect...");

        } catch (final Exception e) {
            showErrorMessage(e);
        }

    }

    public class GameInitClient implements Runnable {

        public GameInitClient() throws IOException {
            gameServer = new ServerSocket(localPort);
        }

        @Override
        public void run() {

            // Create the Client Socket
            try (final Socket clientSocketToConnect = new Socket(distantIp, distantPort)) {

                // Create the input & output streams to the server
                final ObjectOutputStream outToServer = new ObjectOutputStream(clientSocketToConnect.getOutputStream());
                final ObjectInputStream inFromServer = new ObjectInputStream(clientSocketToConnect.getInputStream());
                outToServer.writeObject(playerName + "@" + localIp);
                game = (TicTacToe)inFromServer.readObject();
                clientSocketToConnect.close();

                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(JoinOnlineTestGameActivity.this,
                                "connected to server", Toast.LENGTH_LONG).show();
                        ((TextView)findViewById(R.id.playingPlayerText)).setText(
                                game.getPlayingPlayer() + " turn.");
                        updateBoard();
                        removeListeners();
                    }
                });

                Executors.newSingleThreadExecutor().submit(new GameServer());

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

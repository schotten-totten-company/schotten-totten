package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.model.Game;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

public class CreateOnlineGameActivity extends OnlineGameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playerName = "P1";
        localPort = 8011;
        distantPort = 8022;

        try {
            localIp = getIPAddress();
            // set layout
            setContentView(R.layout.activity_hot_seat_game);
            ((TextView)findViewById(R.id.textView)).setText(localIp + " is waiting ...");
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
                            Toast.makeText(CreateOnlineGameActivity.this,
                                    "other player online", Toast.LENGTH_LONG).show();
                        }
                    });
                    // Create input and output streams to client
                    final ObjectInputStream inFromClient =
                            new ObjectInputStream(clientSocket.getInputStream());
                    final ObjectOutputStream outToClient =
                            new ObjectOutputStream(clientSocket.getOutputStream());
                    final String ipAndName = (String) inFromClient.readObject();
                    distantIp = ipAndName.split("@")[1];

                    // create game
                    game = new Game(playerName, ipAndName.split("@")[0]);
                    initUI(CreateOnlineGameActivity.this);
                    outToClient.writeObject(game);

                    updateUI();

                    // launch game server
                    Executors.newSingleThreadExecutor().submit(new GameServer());
                } catch (final Exception e) {
                   superCatch(e);
                }
            } catch (final Exception e) {
                superCatch(e);
            }
        }
    }


}

package com.boardgames.bastien.schotten_totten;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.Hand;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

public class JoinOnlineGameActivity extends OnlineGameActivity {

    protected boolean alreadyLaunched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!alreadyLaunched) {
            playerName = "P2";
            localPort = 8022;
            distantPort = 8011;

            try {

                localIp = getIPAddress();
                distantIp = getIntent().getStringExtra("distantIp");
                Executors.newSingleThreadExecutor().submit(new GameInitClient());
                setContentView(R.layout.activity_hot_seat_game);
                ((TextView)findViewById(R.id.textView)).setText("try to connect...");

            } catch (final UnknownHostException e) {
                showAlertMessage(getString(R.string.unknown_host_title),
                        getString(R.string.unknown_host_message), true, true);
            } catch (final Exception e) {
                showErrorMessage(e);
            }
            //
            alreadyLaunched = true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alreadyLaunched = false;
    }

    @Override
    public void onBackPressed() {
        final Intent backFromJoin = new Intent(JoinOnlineGameActivity.this, LauncherActivity.class);
        backFromJoin.putExtra("joinLaunched", alreadyLaunched);
        startActivity(backFromJoin);
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
                game = (Game)inFromServer.readObject();
                clientSocketToConnect.close();

                final Hand handToUpdate = game.getPlayer(PlayerType.TWO).getHand();
                runOnUiThread(new Runnable() {
                    public void run() {
                        initUI(handToUpdate);
                        Toast.makeText(JoinOnlineGameActivity.this,
                                "connected to server", Toast.LENGTH_LONG).show();
                    }
                });

                disableClick();

                Executors.newSingleThreadExecutor().submit(new GameServer());

            } catch (final Exception e) {
                superCatch(e);
            }
        }
    }


}

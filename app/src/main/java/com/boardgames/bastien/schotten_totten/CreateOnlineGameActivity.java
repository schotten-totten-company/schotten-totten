package com.boardgames.bastien.schotten_totten;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.Hand;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

public class CreateOnlineGameActivity extends OnlineGameActivity {

    protected static boolean alreadyLaunched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!alreadyLaunched) {
            playerName = "P1";
            playerType = PlayerType.ONE;
            localPort = 8011;
            distantPort = 8022;

            try {
                localIp = getIPAddress();
                waitingDialog.setTitle(localIp + " is waiting ...");
                waitingDialog.show();
                // init game (wait for client)
                Executors.newSingleThreadExecutor().submit(new GameInitServer());

            } catch (final UnknownHostException e) {
                showAlertMessage(getString(R.string.unknown_host_title),
                        getString(R.string.unknown_host_message), true, true);
            } catch (final Exception e) {
                showErrorMessage(e);
            }

            alreadyLaunched = true;
        }

    }

    public class GameInitServer implements Runnable {

        @Override
        public void run() {

            try (final ServerSocket serverSocket = new ServerSocket(localPort)) {
                // Create the Client Socket
                try (final Socket initClientSocket = serverSocket.accept()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            waitingDialog.dismiss();
                            findViewById(R.id.gameLayout).setVisibility(View.VISIBLE);
                            Toast.makeText(CreateOnlineGameActivity.this,
                                    "other player online", Toast.LENGTH_LONG).show();
                        }
                    });
                    // Create input and output streams to client
                    final ObjectInputStream inFromClient =
                            new ObjectInputStream(initClientSocket.getInputStream());
                    final ObjectOutputStream outToClient =
                            new ObjectOutputStream(initClientSocket.getOutputStream());
                    final String ipAndName = (String) inFromClient.readObject();
                    distantIp = ipAndName.split("@")[1];

                    // create game
                    game = new Game(playerName, ipAndName.split("@")[0]);
                    outToClient.writeObject(game);

                    final Hand handToUpdate = game.getPlayer(PlayerType.ONE).getHand();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            initUI(handToUpdate);
                        }
                    });

                    runTheGame(serverSocket);

                } catch (final Exception e) {
                    superCatch(e);
                }
            } catch (final Exception e) {
                superCatch(e);
            }
        }
    }


}

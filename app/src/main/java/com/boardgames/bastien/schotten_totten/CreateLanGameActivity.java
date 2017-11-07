package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.controllers.SimpleGameManager;
import com.boardgames.bastien.schotten_totten.model.Hand;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

public class CreateLanGameActivity extends LanGameActivity {

    protected static boolean alreadyLaunched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!alreadyLaunched) {
            playerName = getString(R.string.player1name);
            playerType = PlayerType.ONE;
            localPort = 8011;
            distantPort = 8022;

            try {
                localIp = getIPAddress();
                waitingDialog.setTitle(localIp);
                waitingDialog.setMessage(getString(R.string.please_wait));
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
                            gameLayout.setVisibility(View.VISIBLE);
                            Toast.makeText(CreateLanGameActivity.this,
                                    getString(R.string.connection_ok), Toast.LENGTH_LONG).show();
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
                    gameManager = new SimpleGameManager(playerName, ipAndName.split("@")[0]);
                    outToClient.writeObject(gameManager.getGame());

                    final Hand handToUpdate = gameManager.getGame().getPlayer(PlayerType.ONE).getHand();
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
            } finally {
                alreadyLaunched = false;
            }
        }
    }


}

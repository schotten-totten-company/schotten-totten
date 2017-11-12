package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.controllers.AbstractGameManager;
import com.boardgames.bastien.schotten_totten.model.Hand;
import com.boardgames.bastien.schotten_totten.model.MilestonePlayerType;
import com.boardgames.bastien.schotten_totten.model.PlayingPlayerType;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

public class JoinLanGameActivity extends LanGameActivity {

    protected static boolean joinAlreadyLaunched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!joinAlreadyLaunched) {
            playerName = getString(R.string.player2name);
            playingPlayerType = PlayingPlayerType.TWO;
            localPort = 8022;
            distantPort = 8011;

            try {

                localIp = getIPAddress();
                distantIp = getIntent().getStringExtra("distantIp");
                waitingDialog.setTitle(localIp);
                waitingDialog.setMessage(getString(R.string.please_wait));
                waitingDialog.show();
                Executors.newSingleThreadExecutor().submit(new GameInitClient());
                textView.setText(getString(R.string.please_wait));

            } catch (final UnknownHostException e) {
                showAlertMessage(getString(R.string.unknown_host_title),
                        getString(R.string.unknown_host_message), true, true);
            } catch (final Exception e) {
                showErrorMessage(e);
            }
            //
            joinAlreadyLaunched = true;
        }
    }

    public class GameInitClient implements Runnable {

        @Override
        public void run() {

            // Create the Client Socket
            try (final Socket clientSocketToConnect = new Socket(distantIp, distantPort)) {

                // Create the input & output streams to the server
                final ObjectOutputStream outToServer = new ObjectOutputStream(clientSocketToConnect.getOutputStream());
                final ObjectInputStream inFromServer = new ObjectInputStream(clientSocketToConnect.getInputStream());
                outToServer.writeObject(playerName + "@" + localIp);
                gameManager = (AbstractGameManager)inFromServer.readObject();
                clientSocketToConnect.close();


                final Hand handToUpdate = gameManager.getPlayer(PlayingPlayerType.TWO).getHand();
                runOnUiThread(new Runnable() {
                    public void run() {
                        waitingDialog.dismiss();
                        gameLayout.setVisibility(View.VISIBLE);
                        initUI(handToUpdate);
                        Toast.makeText(JoinLanGameActivity.this,
                                getString(R.string.connection_ok), Toast.LENGTH_LONG).show();
                    }
                });

                disableClick();

                try (final ServerSocket serverSocket = new ServerSocket(localPort)) {
                    runTheGame(serverSocket);
                }

            } catch (final Exception e) {
                superCatch(e);
            } finally {
                joinAlreadyLaunched = false;
            }
        }
    }


}

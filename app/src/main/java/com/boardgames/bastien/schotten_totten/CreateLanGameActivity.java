package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.server.LanGameManager;
import com.boardgames.bastien.schotten_totten.server.RestGameClient;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.net.UnknownHostException;

public class CreateLanGameActivity extends LanGameActivity {

    protected static boolean alreadyLaunched = false;
    private final LanGameManager lanGameManager = new LanGameManager();
    protected String localIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!alreadyLaunched) {
            playerType = PlayingPlayerType.ONE;
            lanGameManager.start();
            gameManager = new RestGameClient("http://localhost:8080", "");

            try {
                localIp = getIPAddress();
                playerName = getString(R.string.player1name) + "-" + localIp;
                playerType = PlayingPlayerType.ONE;

                // start server
                lanGameManager.start();

                waitingDialog.setTitle(localIp);
                waitingDialog.setMessage(getString(R.string.please_wait));
                waitingDialog.show();

                // wait for server started
                // TODO
                while (!lanGameManager.isActive()) {
                    Thread.sleep(3333);
                }
                waitingDialog.dismiss();
                gameLayout.setVisibility(View.VISIBLE);
                Toast.makeText(CreateLanGameActivity.this,
                        getString(R.string.connection_ok), Toast.LENGTH_LONG).show();

                // create game
                ((RestGameClient)gameManager).createGame();

                runOnUiThread(new Runnable() {
                    public void run() {
                        initUI(PlayingPlayerType.ONE);
                    }
                });

            } catch (final UnknownHostException e) {
                showAlertMessage(getString(R.string.unknown_host_title),
                        getString(R.string.unknown_host_message), true, true);
            } catch (final Exception e) {
                showErrorMessage(e);
            }

            alreadyLaunched = true;
        }

    }

}

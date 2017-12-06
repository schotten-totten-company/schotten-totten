package com.boardgames.bastien.schotten_totten;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.boradgames.bastien.schotten_totten.core.model.Player;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public abstract class LanGameActivity extends GameActivity {

    protected String playerName;
    protected ProgressDialog waitingDialog;
    protected PlayingPlayerType playerType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        waitingDialog = new ProgressDialog(LanGameActivity.this);
        waitingDialog.setCancelable(true);
        waitingDialog.setCanceledOnTouchOutside(false);
        waitingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        findViewById(R.id.gameLayout).setVisibility(View.INVISIBLE);
    }

    // un comment for debug
//    @Override
//    public void onBackPressed() {
//        startActivity(new Intent(OnlineGameActivity.this, LauncherActivity.class));
//    }

    @Override
    protected void updateTextField(final PlayingPlayerType updatePointOfView) {
        final PlayingPlayerType playingPlayerType = gameManager.getPlayingPlayer().getPlayerType();
        final String message = playingPlayerType.equals(updatePointOfView) ?
                playerName + getString(R.string.it_is_your_turn_message) :
                getString(R.string.not_your_turn_message) ;
        ((TextView) findViewById(R.id.textView)).setText(message);
    }

    protected void updateBoardUI() {
        final PlayingPlayerType playingPlayerType = gameManager.getPlayingPlayer().getPlayerType();
        final String playingPlayerOpponentName = playingPlayerType.equals(playingPlayerType) ?
                getString(R.string.not_your_turn_message) :
                playerName + getString(R.string.it_is_your_turn_message) ;
        runOnUiThread(new Runnable() {
            public void run() {
                // update playing player text
                updateTextField(playingPlayerType);
                // update board
                for (int i = 0; i < gameManager.getMilestones().size(); i++) {
                    updateMilestoneView(i, playingPlayerType);
                }
            }
        });
    }

//    private class GameSender implements Runnable {
//
//        @Override
//        public void run() {
//            // Create the socket
//            try (final Socket clientSocketToPass = new Socket(distantIp, distantPort)){
//                // Create the input & output streams to the server
//                final ObjectOutputStream outToServer =
//                        new ObjectOutputStream(clientSocketToPass.getOutputStream());
//                outToServer.writeObject(gameManager);
//                clientSocketToPass.close();
//            } catch (final Exception e) {
//                superCatch(e);
//            }
//        }
//    }

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
                showErrorMessage(e);
            }
        });
    }

    @Override
    protected void endOfTheGame(final Player winner) {
        endOfTurn();
        super.endOfTheGame(winner);
    }

    @Override
    protected void endOfTurn() {
        updateBoardUI();
        // disable click
        disableClick();
        passButton.setVisibility(View.INVISIBLE);

        // wait for your turn
        while (gameManager.getPlayingPlayer().getPlayerType() != playerType) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                superCatch(e);
            }
        }

        // show that it is your turn
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(LanGameActivity.this,
                        getString(R.string.it_is_your_turn), Toast.LENGTH_LONG).show();
            }
        });

        // update ui
        runOnUiThread(new Runnable() {
            public void run() {
                updateUI(gameManager.getPlayingPlayer().getPlayerType());
            }
        });

        // enable click
        enableClick();
    }
}

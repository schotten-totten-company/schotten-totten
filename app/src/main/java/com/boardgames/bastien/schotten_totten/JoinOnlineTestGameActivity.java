package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.model.TicTacToe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

public class JoinOnlineTestGameActivity extends OnlineTestGameActivity {

    private String myIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        playerName = "P2";
        localPort = 8022;
        distantPort = 8011;
        symbol = "O";
        myIp = "192.168.1.42";//getIPAddress(true);
        otherPlayerIp = "192.168.1.3";

        try {

            Executors.newSingleThreadExecutor().submit(new GameInitClient());
            setContentView(R.layout.activity_online_test);
            ((TextView)findViewById(R.id.playingPlayerText)).setText("try to connect...");

        } catch (final Exception e) {
            showErrorMessage(e);
        }

    }


    public class GameInitClient implements Callable<TicTacToe> {

        private final ServerSocket server;

        public GameInitClient() throws IOException {
            server = new ServerSocket(localPort);
        }

        @Override
        public TicTacToe call() throws Exception {

            // Create the Client Socket
            try (final Socket clientSocketToConnect = new Socket(otherPlayerIp, distantPort)) {
                // Create the input & output streams to the server
                final ObjectOutputStream outToServer = new ObjectOutputStream(clientSocketToConnect.getOutputStream());
                final ObjectInputStream inFromServer = new ObjectInputStream(clientSocketToConnect.getInputStream());
                outToServer.writeObject(playerName + "@" + myIp);
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

                Executors.newSingleThreadExecutor().submit(new GameServer(server));
            } catch (final Exception e) {
                throw e;
            }
            return game;
        }
    }

    public static String getIPAddress(boolean useIPv4) {
        try {
            final List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
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
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
}

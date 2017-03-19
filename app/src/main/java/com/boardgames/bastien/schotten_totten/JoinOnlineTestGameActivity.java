package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.model.TicTacToe;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
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
            Executors.newSingleThreadExecutor().submit(new GameInitClient());
            setContentView(R.layout.activity_online_test);
            ((TextView)findViewById(R.id.playingPlayerText)).setText("try to connect...");

        } catch (final Exception e) {
            showErrorMessage(e);
        }

    }


    public class GameInitClient implements Runnable {

        private final ServerSocket server;

        public GameInitClient() throws IOException {
            server = new ServerSocket(localPort);
        }

        @Override
        public void run() {

            // Create the Client Socket
            try {
                final String prefix = localIp.substring(0, localIp.lastIndexOf(".")+1);
                for (int i = 1; i <255; i++) {
                    try (final Socket clientSocketToConnect = new Socket()) {
                        distantIp = prefix + i;
                        clientSocketToConnect.connect(new InetSocketAddress(distantIp, distantPort), 100);

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

                        Executors.newSingleThreadExecutor().submit(new GameServer(server));

                        break;
                    } catch (final SocketTimeoutException | SocketException e) {
                        System.out.println(distantIp + " : KO");
                    }
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

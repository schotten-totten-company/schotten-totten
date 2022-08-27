package com.boardgames.bastien.schotten_totten;

import android.content.DialogInterface;
import android.content.Intent;

import com.boardgames.bastien.schotten_totten.server.RestGameClient;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ScanForLanServerBackgroundTask extends AbstractBackgroundTask {

    public ScanForLanServerBackgroundTask(LauncherActivity activity) {
        super(activity);
    }

    @Override
    protected void onPreExecute() {
        waitingDialog.setCanceledOnTouchOutside(false);
        waitingDialog.setCancelable(true);
        waitingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                while (!ScanForLanServerBackgroundTask.this.isCancelled()) {
                    ScanForLanServerBackgroundTask.this.cancel(true);
                }
            }
        });
        waitingDialog.show();
    }

    @Override
    protected void onPostExecute(String serverIp) {
        ScanForLanServerBackgroundTask.this.cancel(true);
        // start game
        if (!serverIp.isEmpty()) {
            final Intent joinIntent = new Intent(activity, ServerGameActivity.class);
            joinIntent.putExtra(activity.getString(R.string.server_url_key), activity.getString(R.string.http_prefix) + serverIp + activity.getString(R.string.localhost_port));
            joinIntent.putExtra(activity.getString(R.string.game_name_key), activity.getString(R.string.lan_game));
            joinIntent.putExtra(activity.getString(R.string.type_key), PlayingPlayerType.TWO.toString());
            activity.startActivity(joinIntent);
        } else {
            // no server found
            ((LauncherActivity)activity).showError(activity.getString(R.string.no_local_server_title), activity.getString(R.string.no_local_server_message));
        }
        if (waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        // get ip
        try {
            final String myIp = getIPAddress();
            final String mySubLan = myIp.substring(0, myIp.lastIndexOf('.') + 1);

            final ExecutorService executorService = Executors.newFixedThreadPool(16);
            final List<Callable<String>> scanCallableList = new ArrayList<>();

            // create scan list
            for (int i = 0; i < 256; i++) {
                scanCallableList.add(new ScanIpCallable(i, mySubLan,
                        activity.getString(R.string.http_prefix) + mySubLan + i + activity.getString(R.string.localhost_port),
                        activity.getString(R.string.lan_game), activity.getString(R.string.SCHOTTEN)));
            }

            if (ScanForLanServerBackgroundTask.this.isCancelled()) {
                return "";
            }

            try {
                // invoke threads and get result
                for (final Future<String> result : executorService.invokeAll(scanCallableList, 60, TimeUnit.SECONDS)) {
                    if (ScanForLanServerBackgroundTask.this.isCancelled()) {
                        return "";
                    }
                    if (!result.get().isEmpty()) {
                        return result.get();
                    }
                }
            } catch (final InterruptedException e) {
                // interrupted by user, return nothing
                if (ScanForLanServerBackgroundTask.this.isCancelled()) {
                    return "";
                }
            }

        } catch (final UnknownHostException | SocketException | ExecutionException e) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((LauncherActivity)activity).showError(e);
                }
            });
        }

        return "";
    }

    private String getIPAddress() throws UnknownHostException, SocketException {
        final List<NetworkInterface> interfaces =
                Collections.list(NetworkInterface.getNetworkInterfaces());

        // find vpn
        for (final NetworkInterface i : interfaces) {
            if (i.getName().equals(activity.getString(R.string.vpn_interface_name))) {
                for (final InetAddress a : Collections.list(i.getInetAddresses())) {
                    if (!a.isLoopbackAddress() && a.getHostAddress() != null
                            && a.getHostAddress().matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                        return a.getHostAddress();
                    }
                }
            }
        }
        // find wifi
        for (final NetworkInterface i : interfaces) {
            if (i.getName().equals(activity.getString(R.string.wlan_interface_name))) {
                for (final InetAddress a : Collections.list(i.getInetAddresses())) {
                    if (!a.isLoopbackAddress() && a.getHostAddress() != null
                            && a.getHostAddress().matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
                        return a.getHostAddress();
                    }
                }
            }
        }
        throw new UnknownHostException();
    }

}

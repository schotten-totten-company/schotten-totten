package com.boardgames.bastien.schotten_totten;

import com.boardgames.bastien.schotten_totten.server.RestGameClient;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class ScanIpCallable implements Callable<String> {

    private final int index;
    private final String mySubLan;
    private final String url;
    private final String gamename;
    private final String matchingContent;

    public ScanIpCallable(final int index, final String mySubLan, final String url, final String gamename, final String matchingContent) {
        this.index = index;
        this.mySubLan = mySubLan;
        this.url = url;
        this.gamename = gamename;
        this.matchingContent = matchingContent;
    }

    @Override
    public String call() throws Exception {
        final String ipToScan = mySubLan + index;
        final RestGameClient restGameClient =
                new RestGameClient(url, gamename);
        try {
            final String pingResult = restGameClient.ping();
            if (pingResult.contains(matchingContent)) {
                return ipToScan;
            } else {
                return "";
            }
        } catch (ExecutionException | InterruptedException e) {
            // timeout, not the right host
            return "";
        }
    }
}

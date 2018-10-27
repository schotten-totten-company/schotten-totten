package com.boardgames.bastien.schotten_totten.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Bastien on 19/10/2017.
 */

public class LanGameServer extends NanoHTTPD {

    public LanGameServer(int port) {
        super(port);
    }

    public LanGameServer(String hostname, int port) {
        super(hostname, port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Map<String, String> files = new HashMap<String, String>();
        Method method = session.getMethod();
        if (Method.PUT.equals(method) || Method.POST.equals(method)) {
            try {
                session.parseBody(files);
            } catch (IOException ioe) {
                return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, "SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
            } catch (ResponseException re) {
                return newFixedLengthResponse(re.getStatus(), NanoHTTPD.MIME_PLAINTEXT, re.getMessage());
            }
        }

        return NanoHTTPD.newFixedLengthResponse(Response.Status.ACCEPTED, NanoHTTPD.MIME_PLAINTEXT, "OK");
    }

}

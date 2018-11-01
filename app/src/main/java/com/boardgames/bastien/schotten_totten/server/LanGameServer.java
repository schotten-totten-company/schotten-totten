package com.boardgames.bastien.schotten_totten.server;

import com.boradgames.bastien.schotten_totten.core.exceptions.GameCreationException;
import com.boradgames.bastien.schotten_totten.core.model.Game;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by Bastien on 19/10/2017.
 */

public class LanGameServer extends NanoHTTPD {

    private final Map<String, Game> gameMap = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LanGameServer(int port) {
        super(port);
    }

    private Response serializeObjectToResponse(final Object o) {
        try {
            final String jsonObject = objectMapper.writeValueAsString(o);
            return NanoHTTPD.newFixedLengthResponse(Response.Status.OK, "application/json", jsonObject);
        } catch (final JsonProcessingException e) {
            return NanoHTTPD.newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
        }
    }

    @Override
    public Response serve(IHTTPSession session) {

        final Method method = session.getMethod();
        switch (method) {
            case GET:
                switch (session.getUri()) {
                    case "/ping":
                        final String message = new Date().toString() + " - it is time to SCHOTTEN !!!!";
                        return serializeObjectToResponse(message);
                    case "/createGame":
                        final String gamename = session.getParameters().get("gamename").get(0);
                        if (gameMap.containsKey(gamename)) {
                            return serializeObjectToResponse(Boolean.FALSE);
                        } else {
                            try {
                                final Game game = new Game("Player 1", "Player 2");
                                gameMap.put(gamename, game);
                                return serializeObjectToResponse(Boolean.TRUE);
                            } catch (GameCreationException e) {
                                return serializeObjectToResponse(Boolean.FALSE);
                            }
                        }
                    case "/getGame":
                        return serializeObjectToResponse(gameMap.get(session.getParameters().get("gamename").get(0)));
                    case "/listGames":
                        return serializeObjectToResponse(new ArrayList<>(gameMap.keySet()));
                    case "/deleteGame":
                        return serializeObjectToResponse(gameMap.remove(session.getParameters().get("gamename").get(0)) != null);
                    case "/getPlayingPlayer":
                        return serializeObjectToResponse(gameMap.get(session.getParameters().get("gamename").get(0)).getPlayingPlayer());
                    default:
                        return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, NanoHTTPD.MIME_PLAINTEXT, session.getUri() + " not supported.");
                }
            case POST:
                switch (session.getUri()) {
                    case "/updateGame":
                        try {
                            final String gamename = session.getParameters().get("gamename").get(0);
                            final HashMap<String, String> map = new HashMap<>();
                            session.parseBody(map);
                            final String jsonGame = map.get("postData");
                            final Game game = objectMapper.readValue(jsonGame, Game.class);
                            if (!gameMap.containsKey(gamename)) {
                                return serializeObjectToResponse(Boolean.FALSE);
                            } else {
                                gameMap.put(gamename, game);
                                return serializeObjectToResponse(Boolean.TRUE);
                            }
                        } catch (IOException | ResponseException e) {
                            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, NanoHTTPD.MIME_PLAINTEXT, e.getMessage());
                        }
                    default:
                        return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, NanoHTTPD.MIME_PLAINTEXT, session.getUri() + " not supported.");
                }
            default:
                return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, NanoHTTPD.MIME_PLAINTEXT, method + " not supported.");
        }

    }

}

package com.boardgames.bastien.schotten_totten.server;


import com.boradgames.bastien.schotten_totten.core.model.Game;
import com.boradgames.bastien.schotten_totten.core.model.Player;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

/**
 * Created by Bastien on 19/11/2017.
 */

public class RestGameClient {

    private final String url;
    private final String guid;
    private final RestTemplate restTemplate;

    public RestGameClient(final String url, final String guid) {
        this.url = url;
        this.guid = guid;
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    public String ping() throws InterruptedException, ExecutionException {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<String>() {
                @Override
                public String call() {
                    final RestTemplate restTemplateForPing = new RestTemplate();
                    restTemplateForPing.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    if (restTemplateForPing.getRequestFactory() instanceof SimpleClientHttpRequestFactory) {
                        ((SimpleClientHttpRequestFactory) restTemplateForPing.getRequestFactory()).setConnectTimeout(1000);
                        ((SimpleClientHttpRequestFactory) restTemplateForPing.getRequestFactory()).setReadTimeout(5000);
                    } else if (restTemplateForPing.getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory) {
                        ((HttpComponentsClientHttpRequestFactory) restTemplateForPing.getRequestFactory()).setReadTimeout(5000);
                        ((HttpComponentsClientHttpRequestFactory) restTemplateForPing.getRequestFactory()).setConnectTimeout(1000);
                    }
                    return restTemplateForPing.getForObject(url + "/ping", String.class);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean createGame() {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return restTemplate.getForObject(url + "/createGame?"
                            + "gamename=" + guid, Boolean.class);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<String> listGames() {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<List<String>>() {
                @Override
                public List<String> call() {
                    final ResponseEntity<String[]> list =
                            restTemplate.getForEntity(url + "/listGames", String[].class);
                    return Arrays.asList(list.getBody());
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean deleteGame() {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return restTemplate.getForObject(url + "/deleteGame?"
                            + "gamename=" + guid, Boolean.class);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean updateGame(final Game game) {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Boolean>() {
                @Override
                public Boolean call() {
                    return restTemplate.postForObject(url + "/updateGame?"
                            + "gamename=" + guid, game, Boolean.class);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Game getGame() {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Game>() {
                @Override
                public Game call() {
                    return restTemplate.getForObject(url + "/getGame?"
                            + "gamename=" + guid, Game.class);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Player getPlayingPlayer() {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Player>() {
                @Override
                public Player call() {
                    return restTemplate.getForObject(url + "/getPlayingPlayer?"
                            + "gamename=" + guid, Player.class);
                }
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}

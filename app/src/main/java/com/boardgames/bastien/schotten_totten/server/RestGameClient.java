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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
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
        final SimpleClientHttpRequestFactory clientHttpRequestFactory = new SimpleClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(30000);
        clientHttpRequestFactory.setReadTimeout(10000);
        restTemplate = new RestTemplate(clientHttpRequestFactory);
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }

    public String ping() throws ExecutionException, InterruptedException {
        try (final ExecutorService executor = Executors.newSingleThreadExecutor()) {
            return executor.submit(() -> {
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
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw e;
        }
    }

    public boolean createGame() {
        try (final ExecutorService executor = Executors.newSingleThreadExecutor()) {
            return executor.submit(() -> restTemplate.getForObject(url + "/createGame?"
                    + "gamename=" + guid, Boolean.class)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> listGames() {
        try (final ExecutorService executor = Executors.newSingleThreadExecutor()) {
            return executor.submit(() -> {
                final ResponseEntity<String[]> list =
                        restTemplate.getForEntity(url + "/listGames", String[].class);
                return Arrays.asList(list.getBody());
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean deleteGame() {
        try (final ExecutorService executor = Executors.newSingleThreadExecutor()) {
            return executor.submit(() -> restTemplate.getForObject(url + "/deleteGame?"
                    + "gamename=" + guid, Boolean.class)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean updateGame(final Game game) {
        try (final ExecutorService executor = Executors.newSingleThreadExecutor()) {
            return executor.submit(() -> restTemplate.postForObject(url + "/updateGame?"
                    + "gamename=" + guid, game, Boolean.class)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Game getGame() {
        try (final ExecutorService executor = Executors.newSingleThreadExecutor()) {
            return executor.submit(() -> restTemplate.getForObject(url + "/getGame?"
                    + "gamename=" + guid, Game.class)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Player getPlayingPlayer() {
        try (final ExecutorService executor = Executors.newSingleThreadExecutor()) {
            return executor.submit(() -> restTemplate.getForObject(url + "/getPlayingPlayer?"
                    + "gamename=" + guid, Player.class)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}

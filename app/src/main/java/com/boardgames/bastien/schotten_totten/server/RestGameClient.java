package com.boardgames.bastien.schotten_totten.server;


import com.boradgames.bastien.schotten_totten.core.controllers.AbstractGameManager;
import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NoPlayerException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NotYourTurnException;
import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.Player;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

public class RestGameClient extends AbstractGameManager {

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
                public String call() throws Exception {
                  return restTemplate.getForObject(url + "/ping", String.class);
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw e;
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean createGame() {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return restTemplate.getForObject(url + "/createGame?"
                            + "gamename=" + guid, Boolean.class).booleanValue();
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean deleteGame() {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return restTemplate.getForObject(url + "/deleteGame?"
                            + "gamename=" + guid, Boolean.class).booleanValue();
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Card getLastPlayedCard() {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Card>() {
                @Override
                public Card call() throws Exception {
                    return restTemplate.getForObject(url + "/getLastPlayedCard?"
                            + "gamename=" + guid, Card.class);
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean reclaimMilestone(
            final PlayingPlayerType p, final int milestoneIndex) throws NotYourTurnException {

        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    final ResponseEntity<Boolean> b = restTemplate.getForEntity(url + "/reclaimMilestone?"
                            + "gamename=" + guid
                            + "&p=" + p.toString()
                            + "&milestoneIndex=" + milestoneIndex, Boolean.class);

                    if (b.getStatusCode() == HttpStatus.OK) {
                        return b.getBody();
                    } else {
                        throw new NotYourTurnException(p);
                    }
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (ExecutionException e) {
            if (e.getCause() instanceof NotYourTurnException) {
                throw (NotYourTurnException) e.getCause();
            } else {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public Player getPlayer(final PlayingPlayerType p) {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Player>() {
                @Override
                public Player call() throws Exception {
                    return restTemplate.getForObject(
                            url + "/getPlayer?"
                                    + "gamename=" + guid
                                    + "&p=" + p.toString(), Player.class);
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean swapPlayers() {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return restTemplate.getForObject(url + "/swapPlayers?"
                            + "gamename=" + guid
                            + "&p=", Boolean.class);
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public Player getPlayingPlayer() {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Player>() {
                @Override
                public Player call() throws Exception {
                    return restTemplate.getForObject(url + "/getPlayingPlayer?"
                            + "gamename=" + guid, Player.class);
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Player getWinner() throws NoPlayerException {

        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Player>() {
                @Override
                public Player call() throws Exception {
                    final ResponseEntity<Player> winner =
                            restTemplate.getForEntity(url + "/getWinner?"
                            + "gamename=" + guid, Player.class);
                    if (winner.getStatusCode() == HttpStatus.OK) {
                        return winner.getBody();
                    } else {
                        throw new NoPlayerException(winner.getStatusCode().getReasonPhrase());
                    }
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof NoPlayerException) {
                throw (NoPlayerException) e.getCause();
            } else {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public List<Milestone> getMilestones() {
        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<List<Milestone>>() {
                @Override
                public List<Milestone> call() throws Exception {
                    final ResponseEntity<Milestone[]> list =
                            restTemplate.getForEntity(url + "/getMilestones?"
                                    + "gamename=" + guid, Milestone[].class);
                    return Arrays.asList(list.getBody());
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public boolean playerPlays(
            final PlayingPlayerType p, final int indexInPlayingPlayerHand, final int milestoneIndex)
            throws NotYourTurnException, MilestoneSideMaxReachedException {


        try {
            return Executors.newSingleThreadExecutor().submit(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    final ResponseEntity<Boolean> result =
                            restTemplate.getForEntity(url + "/playerPlays?"
                            + "gamename=" + guid
                            + "&p=" + p.toString()
                            + "&indexInPlayingPlayerHand=" + indexInPlayingPlayerHand
                            + "&milestoneIndex=" + milestoneIndex, Boolean.class);

                    if (result.getStatusCode() == HttpStatus.OK) {
                        return result.getBody().booleanValue();
                    } else if (result.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                        throw new NotYourTurnException(p);
                    } else {
                        throw new MilestoneSideMaxReachedException(milestoneIndex);
                    }
                }
            }).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof NotYourTurnException) {
                throw (NotYourTurnException) e.getCause();
            }  else if (e.getCause() instanceof MilestoneSideMaxReachedException) {
                throw (MilestoneSideMaxReachedException) e.getCause();
            } else {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

}

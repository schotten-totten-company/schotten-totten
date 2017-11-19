package com.boardgames.bastien.schotten_totten.server;

import com.boardgames.bastien.schotten_totten.controllers.AbstractGameManager;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.exceptions.NotYourTurnException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.Player;
import com.boardgames.bastien.schotten_totten.model.PlayingPlayerType;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Bastien on 19/11/2017.
 */

public class RestGameClient extends AbstractGameManager {

    private final String url;

    public RestGameClient(final String url) {
        this.url = url;
    }

    public String ping() {
        final RestTemplate rest = new RestTemplate();
        return rest.getForObject(this.url + "/ping", String.class).toString();
    }

    public boolean createGame() {
        final RestTemplate rest = new RestTemplate();
        return rest.getForObject(this.url + "/createGame", Boolean.class).booleanValue();
    }

    public Card getLastPlayedCard() {
        final RestTemplate rest = new RestTemplate();
        return rest.getForObject(this.url + "/getLastPlayedCard", Card.class);
    }

    public boolean reclaimMilestone(
            final PlayingPlayerType p, final int milestoneIndex) throws NotYourTurnException {
        final RestTemplate rest = new RestTemplate();
        final ResponseEntity<Boolean> b = rest.getForEntity(this.url + "/reclaimMilestone?"
                        + "p=" + p.toString() + "&milestoneIndex=" + milestoneIndex, Boolean.class);

        if (b.getStatusCode() == HttpStatus.OK) {
            return b.getBody();
        } else {
            throw new NotYourTurnException(p);
        }
    }

    public Player getPlayer(final PlayingPlayerType p) {
        final RestTemplate rest = new RestTemplate();
        return rest.getForObject(
                this.url + "/getPlayer?" + "p=" + p.toString(), Player.class);
    }

    public Player getPlayingPlayer() {
        final RestTemplate rest = new RestTemplate();
        return rest.getForObject(this.url + "/getPlayingPlayer", Player.class);
    }

    public Player getWinner() throws NoPlayerException {
        final RestTemplate rest = new RestTemplate();
        final ResponseEntity<Player> winner = rest.getForEntity(this.url + "/getWinner", Player.class);
        if (winner.getStatusCode() == HttpStatus.OK) {
            return winner.getBody();
        } else {
            throw new NoPlayerException(winner.getStatusCode().getReasonPhrase());
        }
    }

    public List<Milestone> getMilestones() {
        final RestTemplate rest = new RestTemplate();
        final ResponseEntity<Milestone[]> list =
                rest.getForEntity(this.url + "/getMilestones", Milestone[].class);
        return Arrays.asList(list.getBody());
    }

    public boolean playerPlays(
            final PlayingPlayerType p, final int indexInPlayingPlayerHand, final int milestoneIndex)
            throws NotYourTurnException, MilestoneSideMaxReachedException {

        final RestTemplate rest = new RestTemplate();
        final ResponseEntity<Boolean> result = rest.getForEntity(this.url + "/playerPlays?"
                + "p=" + p.toString()
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

}

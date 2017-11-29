package com.boardgames.bastien.schotten_totten.server;

import com.boardgames.bastien.schotten_totten.controllers.AbstractGameManager;
import com.boardgames.bastien.schotten_totten.controllers.SimpleGameManager;
import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.exceptions.NotYourTurnException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.Player;
import com.boardgames.bastien.schotten_totten.model.PlayingPlayerType;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.List;

/**
 * Created by Bastien on 21/11/2017.
 */

@RestController
public class RestGameControllers {

    private AbstractGameManager gameManager;

    @RequestMapping("/ping")
    public String ping() {
        return new Date().toString() + " - it is time to SCHOTTEN !!!!";
    }

    @RequestMapping("/createGame")
    public boolean createGame() {
        try {
            gameManager = new SimpleGameManager("Player 1", "Player 2");
            return true;
        } catch (final GameCreationException e) {
            return false;
        }
    }

    @RequestMapping("/getLastPlayedCard")
    public Card getLastPlayedCard() {
        return gameManager.getLastPlayedCard();
    }

    @RequestMapping("/swapPlayers")
    public Boolean swapPlayers() {
        return gameManager.swapPlayers();
    }

    @RequestMapping("/reclaimMilestone")
    public Boolean reclaimMilestone(
            @RequestParam(value="p") PlayingPlayerType p,
            @RequestParam(value="milestoneIndex") int milestoneIndex) throws NotYourTurnException {
        return new Boolean(gameManager.reclaimMilestone(p, milestoneIndex));
    }

    @RequestMapping("/getPlayer")
    public Player getPlayer(
            @RequestParam(value="p") PlayingPlayerType p) {
        return gameManager.getPlayer(p);
    }

    @RequestMapping("/getPlayingPlayer")
    public Player getPlayingPlayer() {
        return gameManager.getPlayingPlayer();
    }

    @RequestMapping("/getWinner")
    public Player getWinner() throws NoPlayerException {
        return gameManager.getWinner();
    }

    @RequestMapping("/getMilestones")
    public List<Milestone> getMilestones() {
        return gameManager.getMilestones();
    }

    @RequestMapping("/playerPlays")
    public Boolean playerPlays(
            @RequestParam(value="p") PlayingPlayerType p,
            @RequestParam(value="indexInPlayingPlayerHand") int indexInPlayingPlayerHand,
            @RequestParam(value="milestoneIndex") int milestoneIndex)
            throws NotYourTurnException, MilestoneSideMaxReachedException {

        try {
            gameManager.playerPlays(p, indexInPlayingPlayerHand, milestoneIndex);
            return true;
        } catch (final EmptyDeckException e) {
            // nothing to send to the client
            e.printStackTrace();
            return false;
        } catch (final HandFullException e) {
            // nothing to send to the client
            e.printStackTrace();
            return false;
        }
    }


}

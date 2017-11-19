package com.boardgames.bastien.schotten_totten.controllers;

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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * Created by Bastien on 19/10/2017.
 */

@SpringBootApplication
public class LanGameManager {

    private SimpleGameManager gameManager;
    private ConfigurableApplicationContext context;

    public void start() {
        context = SpringApplication.run(LanGameManager.class);
    }

    public void stop() {
        SpringApplication.exit(context);
    }

    @RestController
    private class Controlers {

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

        @RequestMapping("/reclaimMilestone")
        public boolean reclaimMilestone(
                @RequestParam(value="p") PlayingPlayerType p,
                @RequestParam(value="milestoneIndex") int milestoneIndex) throws NotYourTurnException {
            return gameManager.reclaimMilestone(p, milestoneIndex);
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
        public void playerPlays(
                @RequestParam(value="p") PlayingPlayerType p,
                @RequestParam(value="indexInPlayingPlayerHand") int indexInPlayingPlayerHand,
                @RequestParam(value="milestoneIndex") int milestoneIndex)
                throws NotYourTurnException, EmptyDeckException, HandFullException, MilestoneSideMaxReachedException {

            gameManager.playerPlays(p, indexInPlayingPlayerHand, milestoneIndex);
        }

    }

}

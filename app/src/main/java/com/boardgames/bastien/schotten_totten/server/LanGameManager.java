package com.boardgames.bastien.schotten_totten.server;

import com.boradgames.bastien.schotten_totten.core.controllers.GameManagerInterface;
import com.boradgames.bastien.schotten_totten.core.exceptions.EmptyDeckException;
import com.boradgames.bastien.schotten_totten.core.exceptions.HandFullException;
import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NoPlayerException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NotYourTurnException;
import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.Player;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.util.List;

/**
 * Created by Bastien on 19/10/2017.
 */

public class LanGameManager implements GameManagerInterface {

    public void start() {
    }

    public boolean isActive() {
        return true;
    }

    public void stop() {
        //SpringApplication.exit(context);
    }

    @Override
    public Card getLastPlayedCard() {
        return null;
    }

    @Override
    public boolean playerPlays(PlayingPlayerType playingPlayerType, int i, int i1)
            throws MilestoneSideMaxReachedException, NotYourTurnException,
            EmptyDeckException, HandFullException {
        return false;
    }

    @Override
    public boolean reclaimMilestone(PlayingPlayerType playingPlayerType, int i)
            throws NotYourTurnException {
        return false;
    }

    @Override
    public Player getPlayingPlayer() {
        return null;
    }

    @Override
    public Player getPlayer(PlayingPlayerType playingPlayerType) {
        return null;
    }

    @Override
    public boolean swapPlayers() {
        return false;
    }

    @Override
    public Player getWinner() throws NoPlayerException {
        return null;
    }

    @Override
    public List<Milestone> getMilestones() {
        return null;
    }
}

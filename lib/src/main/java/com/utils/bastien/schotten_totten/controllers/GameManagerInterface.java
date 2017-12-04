package com.utils.bastien.schotten_totten.controllers;

import com.utils.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.utils.bastien.schotten_totten.exceptions.HandFullException;
import com.utils.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.utils.bastien.schotten_totten.exceptions.NoPlayerException;
import com.utils.bastien.schotten_totten.exceptions.NotYourTurnException;
import com.utils.bastien.schotten_totten.model.Milestone;
import com.utils.bastien.schotten_totten.model.Player;
import com.utils.bastien.schotten_totten.model.PlayingPlayerType;

import java.util.List;

/**
 * Created by Bastien on 07/11/2017.
 */

public interface GameManagerInterface {

    public boolean playerPlays(final PlayingPlayerType p, final int indexInPlayingPlayerHand, final int milestoneIndex)
            throws MilestoneSideMaxReachedException, NotYourTurnException, EmptyDeckException, HandFullException;

    public boolean reclaimMilestone(final PlayingPlayerType p, final int milestoneIndex) throws NotYourTurnException;

    public Player getPlayingPlayer();

    public Player getPlayer(final PlayingPlayerType p);

    public boolean swapPlayers();

    public Player getWinner() throws NoPlayerException;

    public List<Milestone> getMilestones();
}

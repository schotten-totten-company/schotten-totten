package com.boardgames.bastien.schotten_totten.controllers;

import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NotYourTurnException;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

/**
 * Created by Bastien on 07/11/2017.
 */

public interface GameManagerInterface {

    public Game playerPlays(final PlayerType p, final int indexInPlayingPlayerHand, final int milestoneIndex)
            throws MilestoneSideMaxReachedException, NotYourTurnException, EmptyDeckException;

    public void swapPlayingPlayer();

    public boolean reclaimMilestone(final PlayerType p, final int milestoneIndex) throws NotYourTurnException;
}

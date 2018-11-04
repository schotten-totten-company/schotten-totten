package com.boardgames.bastien.schotten_totten.ai;


import com.boradgames.bastien.schotten_totten.core.controllers.GameManagerInterface;
import com.boradgames.bastien.schotten_totten.core.exceptions.CardInitialisationException;
import com.boradgames.bastien.schotten_totten.core.exceptions.HandFullException;
import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NoPlayerException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NotYourTurnException;
import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.Player;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bastien on 27/03/2017.
 */

public abstract class GameAI {

    public void reclaimAndPlay(final GameManagerInterface gameManager) {

        // reclaim
        reclaim(gameManager);

        // play
        play(gameManager);
    }

    protected void reclaim(GameManagerInterface gameManager) {
        try {
            for (final Milestone m :gameManager.getMilestones()) {
                gameManager.reclaimMilestone(PlayingPlayerType.TWO, m.getId());
            }
        } catch (final NotYourTurnException e) {
            // nothing to do
        }
    }

    protected abstract void play(final GameManagerInterface gameManager);

}

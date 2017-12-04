package com.boardgames.bastien.schotten_totten.ai;

import com.utils.bastien.schotten_totten.controllers.AbstractGameManager;
import com.utils.bastien.schotten_totten.controllers.GameManagerInterface;
import com.utils.bastien.schotten_totten.controllers.SimpleGameManager;
import com.utils.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.utils.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.utils.bastien.schotten_totten.exceptions.HandFullException;
import com.utils.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.utils.bastien.schotten_totten.exceptions.NoPlayerException;
import com.utils.bastien.schotten_totten.model.Card;
import com.utils.bastien.schotten_totten.model.Game;
import com.utils.bastien.schotten_totten.model.Hand;
import com.utils.bastien.schotten_totten.model.Milestone;
import com.utils.bastien.schotten_totten.model.Player;
import com.utils.bastien.schotten_totten.model.MilestonePlayerType;
import com.utils.bastien.schotten_totten.model.PlayingPlayerType;

/**
 * Created by Bastien on 27/03/2017.
 */

public abstract class GameAI {

    public void reclaimAndPlay(final AbstractGameManager gameManager) throws NoPlayerException,
            MilestoneSideMaxReachedException,
            CardInitialisationException,
            HandFullException {

        final Player playingPlayer = gameManager.getPlayingPlayer();

        // reclaim
        reclaim(gameManager);

        // play
        play(gameManager);
    }

    protected abstract void reclaim(final GameManagerInterface gameManager);

    protected abstract void play(final GameManagerInterface gameManager);

}

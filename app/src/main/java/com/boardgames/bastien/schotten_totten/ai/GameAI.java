package com.boardgames.bastien.schotten_totten.ai;


import com.boradgames.bastien.schotten_totten.core.controllers.GameManagerInterface;
import com.boradgames.bastien.schotten_totten.core.exceptions.CardInitialisationException;
import com.boradgames.bastien.schotten_totten.core.exceptions.HandFullException;
import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NoPlayerException;
import com.boradgames.bastien.schotten_totten.core.model.Player;

/**
 * Created by Bastien on 27/03/2017.
 */

public abstract class GameAI {

    public void reclaimAndPlay(final GameManagerInterface gameManager) throws NoPlayerException,
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

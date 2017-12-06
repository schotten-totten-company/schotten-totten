package com.boardgames.bastien.schotten_totten.ai;


import com.boradgames.bastien.schotten_totten.core.controllers.GameManagerInterface;
import com.boradgames.bastien.schotten_totten.core.exceptions.NotYourTurnException;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

/**
 * Created by Bastien on 27/03/2017.
 */

public class GameAiImpl extends GameAI {


    @Override
    protected void reclaim(GameManagerInterface gameManager) {
        try {
            gameManager.reclaimMilestone(PlayingPlayerType.TWO, 0);
        } catch (final NotYourTurnException e) {
            //
        }
    }

    @Override
    protected void play(GameManagerInterface gameManager) {
        //
    }
}

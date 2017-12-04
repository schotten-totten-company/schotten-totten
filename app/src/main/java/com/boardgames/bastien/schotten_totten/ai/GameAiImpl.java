package com.boardgames.bastien.schotten_totten.ai;

import com.utils.bastien.schotten_totten.controllers.GameManagerInterface;
import com.utils.bastien.schotten_totten.exceptions.NotYourTurnException;
import com.utils.bastien.schotten_totten.model.Hand;
import com.utils.bastien.schotten_totten.model.Milestone;
import com.utils.bastien.schotten_totten.model.PlayingPlayerType;

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

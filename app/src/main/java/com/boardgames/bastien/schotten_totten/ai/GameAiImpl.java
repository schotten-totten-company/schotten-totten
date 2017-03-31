package com.boardgames.bastien.schotten_totten.ai;

import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.model.Hand;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import java.util.List;

/**
 * Created by Bastien on 27/03/2017.
 */

public class GameAiImpl extends GameAI {

    @Override
    public GameResult play(final List<Milestone> milestoneList, final Hand hand) {
        for (final Milestone m : milestoneList) {
            if(m.getPlayer2Side().size() < m.MAX_CARDS_PER_SIDE) {
                return new GameResult(m.getId(), 0);
            }
        }
        return new GameResult(0, 0);
    }
}

package com.boardgames.bastien.schotten_totten.ai;

import com.boardgames.bastien.schotten_totten.model.Hand;
import com.boardgames.bastien.schotten_totten.model.Milestone;

import java.util.List;

/**
 * Created by Bastien on 27/03/2017.
 */

public class GameAiImpl extends GameAI {

    @Override
    public GameResult play(List<Milestone> milestoneList, Hand hand) {
        return new GameResult(0, 0);
    }
}

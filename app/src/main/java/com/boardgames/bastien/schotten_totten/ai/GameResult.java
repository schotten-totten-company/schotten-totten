package com.boardgames.bastien.schotten_totten.ai;

import com.boardgames.bastien.schotten_totten.exceptions.NoMilestoneToRelaimException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Milestone;

/**
 * Created by Bastien on 27/03/2017.
 */

public class GameResult {

    private final int indexOfMilestoneToPlay;
    private final int cardToPlay;

    public GameResult(final int indexOfMilestoneToPlay, final int handIndexOfCardToPlay) {
        this.indexOfMilestoneToPlay = indexOfMilestoneToPlay;
        this.cardToPlay = handIndexOfCardToPlay;
    }

    public int getMilestoneToAdd() {
        return this.indexOfMilestoneToPlay;
    }

    public int handIndexOfCardToPlay() {
        return this.cardToPlay;
    }
}

package com.boardgames.bastien.schotten_totten.exceptions;

import com.boardgames.bastien.schotten_totten.model.Milestone;

/**
 * Created by Bastien on 29/11/2016.
 */

public class MilestoneSideMaxReachedException extends Exception {

    public MilestoneSideMaxReachedException(final int id) {
        super("Milestone side is full.");
    }
}

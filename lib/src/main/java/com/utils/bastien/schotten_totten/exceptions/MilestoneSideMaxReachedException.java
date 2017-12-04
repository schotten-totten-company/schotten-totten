package com.utils.bastien.schotten_totten.exceptions;

/**
 * Created by Bastien on 29/11/2016.
 */

public class MilestoneSideMaxReachedException extends Exception {

    public MilestoneSideMaxReachedException(final int id) {
        super("Milestone side is full.");
    }
}

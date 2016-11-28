package com.boardgames.bastien.schotten_totten;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 28/11/2016.
 */

public class Utils {

    private static final List<Integer> allowedColors = new ArrayList<>();

    public Utils() {
        allowedColors.add(Color.BLUE);
        allowedColors.add(Color.CYAN);
        allowedColors.add(Color.GREEN);
        allowedColors.add(Color.RED);
        allowedColors.add(Color.YELLOW);
        allowedColors.add(Color.GRAY);
    }

    public static List<Integer> getAllowedColors() {
        return allowedColors;
    }

}

package com.boardgames.bastien.schotten_totten.view;

import android.widget.LinearLayout;

/**
 * Created by Bastien on 10/12/2016.
 */

public class Margin {

    public static LinearLayout.LayoutParams createMargin() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        return layoutParams;
    }
}

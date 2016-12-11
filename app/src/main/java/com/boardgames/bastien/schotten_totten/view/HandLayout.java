package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.Game;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Milestone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 10/12/2016.
 */

public class HandLayout extends LinearLayout {

    private LayoutParams margin = Margin.createMargin();

    public HandLayout(final Context ctx, final Game game, final List<HandCardView> handView) {
        super(ctx);
        setOrientation(LinearLayout.HORIZONTAL);
        setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        for (final HandCardView card : handView) {
            addView(card);
        }
    }

}

package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Milestone;

/**
 * Created by Bastien on 10/12/2016.
 */

public class MilestoneCardView extends TextView {

    public MilestoneCardView(final Context context) {
        super(context);
        setTextSize(15);
        setPadding(10, 10, 10, 10);
        setText("[ X ]");
        setBackgroundColor(Color.BLACK);
        setTextColor(Color.WHITE);
    }
}

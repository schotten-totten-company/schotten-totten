package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Milestone;

/**
 * Created by Bastien on 10/12/2016.
 */

public class MilestoneView extends CardView {



    public MilestoneView(final Context context, final int id) {
        super(context, null);
        setId(id);
        setText("[ X ]");
        setBackgroundColor(Color.BLACK);
        setTextColor(Color.WHITE);
    }
}

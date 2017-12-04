package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.widget.TextView;

import com.utils.bastien.schotten_totten.model.Card;
import com.utils.bastien.schotten_totten.model.Milestone;

/**
 * Created by Bastien on 10/12/2016.
 */

public class MilestoneCardView extends AppCompatTextView {

    private final int index;

    public MilestoneCardView(final Context context, final OnClickListener clickListener, final int i) {
        this(context, i);
        setOnClickListener(clickListener);
    }

    public MilestoneCardView(final Context context, final int i) {
        super(context);
        index = i;
        setTextSize(10);
        setPadding(10, 10, 10, 10);
        setText("[ X ]");
        setBackgroundColor(Color.BLACK);
        setTextColor(Color.WHITE);
        setTypeface(null, Typeface.NORMAL);
    }

    public int getIndex() { return this.index;}

    public void select() {
        setTypeface(null, Typeface.BOLD);
    }
    public void unselect() {
        setTypeface(null, Typeface.NORMAL);
    }
}

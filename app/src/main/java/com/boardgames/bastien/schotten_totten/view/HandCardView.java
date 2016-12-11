package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;
import android.graphics.Typeface;

import com.boardgames.bastien.schotten_totten.R;
import com.boardgames.bastien.schotten_totten.model.Card;

/**
 * Created by Bastien on 11/12/2016.
 */

public class HandCardView extends CardView {

    public HandCardView(final Context context, final Card card, final OnClickListener clickListener) {
        super(context, card);
        setOnClickListener(clickListener);
        setTypeface(null, Typeface.NORMAL);
    }

    public void select() {
        setTypeface(null, Typeface.BOLD);
    }
    public void unselect() {
        setTypeface(null, Typeface.NORMAL);
    }

}

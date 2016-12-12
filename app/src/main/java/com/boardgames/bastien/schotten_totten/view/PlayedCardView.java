package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;

import com.boardgames.bastien.schotten_totten.model.Card;

/**
 * Created by Bastien on 11/12/2016.
 */

public class PlayedCardView extends CardView {

    public PlayedCardView(final Context context, final Card c) {
        super(context, c);
    }

    public PlayedCardView(final Context context) {
        super(context, null);
    }
}

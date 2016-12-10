package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;
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

    private final List<CardView> hand = new ArrayList<>();

    public HandLayout(final Context ctx, final Game game) {
        super(ctx);
        setOrientation(LinearLayout.HORIZONTAL);
        setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
        for (int i = 0; i < game.getPlayer1().getHand().getCards().size(); i++) {
            final CardView card = new CardView(ctx, game.getPlayer1().getHand().getCards().get(i), i);
            card.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            game.setChosenCard(((CardView) v).getId());
                            Toast.makeText(ctx, "Card Chosen", Toast.LENGTH_SHORT).show();
                        }
                    });
            hand.add(card);
            addView(hand.get(i));
        }
        update(ctx, game);
    }

    public void update(final Context ctx, final Game game) {
        for (int i = 0; i < game.getPlayer1().getHand().getCards().size(); i++) {
            final CardView card = new CardView(ctx, game.getPlayer1().getHand().getCards().get(i));
            card.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            game.setChosenCard(((CardView) v).getId());
                            Toast.makeText(ctx, "Card Chosen", Toast.LENGTH_SHORT).show();
                        }
                    });
            hand.set(i, card);
        }
    }
}

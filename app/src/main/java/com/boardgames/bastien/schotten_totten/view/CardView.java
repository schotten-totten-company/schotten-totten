package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.model.Card;

/**
 * Created by Bastien on 10/12/2016.
 */

public class CardView extends TextView {

    public CardView(final Context context, final Card c) {
        this(context, c, -1);
    }
    public CardView(final Context context, final Card c, final int id) {
        super(context);
        setId(id);
        setTextSize(15);
        setPadding(10, 10, 10, 10);
        setTextColor(Color.BLACK);
        if (c == null) {
            setText("[ 0 ]");
            setBackgroundColor(Color.LTGRAY);
        } else {
            setText("[ " + c.getNumber().toString() + " ]");
            switch (c.getColor()) {
                case BLUE:
                    setBackgroundColor(Color.BLUE);
                    break;
                case YELLOW:
                    setBackgroundColor(Color.YELLOW);
                    break;
                case GREEN:
                    setBackgroundColor(Color.GREEN);
                    break;
                case GREY:
                    setBackgroundColor(Color.GRAY);
                    break;
                case RED:
                    setBackgroundColor(Color.RED);
                    break;
                case CYAN:
                    setBackgroundColor(Color.CYAN);
                    break;
            }
        }
    }

    public void highlight() {
        setTextColor(Color.LTGRAY);
    }
}

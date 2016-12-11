package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.R;
import com.boardgames.bastien.schotten_totten.model.Card;

/**
 * Created by Bastien on 10/12/2016.
 */

public abstract class CardView extends TextView {

    public CardView(final Context context, final Card c) {
        super(context);
        setTextSize(15);
        setPadding(10, 10, 10, 10);
        setTextColor(Color.BLACK);
        if (c == null) {
            setText("[ 0 ]");
            setBackgroundColor(Color.LTGRAY);
        } else {
            update(c);
        }
    }

    public void update(final Card c) {
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

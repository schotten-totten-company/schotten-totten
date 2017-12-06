package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;

import com.boradgames.bastien.schotten_totten.core.model.Card;

/**
 * Created by Bastien on 10/12/2016.
 */

public abstract class CardView extends AppCompatTextView {

    public CardView(final Context context, final Card c) {
        this(context);
        update(c);
    }

    public CardView(final Context context) {
        super(context);
        setTextSize(10);
        setPadding(10, 10, 10, 10);
        setTextColor(Color.BLACK);
        setText("[ _ ]");
        setBackgroundColor(Color.LTGRAY);
    }

    public void update() {
        setText("[ _ ]");
        setBackgroundColor(Color.LTGRAY);
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

package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 10/12/2016.
 */

public class MilestoneLayout extends LinearLayout {

    private LinearLayout.LayoutParams margin = Margin.createMargin();

    public MilestoneLayout(final Context ctx, final MilestoneView milestoneView) {
        super(ctx);
        setOrientation(LinearLayout.VERTICAL);

        addView(milestoneView.getMilestoneOpponent());
        for (int i = milestoneView.getOpponentSide().size()-1; i >= 0; i--) {
            addView(milestoneView.getOpponentSide().get(i), margin);
        }
        addView(milestoneView.getMilestone(), margin);
        for (final PlayedCardView card : milestoneView.getPlayerSide()) {
            addView(card, margin);
        }
        addView(milestoneView.getMilestonePlayer());
    }

}

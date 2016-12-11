package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.Game;
import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Milestone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 10/12/2016.
 */

public class MilestoneLayout extends LinearLayout {

    private LinearLayout.LayoutParams margin = Margin.createMargin();

    private final List<CardView> playerSide = new ArrayList<>();
    private final List<CardView> opponentSide = new ArrayList<>();

    public MilestoneLayout(final Context ctx, final Milestone m, final Game game) {
        super(ctx);
        setOrientation(LinearLayout.VERTICAL);
        setId(m.getId());
        for (int i = 0; i < m.MAX_CARDS_PER_SIDE; i++) {
            final CardView card = new CardView(ctx, null);
            opponentSide.add(card);
            addView(opponentSide.get(i), margin);
        }
        final MilestoneView milestoneView = new MilestoneView(ctx, m.getId());
        milestoneView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        game.setChosenMilestone(((MilestoneView) v).getId());
                        Toast.makeText(ctx, "Milestone Chosen", Toast.LENGTH_SHORT).show();
                        if (game.getChosenCard() != -1 && game.getChosenMilestone() != -1) {
                            try {
                                final Card card = game.getPlayer1().getHand().playCard(game.getChosenCard());
                                game.getGameBoard().getMilestones().get(game.getChosenMilestone()).addCard(card, game.getPlayerTurn());
                            } catch (CardInitialisationException e) {
                                e.printStackTrace();
                            } catch (MilestoneSideMaxReachedException e) {
                                e.printStackTrace();
                            }
                            update(ctx, game.getGameBoard().getMilestones().get(game.getChosenMilestone()));
                        }
                    }
                });
        addView(milestoneView, margin);
        for (int i = 0; i < m.MAX_CARDS_PER_SIDE; i++) {
            final CardView card = new CardView(ctx, null);
            playerSide.add(card);
            addView(playerSide.get(i), margin);
        }
        update(ctx, m);
    }

    public void update(final Context ctx, final Milestone m) {
        for (int i = 0; i < m.getPlayer1Side().size(); i++) {
            final CardView card = new CardView(ctx, m.getPlayer1Side().get(i));
            playerSide.set(i, card);
        }
        for (int i = 0; i < m.getPlayer1Side().size(); i++) {
            opponentSide.set(i, new CardView(ctx, m.getPlayer1Side().get(i)));
        }
    }
}

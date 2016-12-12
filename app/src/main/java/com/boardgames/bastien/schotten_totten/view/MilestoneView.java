package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;
import android.view.View;

import com.boardgames.bastien.schotten_totten.exceptions.NoTurnException;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 11/12/2016.
 */

public class MilestoneView {

    private final List<PlayedCardView> playerSide = new ArrayList<>();
    private final List<PlayedCardView> opponentSide = new ArrayList<>();

    private final MilestoneCardView milestone;
    private final MilestoneCardView milestonePlayer;
    private final MilestoneCardView milestoneOpponent;


    public MilestoneView(final Milestone m, final Context context, final View.OnClickListener clickListener) {
        for (int i = 0; i < m.MAX_CARDS_PER_SIDE; i++) {
            playerSide.add(new PlayedCardView(context));
            opponentSide.add(new PlayedCardView(context));
        }
        final int index = m.getId();
        milestone = new MilestoneCardView(context, clickListener, index);
        milestonePlayer = new MilestoneCardView(context, index);
        milestonePlayer.setVisibility(View.INVISIBLE);
        milestoneOpponent = new MilestoneCardView(context, index);
        milestoneOpponent.setVisibility(View.INVISIBLE);
    }

    public void update(final Milestone m, final PlayerType playerTurn) throws NoTurnException {
        switch (playerTurn) {
            case ONE:
                for (int i = 0; i < m.getPlayer1Side().size(); i++) {
                    playerSide.get(i).update(m.getPlayer1Side().get(i));
                }
                for (int i = 0; i < m.getPlayer2Side().size(); i++) {
                    opponentSide.get(i).update(m.getPlayer2Side().get(i));
                }
                if (m.getCaptured().equals(playerTurn)) {
                    milestonePlayer.setVisibility(View.VISIBLE);
                    milestoneOpponent.setVisibility(View.INVISIBLE);
                    milestone.setVisibility(View.INVISIBLE);
                }
                break;
            case TWO:
                for (int i = 0; i < m.getPlayer1Side().size(); i++) {
                    opponentSide.get(i).update(m.getPlayer1Side().get(i));
                }
                for (int i = 0; i < m.getPlayer2Side().size(); i++) {
                    playerSide.get(i).update(m.getPlayer2Side().get(i));
                }
                if (m.getCaptured().equals(playerTurn)) {
                    milestonePlayer.setVisibility(View.VISIBLE);
                    milestoneOpponent.setVisibility(View.INVISIBLE);
                    milestone.setVisibility(View.INVISIBLE);
                }
                break;
            case NONE:
                throw new NoTurnException(playerTurn.toString());
        }
    }

    public List<PlayedCardView> getPlayerSide() {
        return playerSide;
    }

    public List<PlayedCardView> getOpponentSide() {
        return opponentSide;
    }

    public MilestoneCardView getMilestone() {
        return milestone;
    }

    public MilestoneCardView getMilestonePlayer() {
        return milestonePlayer;
    }

    public MilestoneCardView getMilestoneOpponent() {
        return milestoneOpponent;
    }
}

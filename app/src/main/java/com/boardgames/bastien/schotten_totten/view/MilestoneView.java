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
                updateSide(m, playerSide, opponentSide);
                break;
            case TWO:
                updateSide(m, opponentSide, playerSide);
                break;
            case NONE:
                throw new NoTurnException(playerTurn.toString());
        }
        // update captured by playing player
        if (m.getCaptured().equals(playerTurn)) {
            milestonePlayer.setVisibility(View.VISIBLE);
            milestoneOpponent.setVisibility(View.INVISIBLE);
            milestone.setVisibility(View.INVISIBLE);
        // update not captured
        } else if (m.getCaptured().equals(PlayerType.NONE)) {
            milestonePlayer.setVisibility(View.INVISIBLE);
            milestoneOpponent.setVisibility(View.INVISIBLE);
            milestone.setVisibility(View.VISIBLE);
        // update captured by opponent
        } else {
            milestonePlayer.setVisibility(View.INVISIBLE);
            milestoneOpponent.setVisibility(View.VISIBLE);
            milestone.setVisibility(View.INVISIBLE);
        }
    }

    private void updateSide(final Milestone m, final List<PlayedCardView> side1, final List<PlayedCardView> side2) {
        // reset all cards on both sides
        for (int i = 0; i < m.MAX_CARDS_PER_SIDE; i++) {
            side1.get(i).update();
            side2.get(i).update();
        }
        // update player one side
        for (int i = 0; i < m.getPlayer1Side().size(); i++) {
            side1.get(i).update(m.getPlayer1Side().get(i));
        }
        // update player two side
        for (int i = 0; i < m.getPlayer2Side().size(); i++) {
            side2.get(i).update(m.getPlayer2Side().get(i));
        }
    }

    public int getWidth() {
        return milestone.getWidth();
    }

    public void increaseSize() {
        increaseSize((float)0.1);
    }

    public void increaseSize(final float increment) {

        for (final PlayedCardView playedCardView : this.playerSide) {
            playedCardView.setTextSize(playedCardView.getTextSize() + increment);
        }
        for (final PlayedCardView playedCardView : this.opponentSide) {
            playedCardView.setTextSize(playedCardView.getTextSize() + increment);
        }
        milestone.setTextSize(milestone.getTextSize() + increment);
        milestonePlayer.setTextSize(milestonePlayer.getTextSize() + increment);
        milestoneOpponent.setTextSize(milestoneOpponent.getTextSize() + increment);
    }

    public void setSize(final float size) {

        for (final PlayedCardView playedCardView : this.playerSide) {
            playedCardView.setTextSize(size);
        }
        for (final PlayedCardView playedCardView : this.opponentSide) {
            playedCardView.setTextSize(size);
        }
        milestone.setTextSize(size);
        milestonePlayer.setTextSize(size);
        milestoneOpponent.setTextSize(size);
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

package com.boardgames.bastien.schotten_totten.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 11/12/2016.
 */

public class MilestoneView {

    private final List<ImageView> playerSide;
    private final List<ImageView> opponentSide;

    private final ImageButton milestone;
    private final ImageView milestonePlayer;
    private final ImageView milestoneOpponent;


    public MilestoneView(final ImageButton m, final ImageView mPlayer, final ImageView mOpponent,
                            final List<ImageView> pSide, final List<ImageView> oSide) {
        playerSide = pSide;
        opponentSide = oSide;
        milestone = m;
        milestonePlayer = mPlayer;
        milestonePlayer.setVisibility(View.INVISIBLE);
        milestoneOpponent = mOpponent;
        milestoneOpponent.setVisibility(View.INVISIBLE);
    }

//    public void update(final Milestone m, final PlayerType playerTurn) throws NoPlayerException {
//        switch (playerTurn) {
//            case ONE:
//                updateSide(m, playerSide, opponentSide);
//                break;
//            case TWO:
//                updateSide(m, opponentSide, playerSide);
//                break;
//            case NONE:
//                throw new NoPlayerException(playerTurn.toString());
//        }
//        // update captured by playing player
//        if (m.getCaptured().equals(playerTurn)) {
//            milestonePlayer.setVisibility(View.VISIBLE);
//            milestoneOpponent.setVisibility(View.INVISIBLE);
//            milestone.setVisibility(View.INVISIBLE);
//        // update not captured
//        } else if (m.getCaptured().equals(PlayerType.NONE)) {
//            milestonePlayer.setVisibility(View.INVISIBLE);
//            milestoneOpponent.setVisibility(View.INVISIBLE);
//            milestone.setVisibility(View.VISIBLE);
//        // update captured by opponent
//        } else {
//            milestonePlayer.setVisibility(View.INVISIBLE);
//            milestoneOpponent.setVisibility(View.VISIBLE);
//            milestone.setVisibility(View.INVISIBLE);
//        }
//    }
//
//    private void updateSide(final Milestone m, final List<PlayedCardView> side1, final List<PlayedCardView> side2) {
//        // reset all cards on both sides
//        for (int i = 0; i < m.MAX_CARDS_PER_SIDE; i++) {
//            side1.get(i).update();
//            side2.get(i).update();
//        }
//        // update player one side
//        for (int i = 0; i < m.getPlayer1Side().size(); i++) {
//            side1.get(i).update(m.getPlayer1Side().get(i));
//        }
//        // update player two side
//        for (int i = 0; i < m.getPlayer2Side().size(); i++) {
//            side2.get(i).update(m.getPlayer2Side().get(i));
//        }
//    }
//
//    public int getWidth() {
//        return milestone.getWidth();
//    }
//
//    public void increaseSize() {
//        increaseSize((float)0.1);
//    }
//
//    public void increaseSize(final float increment) {
//
//        for (final PlayedCardView playedCardView : this.playerSide) {
//            playedCardView.setTextSize(playedCardView.getTextSize() + increment);
//        }
//        for (final PlayedCardView playedCardView : this.opponentSide) {
//            playedCardView.setTextSize(playedCardView.getTextSize() + increment);
//        }
//        milestone.setTextSize(milestone.getTextSize() + increment);
//        milestonePlayer.setTextSize(milestonePlayer.getTextSize() + increment);
//        milestoneOpponent.setTextSize(milestoneOpponent.getTextSize() + increment);
//    }
//
//    public void setSize(final float size) {
//
//        for (final PlayedCardView playedCardView : this.playerSide) {
//            playedCardView.setTextSize(size);
//        }
//        for (final PlayedCardView playedCardView : this.opponentSide) {
//            playedCardView.setTextSize(size);
//        }
//        milestone.setTextSize(size);
//        milestonePlayer.setTextSize(size);
//        milestoneOpponent.setTextSize(size);
//    }

    public List<ImageView> getPlayerSide() {
        return playerSide;
    }

    public List<ImageView> getOpponentSide() {
        return opponentSide;
    }

    public ImageButton getMilestone() {
        return milestone;
    }

    public ImageView getMilestonePlayer() {
        return milestonePlayer;
    }

    public ImageView getMilestoneOpponent() {
        return milestoneOpponent;
    }
}

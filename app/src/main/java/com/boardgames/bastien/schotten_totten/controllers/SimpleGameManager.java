package com.boardgames.bastien.schotten_totten.controllers;

import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.exceptions.NotYourTurnException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.Player;
import com.boardgames.bastien.schotten_totten.model.PlayingPlayerType;

import java.util.List;

/**
 * Created by Bastien on 19/10/2017.
 */

public class SimpleGameManager extends AbstractGameManager {

    public SimpleGameManager(final String player1Name, final String player2Name, final long uid) throws GameCreationException {
        super(player1Name, player2Name, uid);
    }

    public SimpleGameManager(final String player1Name, final String player2Name) throws GameCreationException {
        super(player1Name, player2Name, System.currentTimeMillis());
    }

    @Override
    public Card getLastPlayedCard() {
        return this.game.getGameBoard().getLastPlayedCard();
    }

    @Override
    public boolean reclaimMilestone(final PlayingPlayerType p, final int milestoneIndex) throws NotYourTurnException {
        if (game.getPlayingPlayerType() == p) {
            final Milestone milestone = game.getGameBoard().getMilestones().get(milestoneIndex);
            return milestone.reclaim(p, game.getGameBoard().getCardsNotYetPlayed());
        } else {
            throw new NotYourTurnException(p);
        }
    }

    @Override
    public Player getPlayer(final PlayingPlayerType p) {
        return this.game.getPlayer(p);
    }

    @Override
    public Player getPlayingPlayer() {
        return this.game.getPlayingPlayer();
    }

    @Override
    public Player getWinner() throws NoPlayerException {
        return this.game.getWinner();
    }

    @Override
    public List<Milestone> getMilestones() {
        return this.game.getGameBoard().getMilestones();
    }

    @Override
    public void playerPlays(final PlayingPlayerType p, final int indexInPlayingPlayerHand, final int milestoneIndex)
            throws NotYourTurnException, EmptyDeckException, HandFullException, MilestoneSideMaxReachedException {

        if (game.getPlayingPlayerType() == p) {
            final Card cardToPlay = game.getPlayingPlayer().getHand().playCard(indexInPlayingPlayerHand);
            try {
                game.getGameBoard().getMilestones().get(milestoneIndex).addCard(cardToPlay, p);
                game.getGameBoard().updateLastPlayedCard(cardToPlay);
                game.getPlayingPlayer().getHand().addCard(game.getGameBoard().getDeck().drawCard());
                game.swapPlayingPlayerType();
            } catch (final MilestoneSideMaxReachedException e) {
                game.getPlayingPlayer().getHand().addCard(cardToPlay);
                throw e;
            }

        } else {
            throw new NotYourTurnException(p);
        }
    }

}

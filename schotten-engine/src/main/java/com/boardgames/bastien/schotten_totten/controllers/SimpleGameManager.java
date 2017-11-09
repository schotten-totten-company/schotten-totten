package com.boardgames.bastien.schotten_totten.controllers;

import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.exceptions.NotYourTurnException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

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

    public boolean reclaimMilestone(final PlayerType p, final int milestoneIndex) throws NotYourTurnException {
        if (game.getPlayingPlayerType() == p) {
            final Milestone milestone = game.getGameBoard().getMilestones().get(milestoneIndex);
            return milestone.reclaim(p, game.getGameBoard().getCardsNotYetPlayed());
        } else {
            throw new NotYourTurnException(p);
        }
    }

    public Game playerPlays(final PlayerType p, final int indexInPlayingPlayerHand, final int milestoneIndex)
            throws MilestoneSideMaxReachedException, NotYourTurnException, EmptyDeckException {

        if (game.getPlayingPlayerType() == p) {
            try {

                final Card cardToPlay = game.getPlayingPlayer().getHand().playCard(indexInPlayingPlayerHand);
                game.getGameBoard().getMilestones().get(milestoneIndex).addCard(cardToPlay, p);
                game.getGameBoard().updateLastPlayedCard(cardToPlay);
                game.getPlayingPlayer().getHand().addCard(game.getGameBoard().getDeck().drawCard());
                return game;
            } catch (final NoPlayerException e) {
                return game;
            } catch (HandFullException e) {
                return game;
            }
        } else {
            throw new NotYourTurnException(p);
        }
    }

    public void swapPlayingPlayer() {
        this.game.swapPlayingPlayerType();
    }

    public Game getGame() {
        return this.game;
    }
}

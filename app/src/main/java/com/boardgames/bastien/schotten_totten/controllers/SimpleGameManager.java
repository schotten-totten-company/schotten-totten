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

public abstract class SimpleGameManager {

    private final Game game;
    private final long gameUid;

    public SimpleGameManager(final String player1Name, final String player2Name, final long uid) throws GameCreationException {
        game = new Game(player1Name, player2Name);
        this.gameUid = uid;
    }

    public SimpleGameManager(final String player1Name, final String player2Name) throws GameCreationException {
        game = new Game(player1Name, player2Name);
        this.gameUid = System.currentTimeMillis();
    }

    public boolean player1PlaysAndWins(final int cardIndex, final int milestoneIndex) throws MilestoneSideMaxReachedException, NotYourTurnException {
        return playerPlaysAndWins(PlayerType.ONE, cardIndex, milestoneIndex);
    }

    public boolean player2PlaysAndWins(final int cardIndex, final int milestoneIndex) throws MilestoneSideMaxReachedException, NotYourTurnException {
        return playerPlaysAndWins(PlayerType.TWO, cardIndex, milestoneIndex);
    }

    public boolean playingPlayerPlaysAndWins(final int cardIndex, final int milestoneIndex) throws MilestoneSideMaxReachedException {
        try {
            return playerPlaysAndWins(game.getPlayingPlayerType(), cardIndex, milestoneIndex);
        } catch (final NotYourTurnException e) {
            // cannot happen
            return false;
        }
    }

    private PlayerType getWinner() {
        try {
            return game.getWinner().getPlayerType();
        } catch (final NoPlayerException e) {
            return PlayerType.NONE;
        }
    }

    public boolean isGameFinished() {
        try {
            game.getWinner();
            return true;
        } catch (final NoPlayerException e) {
            return false;
        }
    }

    private boolean playerPlaysAndWins(final PlayerType p, final int indexInPlayingPlayerHand, final int milestoneIndex)
            throws MilestoneSideMaxReachedException, NotYourTurnException {

        if (game.getPlayingPlayerType() == p) {
            try {
                for (final Milestone m : game.getGameBoard().getMilestones()) {
                    m.reclaim(p, game.getGameBoard().getCardsNotYetPlayed());
                }
                try {
                    return game.getWinner().getPlayerType() == p;
                } catch (final NoPlayerException e) {
                    // nothing to do
                }
                final Card cardToPlay = game.getPlayingPlayer().getHand().playCard(indexInPlayingPlayerHand);
                game.getGameBoard().getMilestones().get(milestoneIndex).addCard(cardToPlay, p);
                game.getPlayingPlayer().getHand().addCard(game.getGameBoard().getDeck().drawCard());
                game.swapPlayingPlayerType();
                return false;
            } catch (final NoPlayerException e) {
                return false;
            } catch (final EmptyDeckException e) {
                return false;
            } catch (HandFullException e) {
                return false;
            }
        } else {
            throw new NotYourTurnException(p);
        }
    }

    public abstract int chooseCardToPlay();

    public abstract int chooseMilestoneWhereToPlay();

    public PlayerType playWholeGame() {
        while(!isGameFinished()) {
            try {
                playingPlayerPlaysAndWins(chooseCardToPlay(), chooseMilestoneWhereToPlay());
            } catch (MilestoneSideMaxReachedException e) {
                // dont care, continue to play
            }
        }
        return getWinner();
    }

}

package com.boardgames.bastien.schotten_totten.ai;

import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.Hand;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.Player;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import java.util.List;

/**
 * Created by Bastien on 27/03/2017.
 */

public abstract class GameAI {

    public Game reclaimAndPlay(final Game game) throws NoPlayerException,
            MilestoneSideMaxReachedException,
            CardInitialisationException,
            HandFullException {

        final Player playingPlayer = game.getPlayer(game.getPlayingPlayer().getPlayerType());

        // reclaim
        reclaim(game);

        // game game result from ai
        final GameResult gameRes =
                play(game.getGameBoard().getMilestones(), playingPlayer.getHand());

        // get card to play from the hand
        final Card cardToPlay = playingPlayer.getHand().playCard(gameRes.handIndexOfCardToPlay());

        // update last played card
        game.getGameBoard().updateLastPlayedCard(cardToPlay);

        // put it on the board
        game.getGameBoard().getMilestones().get(gameRes.getMilestoneToAdd())
                .addCard(cardToPlay, playingPlayer.getPlayerType());

        // draw new card
        try {
            playingPlayer.getHand().addCard(game.getGameBoard().getDeck().drawCard());
        } catch (EmptyDeckException e) {
            // nothing to do here
        }

        // swap playing player
        game.swapPlayingPlayerType();

        // return game
        return game;
    }

    private void reclaim(final Game g) {
        for (final Milestone m : g.getGameBoard().getMilestones()) {
            m.reclaim(PlayerType.TWO, g.getGameBoard().getCardsNotYetPlayed());
        }
    }

    protected abstract GameResult play(final List<Milestone> milestoneList, final Hand hand);

}

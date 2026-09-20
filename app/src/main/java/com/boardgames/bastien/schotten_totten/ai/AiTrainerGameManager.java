package com.boardgames.bastien.schotten_totten.ai;

import com.boradgames.bastien.schotten_totten.core.controllers.SimpleGameManager;
import com.boradgames.bastien.schotten_totten.core.exceptions.EmptyDeckException;
import com.boradgames.bastien.schotten_totten.core.exceptions.GameCreationException;
import com.boradgames.bastien.schotten_totten.core.exceptions.HandFullException;
import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NotYourTurnException;
import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.util.List;

public class AiTrainerGameManager extends SimpleGameManager {

    public AiTrainerGameManager(String player1Name, String player2Name) throws GameCreationException {
        super(player1Name, player2Name);
    }

    private void play(final PlayingPlayerType player, int handCardIndex, int milestoneIndex) throws MilestoneSideMaxReachedException, HandFullException, NotYourTurnException, EmptyDeckException {
        for (final Milestone m :game.getGameBoard().getMilestones()) {
            super.reclaimMilestone(PlayingPlayerType.TWO, m.getId());
        }
        super.playerPlays(player, handCardIndex, milestoneIndex);
        super.swapPlayers();
    }

    public void player1Plays(int handCardIndex, int milestoneIndex) throws MilestoneSideMaxReachedException, HandFullException, NotYourTurnException, EmptyDeckException {
        play(PlayingPlayerType.ONE, handCardIndex, milestoneIndex);
    }

    public void player2Plays(int handCardIndex, int milestoneIndex) throws MilestoneSideMaxReachedException, HandFullException, NotYourTurnException, EmptyDeckException {
        play(PlayingPlayerType.TWO, handCardIndex, milestoneIndex);
    }

    public List<Milestone> getGameBoard() {
        return game.getGameBoard().getMilestones();
    }

    private List<Card> getHand(final PlayingPlayerType player) {
        return game.getPlayer(player).getHand().getCards();
    }

    public List<Card> getPlayer1Hand() {
        return game.getPlayer(PlayingPlayerType.ONE).getHand().getCards();
    }

    public List<Card> getPlayer2Hand() {
        return game.getPlayer(PlayingPlayerType.TWO).getHand().getCards();
    }

}

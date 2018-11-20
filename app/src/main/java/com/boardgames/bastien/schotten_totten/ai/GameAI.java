package com.boardgames.bastien.schotten_totten.ai;


import com.boradgames.bastien.schotten_totten.core.exceptions.EmptyDeckException;
import com.boradgames.bastien.schotten_totten.core.exceptions.HandFullException;
import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NotYourTurnException;
import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 27/03/2017.
 */

public abstract class GameAI {

    protected String name = this.getClass().getSimpleName();

    protected PlayingPlayerType playingPlayerType = PlayingPlayerType.TWO;

    public String getName() {
        return this.name;
    }

    protected class Indexes {
        private final int handIndex;
        private final int milestoneIndex;

        public Indexes(final int handIndex, final int milestoneIndex) {
            this.handIndex = handIndex;
            this.milestoneIndex = milestoneIndex;
        }

        public int getHandIndex() {
            return this.handIndex;
        }
        public int getMilestoneIndex() {
            return this.milestoneIndex;
        }
    }

    public void reclaimAndPlay(final AiGameManager gameManager) throws MilestoneSideMaxReachedException, HandFullException, NotYourTurnException {

        // reclaim
        reclaim(gameManager);

        // test if AI can play
        for (final Milestone m : gameManager.getMilestones()) {
            final List<Card> side = m.getPlayer2Side();
            final List<Card> hand = gameManager.getPlayingPlayer().getHand().getCards();
            if (side.size() < Milestone.MAX_CARDS_PER_SIDE && !hand.isEmpty()) {
                // play
                play(gameManager);
                break;
            }
        }

    }

    private void reclaim(AiGameManager gameManager) {
        try {
            for (final Milestone m :gameManager.getMilestones()) {
                gameManager.reclaimMilestone(PlayingPlayerType.TWO, m.getId());
            }
        } catch (final NotYourTurnException e) {
            // nothing to do
        }
    }

    private void play(final AiGameManager gameManager) throws MilestoneSideMaxReachedException, HandFullException, NotYourTurnException {

        final List<Card> hand = new ArrayList<>(gameManager.getPlayingPlayer().getHand().getCards());
        final List<Milestone> milestones = new ArrayList<>( gameManager.getMilestones());
        final Indexes indexes = handCardIndexAndMilestoneIndex(hand, milestones, gameManager.getCardsNotYetPlayed(), gameManager.getAllTheCards(), playingPlayerType);
        try {
            gameManager.playerPlays(PlayingPlayerType.TWO, indexes.getHandIndex(), indexes.getMilestoneIndex());
        } catch (EmptyDeckException e) {
            // nothing special to do
        }
    }

    protected int getStrongestCardIndex(final List<Card> hand) {
        int index = 0;
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getNumber().ordinal() > hand.get(index).getNumber().ordinal()) {
               index = i;
            }
        }
        return index;
    }

    protected int getWeakestCardIndex(final List<Card> hand) {
        int index = 0;
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getNumber().ordinal() < hand.get(index).getNumber().ordinal()) {
                index = i;
            }
        }
        return index;
    }

    protected abstract Indexes handCardIndexAndMilestoneIndex(
            final List<Card> hand, final List<Milestone> milestones,
            final List<Card> cardsNotYetPlayed, final List<Card> allTheCards, final PlayingPlayerType pType);


    // used to test if the card in parameter can be played on the milestone in parameter
    protected boolean tryToPlay(final Milestone milestoneTest, final Card cardToTest) {

        try {
            final Milestone mCopy = new Milestone(milestoneTest.getId());
            if (playingPlayerType == PlayingPlayerType.ONE) {
                for (final Card c : milestoneTest.getPlayer1Side()) {
                    mCopy.addCard(c, PlayingPlayerType.ONE);
                }
            } else {
                for (final Card c : milestoneTest.getPlayer2Side()) {
                    mCopy.addCard(c, PlayingPlayerType.TWO);
                }
            }
            mCopy.addCard(cardToTest, playingPlayerType);
        } catch (final MilestoneSideMaxReachedException e) {
            return false;
        }
        return true;

    }
}

package com.boardgames.bastien.schotten_totten.ai;


import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.MilestonePlayerType;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Bastien on 27/03/2017.
 */

public class GameAiImpl extends GameAI {

    public GameAiImpl(final PlayingPlayerType pType) {
        this.name = "Basic BaBa";
        this.playingPlayerType = pType;
    }

    protected Indexes handCardIndexAndMilestoneIndex(
            final List<Card> hand, final List<Milestone> milestones,
            final List<Card> cardsNotYetPlayed, final List<Card> allTheCards,  final PlayingPlayerType pType) {

        // try to complete a 1 or 2 card side with straight flush, 3 of a kind, or flush
        for (final Milestone m : milestones) {
            if (m.getCaptured().equals(MilestonePlayerType.NONE)) {
                final List<Card> opponent = m.getPlayer1Side();
                final List<Card> side = m.getPlayer2Side();
                if (opponent.size() != Milestone.MAX_CARDS_PER_SIDE && !side.isEmpty()) {
                    switch (side.size()) {
                        case 0:
                            break;
                        case 1:
                            for (final Card c : cardsNotYetPlayed) {
                                for (int i = 0; i < hand.size(); i++) {
                                    final List<Card> sideToTest = new ArrayList<>();
                                    sideToTest.addAll(side);
                                    sideToTest.add(hand.get(i));
                                    sideToTest.add(c);
                                    // try straight flush
                                    if (m.sideStrength(sideToTest) > 500) {
                                        return new Indexes(i, m.getId());
                                    }
                                }
                                for (int i = 0; i < hand.size(); i++) {
                                    final List<Card> sideToTest = new ArrayList<>();
                                    sideToTest.addAll(side);
                                    sideToTest.add(hand.get(i));
                                    sideToTest.add(c);
                                    // try 3 of a kind
                                    if (m.sideStrength(sideToTest) > 400) {
                                        return new Indexes(i, m.getId());
                                    }
                                }
                                for (int i = 0; i < hand.size(); i++) {
                                    final List<Card> sideToTest = new ArrayList<>();
                                    sideToTest.addAll(side);
                                    sideToTest.add(hand.get(i));
                                    sideToTest.add(c);
                                    // try flush
                                    if (m.sideStrength(sideToTest) > 300) {
                                        return new Indexes(i, m.getId());
                                    }
                                }
                            }
                            break;
                        case 2:
                            for (int i = 0; i < hand.size(); i++) {
                                final List<Card> sideToTest = new ArrayList<>();
                                sideToTest.addAll(side);
                                sideToTest.add(hand.get(i));
                                // try straight flush
                                if (m.sideStrength(sideToTest) > 500) {
                                    return new Indexes(i, m.getId());
                                }
                            }
                            for (int i = 0; i < hand.size(); i++) {
                                final List<Card> sideToTest = new ArrayList<>();
                                sideToTest.addAll(side);
                                sideToTest.add(hand.get(i));
                                // try 3 of a kind
                                if (m.sideStrength(sideToTest) > 400) {
                                    return new Indexes(i, m.getId());
                                }
                            }
                            for (int i = 0; i < hand.size(); i++) {
                                final List<Card> sideToTest = new ArrayList<>();
                                sideToTest.addAll(side);
                                sideToTest.add(hand.get(i));
                                // try flush
                                if (m.sideStrength(sideToTest) > 300) {
                                    return new Indexes(i, m.getId());
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }

        }

        // try to counter a 3 card opponent side
        for (final Milestone m : milestones) {
            if (m.getCaptured().equals(MilestonePlayerType.NONE)) {
                final List<Card> opponent = m.getPlayer1Side();
                final List<Card> side = m.getPlayer2Side();
                if ((opponent.size() == Milestone.MAX_CARDS_PER_SIDE) && (side.size() < Milestone.MAX_CARDS_PER_SIDE)) {
                    final int opponentStrength = m.sideStrength(opponent);
                    if (side.size() == Milestone.MAX_CARDS_PER_SIDE-1) {
                        // try to do something better
                        for (int i = 0; i < hand.size(); i++) {
                            final List<Card> sideToTest = new ArrayList<>();
                            sideToTest.addAll(side);
                            sideToTest.add(hand.get(i));
                            if (m.sideStrength(sideToTest) > opponentStrength) {
                                return new Indexes(i, m.getId());
                            }
                        }
                    } else if (side.size() == 1) {
                        // try to do something that could be better
                        for (int i = 0; i < hand.size(); i++) {
                            for (final Card c : cardsNotYetPlayed) {
                                final List<Card> sideToTest = new ArrayList<>();
                                sideToTest.addAll(side);
                                sideToTest.add(hand.get(i));
                                sideToTest.add(c);
                                if (m.sideStrength(sideToTest) > opponentStrength) {
                                    return new Indexes(i, m.getId());
                                }
                            }
                        }
                    }
                }
            }
        }

        // try to do a better combination
        for (final Milestone m : milestones) {
            if (m.getCaptured().equals(MilestonePlayerType.NONE)) {
                final List<Card> opponent = m.getPlayer1Side();
                final List<Card> side = m.getPlayer2Side();
                if (!opponent.isEmpty() && (side.size() == 1)) {
                    // try to do something better
                    for (final Card c : cardsNotYetPlayed) {
                        for (int i = 0; i < hand.size(); i++) {
                            final List<Card> sideToTest = new ArrayList<>();
                            sideToTest.addAll(side);
                            sideToTest.add(hand.get(i));
                            sideToTest.add(c);
                            final int sideToCompareStrength = m.sideStrength(sideToTest);
                            final Iterator<int[]> combinationsIterator =
                                    CombinatoricsUtils.combinationsIterator(cardsNotYetPlayed.size(), Milestone.MAX_CARDS_PER_SIDE - opponent.size());
                            while(combinationsIterator.hasNext()){
                                final List<Card> newCombination = new ArrayList(opponent);
                                for (final int index : combinationsIterator.next()) {
                                    newCombination.add(cardsNotYetPlayed.get(index));
                                }
                                if (sideToCompareStrength > m.sideStrength(newCombination)) {
                                    return new Indexes(i, m.getId());
                                }
                            }
                        }
                    }
                }
            }
        }

        // put strongest card somewhere empty
        for (final Milestone m : milestones) {
            if (m.getCaptured().equals(MilestonePlayerType.NONE) && m.getPlayer2Side().isEmpty()) {
                return new Indexes(getStrongestCardIndex(hand), m.getId());
            }
        }

        // put weakest card somewhere not full
        for (final Milestone m : milestones) {
            if (m.getCaptured().equals(MilestonePlayerType.NONE) && (m.getPlayer2Side().size() != Milestone.MAX_CARDS_PER_SIDE)) {
                return new Indexes(getWeakestCardIndex(hand), m.getId());
            }
        }

        // cannot play, should not occur
        return null;
    }

}

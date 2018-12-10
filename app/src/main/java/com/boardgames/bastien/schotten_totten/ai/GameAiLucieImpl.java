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

public class GameAiLucieImpl extends GameAI {

    public GameAiLucieImpl(final PlayingPlayerType pType) {
        this.playingPlayerType = pType;
    }

    public String getName() {
        return "Lilouhbe (Advanced)";
    }

    protected Indexes handCardIndexAndMilestoneIndex(
            final List<Card> hand, final List<Milestone> milestones,
            final List<Card> cardsNotYetPlayed, final List<Card> allTheCards,  final PlayingPlayerType pType) {


        // if straight flush in hand
        for (int i = 0; i < hand.size(); i++) {
            final Card c = hand.get(i);
            // find same color but number +1
            for (int j = 0; j < hand.size(); j++) {
                if (hand.get(j).getColor().equals(c.getColor()) && (hand.get(j).getNumber().ordinal() == (c.getNumber().ordinal() + 1))) {
                    // find same color but number -1
                    for (int k = 0; k < hand.size(); k++) {
                        if (hand.get(k).getColor().equals(c.getColor()) && (hand.get(k).getNumber().ordinal() == (c.getNumber().ordinal() - 1))) {

                            final List<Card> sideToTest = new ArrayList<>();
                            sideToTest.add(hand.get(i));
                            sideToTest.add(hand.get(j));
                            sideToTest.add(hand.get(k));

                            try {
                                return testCombinationFromTheMiddleMilestone(milestones, cardsNotYetPlayed, sideToTest, k);
                            } catch (NoIndexFoundException e) {
                                // nothing special to do
                            }
                        }
                    }
                }
            }
        }

        // if 3 of a kind in hand
        for (int i = 0; i < hand.size(); i++) {
            final Card c = hand.get(i);
            // find same number but other color
            for (int j = 0; j < hand.size(); j++) {
                if (!hand.get(j).getColor().equals(c.getColor()) && (hand.get(j).getNumber().ordinal() == c.getNumber().ordinal())) {
                    // find same number but other color
                    for (int k = 0; k < hand.size(); k++) {
                        if (!hand.get(k).getColor().equals(hand.get(j).getColor())
                                && !hand.get(k).getColor().equals(c.getColor())
                                && (hand.get(k).getNumber().ordinal() == hand.get(j).getNumber().ordinal())
                                && (hand.get(k).getNumber().ordinal() == c.getNumber().ordinal())) {

                            final List<Card> sideToTest = new ArrayList<>();
                            sideToTest.add(hand.get(i));
                            sideToTest.add(hand.get(j));
                            sideToTest.add(hand.get(k));

                            try {
                                return testCombinationFromTheMiddleMilestone(milestones, cardsNotYetPlayed, sideToTest, k);
                            } catch (NoIndexFoundException e) {
                                // nothing special to do
                            }

                        }
                    }
                }
            }
        }

        // try to complete a straight flush, then a 3 of a kind (if a side has already 1 or 2 cards)
        int pimPamIndex = milestones.size()/2;
        for (int im = 0; im < milestones.size(); im++) {

            // do the pim pam
            if (im % 2 == 0) {
                pimPamIndex = pimPamIndex + im;
            } else {
                pimPamIndex = pimPamIndex - im;
            }

            // milestone to test
            final Milestone m = milestones.get(pimPamIndex);
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
                            break;
                        default:
                            break;
                    }
                }
            }

        }

        // put strongest card somewhere empty doing the pim pam from the edges
        int pimPamIndex2 = 0;
        for (int im = milestones.size()-1; im > 0; im--) {

            // do the pim pam from the edges
            if (im % 2 == 0) {
                pimPamIndex2 = pimPamIndex2 + im;
            } else {
                pimPamIndex2 = pimPamIndex2 - im;
            }

            // milestone to test
            final Milestone m = milestones.get(pimPamIndex2);
            if (m.getCaptured().equals(MilestonePlayerType.NONE) && m.getPlayer2Side().isEmpty()) {
                return new Indexes(getStrongestCardIndex(hand), m.getId());
            }
        }

        // desperate case ....
        // put weakest card somewhere not full
        for (final Milestone m : milestones) {
            if (m.getCaptured().equals(MilestonePlayerType.NONE) && (m.getPlayer2Side().size() != Milestone.MAX_CARDS_PER_SIDE)) {
                return new Indexes(getWeakestCardIndex(hand), m.getId());
            }
        }

        // cannot play, should not occur
        return null;
    }

    private Indexes testCombinationFromTheMiddleMilestone(final List<Milestone> milestones, final List<Card> cardsNotYetPlayed,  final List<Card> sideToTest, final int handCardIndex) throws NoIndexFoundException {
        // find a milestone in the middle or around
        int pimPamIndex = milestones.size()/2;
        for (int m = 0; m < milestones.size(); m++) {

            // do the pim pam
            if (m%2 == 0) {
                pimPamIndex = pimPamIndex + m;
            } else {
                pimPamIndex = pimPamIndex - m;
            }

            // milestone to test
            final Milestone milestoneToTry = milestones.get(pimPamIndex);

            if (milestoneToTry.getPlayer2Side().size() != Milestone.MAX_CARDS_PER_SIDE) {
                // complex algorithm to compare a combination with all the possible combinations on the opponent side
                final List<Card> opponentSide = milestoneToTry.getPlayer1Side();

                final int sideToCompareStrength = milestoneToTry.sideStrength(sideToTest);
                final Iterator<int[]> combinationsIterator =
                        CombinatoricsUtils.combinationsIterator(cardsNotYetPlayed.size(), Milestone.MAX_CARDS_PER_SIDE - opponentSide.size());
                while(combinationsIterator.hasNext()){
                    final List<Card> newCombination = new ArrayList(opponentSide);
                    for (final int index : combinationsIterator.next()) {
                        newCombination.add(cardsNotYetPlayed.get(index));
                    }
                    if (sideToCompareStrength > milestoneToTry.sideStrength(newCombination)) {
                        // if the milestone is empty AND no overall combination (including me hand) can beat this one,
                        // put the weakest card of the combination
                        return new Indexes(handCardIndex, pimPamIndex);
                    }
                }
            }
        }
        throw new NoIndexFoundException();
    }

    private class NoIndexFoundException extends Exception {
    }
}

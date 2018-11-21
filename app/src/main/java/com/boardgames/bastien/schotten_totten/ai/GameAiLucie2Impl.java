package com.boardgames.bastien.schotten_totten.ai;


import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.MilestonePlayerType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Created by Lucie on 21/11/2018.
 */

public class GameAiLucie2Impl extends GameAI {

    @JsonIgnore
    private final int MAX_MILESTONES = 9;

    protected Indexes handCardIndexAndMilestoneIndex(final List<Card> hand, final List<Milestone> milestones, final List<Card> cardsNotYetPlayed, final List<Card> allTheCards) {


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

    public ArrayList<Integer> random_indexes() {
        // Return randomly a list of 9 indexes
        ArrayList<Integer> indexes_chaos = new ArrayList<Integer>();
        for (int m = 0; MAX_MILESTONES > m; m++) {
            indexes_chaos.add(m);
        }
        Collections.shuffle(indexes_chaos, new Random(System.currentTimeMillis()));
        return indexes_chaos;
    }

    public ArrayList<Integer> random_neighbours() {
        // Return randomly -1 or 1
        ArrayList<Integer> neighbours_chaos = new ArrayList<Integer>();
        neighbours_chaos.add(1);
        neighbours_chaos.add(-1);
        Collections.shuffle(neighbours_chaos, new Random(System.currentTimeMillis()));
        return neighbours_chaos;
    }

    public ArrayList<Integer> milestonesOrder(final List<Milestone> milestones) {
        // sort the milestones by interest
        final LinkedHashSet<Integer> milestonesOrder = new LinkedHashSet<Integer>();
        final ArrayList<Integer> milestonesWith0Card = new ArrayList<Integer>();
        final ArrayList<Integer> milestonesWith1Card = new ArrayList<Integer>();
        final ArrayList<Integer> milestonesWith2Cards = new ArrayList<Integer>();

        // Adding chaos at the begging
        final ArrayList<Integer> indexes_chaos = random_indexes();

        // Adding chaos into the way to look neighbours
        final ArrayList<Integer> neighbours = random_neighbours();

        //  Top priority if the milestone is not full on my side
        for (int index  : indexes_chaos) {
            // First look to the milestone close to the ones already captured
            if (milestones.get(index).getPlayer2Side().size() < Milestone.MAX_CARDS_PER_SIDE) {
                if (8 > index & index > 0) {
                    for (int i : neighbours) {
                        if (milestones.get(index + i).getPlayer2Side().size() == Milestone.MAX_CARDS_PER_SIDE) {
                            milestonesOrder.add(index);
                        }
                    }
                } else if (index > 0) {
                    if (milestones.get(index - 1).getPlayer2Side().size() == Milestone.MAX_CARDS_PER_SIDE) {
                        milestonesOrder.add(index);
                    }
                } else {
                    if (milestones.get(index + 1).getPlayer2Side().size() == Milestone.MAX_CARDS_PER_SIDE) {
                        milestonesOrder.add(index);
                    }
                }
            }
        }
        // faire le tri sur milestonesOrder
        // faire le tri sur ndex_chaos.clone().removeAll(milestonesOrder)

        milestonesOrder.addAll(indexes_chaos);

        for (int index : milestonesOrder) {
            // First look to the milestone close to the ones already captured
            if (milestones.get(index).getPlayer2Side().size() < Milestone.MAX_CARDS_PER_SIDE) {
                switch (milestones.get(index).getPlayer2Side().size()) {
                    case 0:
                        milestonesWith0Card.add(index);
                        break;
                    case 1:
                        milestonesWith1Card.add(index);
                        break;
                    case 2:
                        milestonesWith2Cards.add(index);
                        break;
                }
            }
        }
        milestonesOrder.clear();

        final ArrayList<Integer> milestonesOrderResult = new ArrayList<Integer>();
        milestonesOrderResult.addAll(milestonesWith2Cards);
        milestonesOrderResult.addAll(milestonesWith1Card);
        milestonesOrderResult.addAll(milestonesWith0Card);

        //System.out.println("Final results");
        //System.out.println(milestonesOrder);
        return milestonesOrderResult;
    }
    //System.out.println();

    private class NoIndexFoundException extends Exception {
    }
}

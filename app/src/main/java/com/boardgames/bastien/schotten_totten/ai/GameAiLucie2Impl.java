package com.boardgames.bastien.schotten_totten.ai;



import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Hand;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.MilestonePlayerType;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Lucie on 21/11/2018.
 */

public class GameAiLucie2Impl extends GameAI {

    @JsonIgnore
    private final int MAX_MILESTONES = 9;

    public GameAiLucie2Impl(final PlayingPlayerType pType) {
        this.name = "Basic Lucie";
        this.playingPlayerType = pType;
    }


    protected class Combination {
        private final List<Card> cards;
        private int strenght;
        private int proba;

        public Combination(final List<Card> cards  ) {
            this.cards = cards;

            this.strenght = Milestone.sideStrength(cards);
            this.proba = -1;
        }

        public List<Card> getCards() {
            return this.cards;
        }
        public int getStrenght() {
            return this.strenght;
        }
        public int getProba() {
            return this.proba;
        }
        public void setProba(int proba){this.proba=proba;}

    }

    private int getHandCardIndex(Combination combination, Hand myHand) throws NoIndexFoundException {

        List<Card> list = new ArrayList<Card>();

        for(int i=0; i<myHand.getCards().size();i++) {
            if (combination.getCards().contains(myHand.getCards().get(i))) {
                return i;
            }
        }
        throw new NoIndexFoundException();
    }

    /**
     * Return the combination with the highest strenght reachable for a given player on a given milestone, regarding the remaining cards
     * @param playedCards
     * @param availableCards all the remaining cards (could be cards not played, cards not played + current hands, etc)
     * @return the highest strenght combination
     */
    private Combination getBestPossibleCombination(List<Card> playedCards, List<Card> availableCards) {

        // List<Card> playedCards = PlayingPlayerType.ONE.equals(pType) ? milestone.getPlayer1Side() : milestone.getPlayer2Side();
        Combination bestCombination=null;

        //case where 3 cards has already been played
        if (playedCards.size()==Milestone.MAX_CARDS_PER_SIDE) {
            return new Combination(playedCards);
        }

        final Iterator<int[]> combinationsIterator =
                CombinatoricsUtils.combinationsIterator(availableCards.size(), Milestone.MAX_CARDS_PER_SIDE - playedCards.size());
        while(combinationsIterator.hasNext()){
            int[] tuple = combinationsIterator.next();

            final List<Card> newCombination = new ArrayList(playedCards);
            for (final int index : tuple) {
                newCombination.add(availableCards.get(index)); //a tester, itération sur des int[] plutot que sur int?
            }
            Combination currentCombination = new Combination(newCombination);
            if (bestCombination==null || currentCombination.getStrenght()>bestCombination.getStrenght()) {
                bestCombination=currentCombination;
            }
            if (bestCombination.getStrenght()==524) {
                return bestCombination;
            }
        }
        return bestCombination;

    }



    protected class Analyser {
        private final Milestone milestone;
        private final Hand hand;

        public Analyser(final Milestone milestone, final Hand hand) {
            this.milestone = milestone;
            this.hand = hand;
        }

        public int getMilestoneIndex() {
            return this.milestone.getId();
        }
    }

    protected int handCardIndex(final List<Card> hand, final Milestone milestone, final ArrayList<Card> cardsNotYetPlayed) throws NoIndexFoundException {

        //First, get the best opponent strenght reachable
        ArrayList<Card> cardsLeft = (ArrayList<Card>) cardsNotYetPlayed.clone();
        cardsLeft.removeAll(hand);
        Combination bestOpponentPossibleCombination = this.getBestPossibleCombination(milestone.getPlayer1Side(), cardsLeft);

        //Then, find the first combination that beat this
        for (int index=0;index<hand.size();index++) {
            Card c = hand.get(index);

            //creation of the hypotetical hand : current milestone cards + one hand card
            List<Card> hypotheticPlayedCards = milestone.getPlayer2Side();
            hypotheticPlayedCards.add(c);

            //build cardNotYetPlayed without hypotetic card
            cardsLeft = (ArrayList<Card>) cardsNotYetPlayed.clone();
            cardsLeft.remove(c);

            Combination combination = this.getBestPossibleCombination(hypotheticPlayedCards, cardsLeft);

            if (combination.getStrenght()>bestOpponentPossibleCombination.getStrenght()) {
                return index;
            }
        }
        throw new NoIndexFoundException();
    }


    protected Indexes handCardIndexAndMilestoneIndex(final List<Card> hand, final List<Milestone> milestones, final List<Card> cardsNotYetPlayed, final List<Card> allTheCards,  final PlayingPlayerType pType) {

        ArrayList<Integer> milestonesOrder = this.milestonesOrder(milestones);
        for(int milestoneIndex : milestonesOrder ) {
            try{
                int cardIndex = this.handCardIndex(hand, milestones.get(milestoneIndex), (ArrayList<Card>) cardsNotYetPlayed);
                return new Indexes(cardIndex,milestoneIndex);
            } catch (NoIndexFoundException e) {
                //nothing to do, just iterate
            }
        }
        return new Indexes(getWeakestCardIndex(hand), milestonesOrder.get(milestonesOrder.size()-1));
    }


    private Indexes testCombinationfinal(final List<Milestone> milestones, final Hand hand, final List<Card> cardsNotYetPlayed, int strenghtToBeat) throws NoIndexFoundException {
        // Loop over milestones in the desired order
        for (Milestone milestoneToTry : milestones) {
            if (milestoneToTry.getPlayer2Side().size() != Milestone.MAX_CARDS_PER_SIDE) {
                int handCardIndex = findStrongestCardInHand(milestoneToTry, hand, cardsNotYetPlayed, strenghtToBeat);
                return new Indexes(handCardIndex, milestoneToTry.getId());
                }
            }
        throw new NoIndexFoundException();
    }

    private int findStrongestCardInHand(final Milestone milestoneToTry, final Hand hand, final List<Card> cardsNotYetPlayed, int strenghtToBeat) throws NoIndexFoundException {
        int indexToPlay = 0;

        if (milestoneToTry.getPlayer2Side().size() != Milestone.MAX_CARDS_PER_SIDE) {
            // boucler sur les cartes de ma main
            for (int index = 0; index < hand.getHandSize(); index++) {
                // initializing the milestone
                final List<Card> oneSide = milestoneToTry.getPlayer2Side();
                // playing the card
                final Card cardToCheck = hand.playCard(index);
                oneSide.add(cardToCheck);

                List<Card> cardsAvailable = cardsNotYetPlayed; // TODO : BLE check
                cardsAvailable.addAll(hand.getCards());

                // Boucler sur toutes les combinaisons possibles

                final Iterator<int[]> combinationsIterator =
                        CombinatoricsUtils.combinationsIterator(cardsAvailable.size(), Milestone.MAX_CARDS_PER_SIDE - oneSide.size());
                while(combinationsIterator.hasNext()){
                    final List<Card> newCombination = new ArrayList(oneSide);
                    for (final int i : combinationsIterator.next()) {
                        newCombination.add(cardsNotYetPlayed.get(i));
                    }
                    // if the combinaison has a higher strenght thant the one to beat
                    if (milestoneToTry.sideStrength(newCombination) > strenghtToBeat) {
                        strenghtToBeat = milestoneToTry.sideStrength(newCombination);
                        indexToPlay = index;

                        // TODO : tester les proba d'une telle combinaison
                    }
                }
                // unplaying the card
                //hand.addCard(cardToCheck, index);
            }
            if (indexToPlay > 0) {
                return indexToPlay;
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
        // add chaos
        ArrayList<Integer> indexes = new ArrayList<Integer>();
        for (int i = 0; i < MAX_MILESTONES; i++) {
            indexes.add(i);
        }
        Collections.shuffle(milestones, new Random(System.currentTimeMillis()));

        // sort the milestones by number of cards already played
        Collections.sort(milestones, new Comparator<Milestone>() {
            @Override
            public int compare(Milestone milestone1, Milestone milestone2) {
                int t = milestone2.getPlayer2Side().size()-milestone1.getPlayer2Side().size();
                if (t == 0) {
                    return milestone2.getPlayer1Side().size()-milestone1.getPlayer1Side().size();
                }
                return t;
            }
        });
        System.out.println(milestones);
        final ArrayList<Integer> milestonesOrder = new ArrayList<Integer>();
        for (Milestone m : milestones) {
            if (m.getPlayer2Side().size() < 3) {
                milestonesOrder.add(m.getId());
            }
        }
        // TODO : First look to the milestone close to the ones already captured

        //System.out.println("Final results");
        //System.out.println(milestonesOrder);
        return milestonesOrder;
    }
    //System.out.println();

    private class NoIndexFoundException extends Exception {
    }
}
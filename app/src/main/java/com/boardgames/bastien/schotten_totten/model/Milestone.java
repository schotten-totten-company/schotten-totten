package com.boardgames.bastien.schotten_totten.model;

import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;

import org.apache.commons.math3.util.CombinatoricsUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Bastien on 29/11/2016.
 */

public class Milestone implements Serializable {

    private final int id;

    private final List<Card> player1Side;

    private final List<Card> player2Side;

    private MilestonePlayerType firstPlayerToReachMaxCardPerSide;

    private MilestonePlayerType captured;

    public final int MAX_CARDS_PER_SIDE = 3;

    public Milestone(final int id) {
        this.id = id;
        this.player1Side = new ArrayList<>(MAX_CARDS_PER_SIDE);
        this.player2Side = new ArrayList<>(MAX_CARDS_PER_SIDE);
        this.firstPlayerToReachMaxCardPerSide = MilestonePlayerType.NONE;
        this.captured = MilestonePlayerType.NONE;
    }

    public void addCard(final Card c, final PlayingPlayerType playingPlayerType) throws MilestoneSideMaxReachedException {
        switch (playingPlayerType) {
            case ONE:
                addCardOnPlayerSide(c, player1Side, MilestonePlayerType.ONE);
                break;
            case TWO:
                addCardOnPlayerSide(c, player2Side, MilestonePlayerType.TWO);
                break;
        }
    }

    public void checkSideSize(final PlayingPlayerType playingPlayerType) throws MilestoneSideMaxReachedException {
        if (playingPlayerType == PlayingPlayerType.ONE) {
            if (player1Side.size() == MAX_CARDS_PER_SIDE) {
                throw new MilestoneSideMaxReachedException(id);
            }
        } else {
            if (player2Side.size() == MAX_CARDS_PER_SIDE) {
                throw new MilestoneSideMaxReachedException(id);
            }
        }
    }

    private void addCardOnPlayerSide(final Card c, final List<Card> playerSide, final MilestonePlayerType milestonePlayerType) throws MilestoneSideMaxReachedException {
        if (playerSide.size() == MAX_CARDS_PER_SIDE) {
            throw new MilestoneSideMaxReachedException(id);
        } else {
            playerSide.add(c);
            if (firstPlayerToReachMaxCardPerSide.equals(MilestonePlayerType.NONE)
                    && playerSide.size() == MAX_CARDS_PER_SIDE) {
                firstPlayerToReachMaxCardPerSide = milestonePlayerType;
            }
        }
    }

    public boolean reclaim(final PlayingPlayerType playerType, final List<Card> cardsNotYetPlayed) {
        switch (playerType) {
            case ONE:
                final boolean player1Stronger = compareSideStrenght(player1Side, player2Side, playerType, cardsNotYetPlayed);
                if (player1Stronger) {
                    captured = MilestonePlayerType.ONE;
                }
                return player1Stronger;
            case TWO:
                final boolean player2Stronger = compareSideStrenght(player2Side, player1Side, playerType, cardsNotYetPlayed);
                if (player2Stronger) {
                    captured = MilestonePlayerType.TWO;
                }
                return player2Stronger;
        }
        return false; // normally not used
    }

    private boolean compareSideStrenght(final List<Card> sideToCompare,
                                        final List<Card> otherSide,
                                        final PlayingPlayerType playerType,
                                        final List<Card> cardsNotYetPlayed){
        if (sideToCompare.size() == MAX_CARDS_PER_SIDE) {
            final int sideToCompareStrength = sideStrength(sideToCompare);
            if (otherSide.size() == MAX_CARDS_PER_SIDE) {
                final int otherSideStrength = sideStrength(otherSide);
                if (sideToCompareStrength == otherSideStrength) {
                    return (playerType.equals(firstPlayerToReachMaxCardPerSide));
                } else {
                    return sideToCompareStrength > otherSideStrength;
                }
            } else {
                final Iterator<int[]> combinationsIterator =
                        CombinatoricsUtils.combinationsIterator(cardsNotYetPlayed.size(), MAX_CARDS_PER_SIDE - otherSide.size());
                while(combinationsIterator.hasNext()){
                    final List<Card> newCombination = new ArrayList(otherSide);
                    for (final int index : combinationsIterator.next()) {
                        newCombination.add(cardsNotYetPlayed.get(index));
                    }
                    if (sideToCompareStrength < sideStrength(newCombination)) {
                        return false;
                    }
                }
                return true;
            }
        } else {
            return false;
        }
    }

    private int sideStrength(final List<Card> side) {
        if (side.size() != MAX_CARDS_PER_SIDE) {
            return 0;
        } else {
            final List<Integer> numbers = new ArrayList<>();
            int sum = 0;
            boolean isFlush = true;
            boolean is3OfKind = true;

            // check flush
            final Card.COLOR firstCardColor = side.get(0).getColor();
            for (final Card card : side) {
                if (!card.getColor().equals(firstCardColor)) {
                    isFlush = false;
                    break;
                }
            }

            // check 3 of a kind
            final Card.NUMBER firstCardNumber = side.get(0).getNumber();
            for (final Card card : side) {
                if (!card.getNumber().equals(firstCardNumber)) {
                    is3OfKind = false;
                    break;
                }
            }

            // check straight
            for (final Card card : side) {
                final int n = card.getNumber().ordinal();
                sum += n;
                numbers.add(n);
            }
            Collections.sort(numbers);
            boolean isStraight = true;
            for (int i = 0; i < numbers.size(); i++) {
                if (numbers.get(i) != (numbers.get(0) + i)) {
                    isStraight = false;
                    break;
                }
            }

            // return strength
            if (isStraight && isFlush) {
                // straight flush
                return sum + 500;
            } else if (is3OfKind) {
                // 3 of a kind
                return sum + 400;
            } else if (isFlush) {
                // flush
                return sum + 300;
            } else if (isStraight) {
                // straight
                return sum + 200;
            } else {
                // wild hand
                return sum + 100;
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public MilestonePlayerType getCaptured() {return this.captured;}

    public List<Card> getPlayer1Side() {
        return player1Side;
    }

    public List<Card> getPlayer2Side() {
        return player2Side;
    }
}

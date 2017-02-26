package com.boardgames.bastien.schotten_totten.model;

import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Bastien on 29/11/2016.
 */

public class Milestone {

    private final int id;


    private final List<Card> player1Side;

    private final List<Card> player2Side;

    private PlayerType firstPlayerToReachMaxCardPerSide;

    private PlayerType captured;

    public final int MAX_CARDS_PER_SIDE = 3;

    public Milestone(final int id) {
        this.id = id;
        this.player1Side = new ArrayList<>(MAX_CARDS_PER_SIDE);
        this.player2Side = new ArrayList<>(MAX_CARDS_PER_SIDE);
        this.firstPlayerToReachMaxCardPerSide = PlayerType.NONE;
        this.captured = PlayerType.NONE;
    }

    public void addCard(final Card c, final PlayerType playerType) throws MilestoneSideMaxReachedException {
        switch (playerType) {
            case ONE:
                addCardOnPlayerSide(c, player1Side, playerType);
                break;
            case TWO:
                addCardOnPlayerSide(c, player2Side, playerType);
                break;
            default:
                break;
        }
    }

    public void checkSideSize(final PlayerType playerType) throws MilestoneSideMaxReachedException {
        switch (playerType) {
            case ONE:
                if (player1Side.size() == MAX_CARDS_PER_SIDE) {
                    throw new MilestoneSideMaxReachedException(id);
                }
                break;
            case TWO:
                if (player2Side.size() == MAX_CARDS_PER_SIDE) {
                    throw new MilestoneSideMaxReachedException(id);
                }
                break;
            default:
                break;
        }
    }

    private void addCardOnPlayerSide(final Card c, final List<Card> playerSide, final PlayerType playerType) throws MilestoneSideMaxReachedException {
        if (playerSide.size() == MAX_CARDS_PER_SIDE) {
            throw new MilestoneSideMaxReachedException(id);
        } else {
            playerSide.add(c);
            if (firstPlayerToReachMaxCardPerSide.equals(PlayerType.NONE)
                    && playerSide.size() == MAX_CARDS_PER_SIDE) {
                firstPlayerToReachMaxCardPerSide = playerType;
            }
        }
    }

    public boolean reclaim(final PlayerType playerType) {
        switch (playerType) {
            case ONE:
                final boolean player1Stronger = compareSideStrenght(player1Side, player2Side, playerType);
                if (player1Stronger) {
                    captured = playerType;
                }
                return player1Stronger;
            case TWO:
                final boolean player2Stronger = compareSideStrenght(player2Side, player1Side, playerType);
                if (player2Stronger) {
                    captured = playerType;
                }
                return player2Stronger;
            default:
               return false;
        }
    }

    private boolean compareSideStrenght(final List<Card> sideToCompare, final List<Card> otherSide, final PlayerType playerType){
        if (sideToCompare.size() == MAX_CARDS_PER_SIDE) {
            if (otherSide.size() == MAX_CARDS_PER_SIDE) {
                final int sideToCompareStrength = sideStrength(sideToCompare);
                final int otherSideStrength = sideStrength(otherSide);
                if (sideToCompareStrength == otherSideStrength) {
                    return (playerType.equals(firstPlayerToReachMaxCardPerSide));
                } else {
                    return sideToCompareStrength > otherSideStrength;
                }
            } else {
                // TODO, very complex case
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
            Collections.reverse(numbers);
            boolean isStraight = false;
            for (int i = 1; i < numbers.size(); i++) {
                isStraight = (numbers.get(i) == numbers.get(0) - i);
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

    public PlayerType getCaptured() {return this.captured;}

    public List<Card> getPlayer1Side() {
        return player1Side;
    }

    public List<Card> getPlayer2Side() {
        return player2Side;
    }
}

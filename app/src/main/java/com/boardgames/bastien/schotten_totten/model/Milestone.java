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

    public final int MAX_CARDS_PER_SIDE = 3;

    public Milestone(final int id) {
        this.id = id;
        this.player1Side = new ArrayList<>(MAX_CARDS_PER_SIDE);
        this.player2Side = new ArrayList<>(MAX_CARDS_PER_SIDE);
        this.firstPlayerToReachMaxCardPerSide = PlayerType.NONE;
    }

    public void addCard(final Card c, final PlayerType playerType) throws MilestoneSideMaxReachedException {
        switch (playerType) {
            case ONE:
                addCardOnPlayer1Side(c, playerType);
                break;
            case TWO:
                addCardOnPlayer2Side(c, playerType);
                break;
            default:
                break;
        }
    }

    private void addCardOnPlayer1Side(final Card c, final PlayerType playerType) throws MilestoneSideMaxReachedException {
        if (player1Side.size() == MAX_CARDS_PER_SIDE) {
            throw new MilestoneSideMaxReachedException(id);
        } else {
            player1Side.add(c);
            if (firstPlayerToReachMaxCardPerSide == null && player1Side.size() == MAX_CARDS_PER_SIDE) {
                firstPlayerToReachMaxCardPerSide = playerType;
            }
        }
    }

    private void addCardOnPlayer2Side(final Card c, final PlayerType playerType) throws MilestoneSideMaxReachedException {
        if (player2Side.size() == MAX_CARDS_PER_SIDE) {
            throw new MilestoneSideMaxReachedException(id);
        } else {
            player2Side.add(c);
            if (firstPlayerToReachMaxCardPerSide == null && player2Side.size() == MAX_CARDS_PER_SIDE) {
                firstPlayerToReachMaxCardPerSide = playerType;
            }
        }
    }

    public boolean reclaim(final PlayerType playerType) {
        switch (playerType) {
            case ONE:
                if (player1Side.size() == MAX_CARDS_PER_SIDE) {
                    if (player2Side.size() == MAX_CARDS_PER_SIDE) {
                        final int player1SideStrength = sideStrength(player1Side);
                        final int player2SideStrength = sideStrength(player1Side);
                        if (player1SideStrength == player2SideStrength) {
                            return (playerType.equals(firstPlayerToReachMaxCardPerSide));
                        } else {
                            return player1SideStrength > player2SideStrength;
                        }
                    } else {
                        // TODO, very complex case
                        return false;
                    }
                } else {
                    return false;
                }
            case TWO:
                if (player2Side.size() == MAX_CARDS_PER_SIDE) {
                    if (player1Side.size() == MAX_CARDS_PER_SIDE) {
                        final int player1SideStrength = sideStrength(player1Side);
                        final int player2SideStrength = sideStrength(player1Side);
                        if (player1SideStrength == player2SideStrength) {
                            return (playerType.equals(firstPlayerToReachMaxCardPerSide));
                        } else {
                            return player2SideStrength > player1SideStrength;
                        }
                    } else {
                        // TODO, very complex case
                        return false;
                    }
                } else {
                    return false;
                }
            default:
               return false;
        }
    }

    private int sideStrength(final List<Card> side) {
        if (side.size() != MAX_CARDS_PER_SIDE) {
            return 0;
        } else {
            final List<Integer> numbers = new ArrayList<>();
            int sum = 0;
            boolean isFlush = false;
            boolean is3OfKind = false;
            final Card.COLOR firstCardColor = side.get(0).getColor();
            for (final Card c : side) {
                final int n = c.getNumber().ordinal();
                sum += n;
                numbers.add(n);
                isFlush = c.getColor().equals(firstCardColor);
            }
            Collections.reverse(numbers);
            final boolean isStraight = (numbers.get(0)-numbers.get(numbers.size()))== MAX_CARDS_PER_SIDE-1;

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

    public List<Card> getPlayer1Side() {
        return player1Side;
    }

    public List<Card> getPlayer2Side() {
        return player2Side;
    }
}

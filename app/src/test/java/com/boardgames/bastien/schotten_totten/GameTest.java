package com.boardgames.bastien.schotten_totten;

import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.MilestonePlayerType;
import com.boardgames.bastien.schotten_totten.model.PlayingPlayerType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Bastien on 15/01/2017.
 */

public class GameTest {

    private Game testGame;

    @Before
    public void before() throws HandFullException, EmptyDeckException, GameCreationException {
        testGame = new Game("p1", "p2");
    }

    @Test
    public void player1WinsWith3MilestonesInARow() throws MilestoneSideMaxReachedException, NoPlayerException {

        addThreeOfAKind(testGame.getGameBoard().getMilestones().get(0), Card.NUMBER.EIGHT, PlayingPlayerType.ONE);
        Assert.assertTrue(testGame.getGameBoard().getMilestones().get(0).reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
        Assert.assertFalse(testGame.getWinner().equals(MilestonePlayerType.ONE));
        Assert.assertTrue(testGame.getWinner().equals(MilestonePlayerType.NONE));

        addThreeOfAKind(testGame.getGameBoard().getMilestones().get(1), Card.NUMBER.FIVE, PlayingPlayerType.ONE);
        Assert.assertTrue(testGame.getGameBoard().getMilestones().get(1).reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
        Assert.assertFalse(testGame.getWinner().equals(MilestonePlayerType.ONE));
        Assert.assertTrue(testGame.getWinner().equals(MilestonePlayerType.NONE));

        addThreeOfAKind(testGame.getGameBoard().getMilestones().get(2), Card.NUMBER.ONE, PlayingPlayerType.ONE);
        Assert.assertTrue(testGame.getGameBoard().getMilestones().get(2).reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
        Assert.assertTrue(testGame.getWinner().equals(MilestonePlayerType.ONE));

    }

    @Test
    public void player1WinsWith3MilestonesInARow_2() throws MilestoneSideMaxReachedException, NoPlayerException {

        addThreeOfAKind(testGame.getGameBoard().getMilestones().get(0), Card.NUMBER.EIGHT, PlayingPlayerType.ONE);
        Assert.assertTrue(testGame.getGameBoard().getMilestones().get(0).reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
        Assert.assertFalse(testGame.getWinner().equals(MilestonePlayerType.ONE));
        Assert.assertTrue(testGame.getWinner().equals(MilestonePlayerType.NONE));

        addThreeOfAKind(testGame.getGameBoard().getMilestones().get(5), Card.NUMBER.ONE, PlayingPlayerType.ONE);
        Assert.assertTrue(testGame.getGameBoard().getMilestones().get(5).reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
        Assert.assertFalse(testGame.getWinner().equals(MilestonePlayerType.ONE));
        Assert.assertTrue(testGame.getWinner().equals(MilestonePlayerType.NONE));

        addThreeOfAKind(testGame.getGameBoard().getMilestones().get(4), Card.NUMBER.ONE, PlayingPlayerType.ONE);
        Assert.assertTrue(testGame.getGameBoard().getMilestones().get(4).reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
        Assert.assertFalse(testGame.getWinner().equals(MilestonePlayerType.ONE));
        Assert.assertTrue(testGame.getWinner().equals(MilestonePlayerType.NONE));

        addThreeOfAKind(testGame.getGameBoard().getMilestones().get(6), Card.NUMBER.ONE, PlayingPlayerType.ONE);
        Assert.assertTrue(testGame.getGameBoard().getMilestones().get(6).reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
        Assert.assertTrue(testGame.getWinner().equals(MilestonePlayerType.ONE));

    }

    private void addThreeOfAKind(final Milestone m, final Card.NUMBER number, final PlayingPlayerType pType) throws MilestoneSideMaxReachedException {
        final Card card1 = new Card(number, Card.COLOR.BLUE);
        final Card card2 = new Card(number, Card.COLOR.RED);
        final Card card3 = new Card(number, Card.COLOR.GREEN);
        m.addCard(card1, pType);
        m.addCard(card2, pType);
        m.addCard(card3, pType);
    }

}

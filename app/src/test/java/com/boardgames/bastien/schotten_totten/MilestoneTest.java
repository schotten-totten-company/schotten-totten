package com.boardgames.bastien.schotten_totten;

import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Bastien on 15/01/2017.
 */

public class MilestoneTest {

    private Milestone testMilestone;

    @Before
    public void before() {
        testMilestone = new Milestone(0);
    }

    @Test
    public void ThreeOfAKindTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.BLUE), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.RED), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.GREEN), PlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.GREY), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.CYAN), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.YELLOW), PlayerType.TWO);

        Assert.assertFalse(testMilestone.reclaim(PlayerType.TWO, new ArrayList<Card>()));
        Assert.assertTrue(testMilestone.reclaim(PlayerType.ONE, new ArrayList<Card>()));
    }

    @Test
    public void ThreeOfAKindVsFlushTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.BLUE), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.YELLOW), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.GREY), PlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.RED), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.RED), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.RED), PlayerType.TWO);

        Assert.assertTrue(testMilestone.reclaim(PlayerType.ONE, new ArrayList<Card>()));
    }

    @Test
    public void StraightTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.GREEN), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.BLUE), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.RED), PlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.CYAN), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.GREY), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.YELLOW), PlayerType.TWO);

        Assert.assertFalse(testMilestone.reclaim(PlayerType.TWO, new ArrayList<Card>()));
        Assert.assertTrue(testMilestone.reclaim(PlayerType.ONE, new ArrayList<Card>()));
    }

    @Test
    public void StraightFlushTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.GREEN), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.BLUE), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.RED), PlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.CYAN), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.CYAN), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.CYAN), PlayerType.TWO);

        Assert.assertTrue(testMilestone.reclaim(PlayerType.TWO, new ArrayList<Card>()));
        Assert.assertFalse(testMilestone.reclaim(PlayerType.ONE, new ArrayList<Card>()));
    }

    @Test
    public void FlushVsStraightFlushTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.YELLOW), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.YELLOW), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.YELLOW), PlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.FOUR, Card.COLOR.YELLOW), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.YELLOW), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.YELLOW), PlayerType.TWO);

        Assert.assertTrue(testMilestone.reclaim(PlayerType.ONE, new ArrayList<Card>()));
        Assert.assertFalse(testMilestone.reclaim(PlayerType.TWO, new ArrayList<Card>()));
    }

    @Test
    public void StraightFlushVs3OfAKindTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.RED), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.FIVE, Card.COLOR.RED), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.FOUR, Card.COLOR.RED), PlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.GREY), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.BLUE), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.CYAN), PlayerType.TWO);

        Assert.assertTrue(testMilestone.reclaim(PlayerType.ONE, new ArrayList<Card>()));
        Assert.assertFalse(testMilestone.reclaim(PlayerType.TWO, new ArrayList<Card>()));
    }

    @Test
    public void FlushTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.GREEN), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.BLUE), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.RED), PlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.YELLOW), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.YELLOW), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.FIVE, Card.COLOR.YELLOW), PlayerType.TWO);

        Assert.assertTrue(testMilestone.reclaim(PlayerType.TWO, new ArrayList<Card>()));
        Assert.assertFalse(testMilestone.reclaim(PlayerType.ONE, new ArrayList<Card>()));
    }

    @Test
    public void WildHandTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.GREEN), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.BLUE), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.RED), PlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.YELLOW), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.CYAN), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.FIVE, Card.COLOR.GREY), PlayerType.TWO);

        Assert.assertTrue(testMilestone.reclaim(PlayerType.TWO, new ArrayList<Card>()));
        Assert.assertFalse(testMilestone.reclaim(PlayerType.ONE, new ArrayList<Card>()));
    }

    @Test
    public void EqualityWildHandTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.GREEN), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.BLUE), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.RED), PlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.YELLOW), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.FOUR, Card.COLOR.CYAN), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.FIVE, Card.COLOR.GREY), PlayerType.TWO);

        Assert.assertTrue(testMilestone.reclaim(PlayerType.ONE, new ArrayList<Card>()));
        Assert.assertFalse(testMilestone.reclaim(PlayerType.TWO, new ArrayList<Card>()));
    }

    @Test
    public void WildHandTest_2() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.CYAN), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.BLUE), PlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.CYAN), PlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.GREY), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.GREY), PlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.GREEN), PlayerType.TWO);

        Assert.assertFalse(testMilestone.reclaim(PlayerType.ONE, new ArrayList<Card>()));
        Assert.assertTrue(testMilestone.reclaim(PlayerType.TWO, new ArrayList<Card>()));
    }
}

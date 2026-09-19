package com.boardgames.bastien.schotten_totten;

import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.model.Card;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * Created by Bastien on 15/01/2017.
 */

public class MilestoneTest {

    private Milestone testMilestone;

    @BeforeEach
    public void before() {
        testMilestone = new Milestone(0);
    }

    @Test
    public void ThreeOfAKindTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.BLUE), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.RED), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.GREEN), PlayingPlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.GREY), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.CYAN), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.YELLOW), PlayingPlayerType.TWO);

        Assertions.assertFalse(testMilestone.reclaim(PlayingPlayerType.TWO, new ArrayList<Card>()));
        Assertions.assertTrue(testMilestone.reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
    }

    @Test
    public void SameThreeOfAKindTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.GREY), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.RED), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.YELLOW), PlayingPlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.BLUE), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.CYAN), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.GREEN), PlayingPlayerType.TWO);

        Assertions.assertFalse(testMilestone.reclaim(PlayingPlayerType.TWO, new ArrayList<Card>()));
        Assertions.assertTrue(testMilestone.reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
    }

    @Test
    public void ThreeOfAKindVsFlushTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.BLUE), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.YELLOW), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.GREY), PlayingPlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.RED), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.RED), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.RED), PlayingPlayerType.TWO);

        Assertions.assertTrue(testMilestone.reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
    }

    @Test
    public void StraightTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.GREEN), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.BLUE), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.RED), PlayingPlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.CYAN), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.GREY), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.YELLOW), PlayingPlayerType.TWO);

        Assertions.assertFalse(testMilestone.reclaim(PlayingPlayerType.TWO, new ArrayList<Card>()));
        Assertions.assertTrue(testMilestone.reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
    }

    @Test
    public void StraightFlushTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.GREEN), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.BLUE), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.RED), PlayingPlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.CYAN), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.CYAN), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.CYAN), PlayingPlayerType.TWO);

        Assertions.assertTrue(testMilestone.reclaim(PlayingPlayerType.TWO, new ArrayList<Card>()));
        Assertions.assertFalse(testMilestone.reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
    }

    @Test
    public void FlushVsStraightFlushTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.YELLOW), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.YELLOW), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.YELLOW), PlayingPlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.FOUR, Card.COLOR.YELLOW), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.YELLOW), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.YELLOW), PlayingPlayerType.TWO);

        Assertions.assertTrue(testMilestone.reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
        Assertions.assertFalse(testMilestone.reclaim(PlayingPlayerType.TWO, new ArrayList<Card>()));
    }

    @Test
    public void StraightFlushVs3OfAKindTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.RED), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.FIVE, Card.COLOR.RED), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.FOUR, Card.COLOR.RED), PlayingPlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.GREY), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.BLUE), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.CYAN), PlayingPlayerType.TWO);

        Assertions.assertTrue(testMilestone.reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
        Assertions.assertFalse(testMilestone.reclaim(PlayingPlayerType.TWO, new ArrayList<Card>()));
    }

    @Test
    public void FlushTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.GREEN), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.BLUE), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.RED), PlayingPlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.YELLOW), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.YELLOW), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.FIVE, Card.COLOR.YELLOW), PlayingPlayerType.TWO);

        Assertions.assertTrue(testMilestone.reclaim(PlayingPlayerType.TWO, new ArrayList<Card>()));
        Assertions.assertFalse(testMilestone.reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
    }

    @Test
    public void WildHandTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.GREEN), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.BLUE), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.RED), PlayingPlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.YELLOW), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.CYAN), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.FIVE, Card.COLOR.GREY), PlayingPlayerType.TWO);

        Assertions.assertTrue(testMilestone.reclaim(PlayingPlayerType.TWO, new ArrayList<Card>()));
        Assertions.assertFalse(testMilestone.reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
    }

    @Test
    public void EqualityWildHandTest() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.ONE, Card.COLOR.GREEN), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.THREE, Card.COLOR.BLUE), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.RED), PlayingPlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.YELLOW), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.FOUR, Card.COLOR.CYAN), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.FIVE, Card.COLOR.GREY), PlayingPlayerType.TWO);

        Assertions.assertTrue(testMilestone.reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
        Assertions.assertFalse(testMilestone.reclaim(PlayingPlayerType.TWO, new ArrayList<Card>()));
    }

    @Test
    public void WildHandTest_2() throws MilestoneSideMaxReachedException {
        // play one
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.CYAN), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.EIGHT, Card.COLOR.BLUE), PlayingPlayerType.ONE);
        testMilestone.addCard(new Card(Card.NUMBER.TWO, Card.COLOR.CYAN), PlayingPlayerType.ONE);

        // player two
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.GREY), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.SEVEN, Card.COLOR.GREY), PlayingPlayerType.TWO);
        testMilestone.addCard(new Card(Card.NUMBER.SIX, Card.COLOR.GREEN), PlayingPlayerType.TWO);

        Assertions.assertFalse(testMilestone.reclaim(PlayingPlayerType.ONE, new ArrayList<Card>()));
        Assertions.assertTrue(testMilestone.reclaim(PlayingPlayerType.TWO, new ArrayList<Card>()));
    }
}

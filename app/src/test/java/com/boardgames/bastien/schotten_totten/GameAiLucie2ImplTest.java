package com.boardgames.bastien.schotten_totten;

import com.boardgames.bastien.schotten_totten.ai.GameAiLucie2Impl;
import com.boradgames.bastien.schotten_totten.core.exceptions.EmptyDeckException;
import com.boradgames.bastien.schotten_totten.core.exceptions.GameCreationException;
import com.boradgames.bastien.schotten_totten.core.exceptions.HandFullException;
import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.model.Game;
import com.boradgames.bastien.schotten_totten.core.model.Milestone;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Created by Bastien on 15/01/2017.
 */

public class GameAiLucie2ImplTest {

    private Game testGame;
    private GameAiLucie2Impl ai = new GameAiLucie2Impl();
    private final static int MAX_CARDS_PER_SIDE = 3;


    @Before
    public void before() throws HandFullException, EmptyDeckException, GameCreationException {
        testGame = new Game("p1", "ai_lb");
    }

    @Test
    public void testRandomIndexes() {
        // Test if the list contains 9 indexes at the begin of the game

        // Define set of milestones to be tested
        ArrayList<Integer> test = ai.random_indexes();

        // Define reference set of milestones
        ArrayList<Integer> reference = new ArrayList<Integer>();
        for (int m = 0; testGame.getGameBoard().MAX_MILESTONES > m; m++) {
            reference.add(m);
        }

        // Comparare if the list of milestones indexes is the one expected
        printLB(test, reference, "testRandomIndexes");
        assertEquals(test.size(), reference.size());
        Collections.sort(test);
        for (int m = 0; m < test.size(); m++) {
            assertEquals(reference.get(m), test.get(m));
        }
    }


    @Test
    public void testRandomNeighbours() {
        // Test if the list contains -1 and 1

        // Define set of milestones to be tested
        ArrayList<Integer> test = ai.random_neighbours();

        // Define reference set of milestones
        ArrayList<Integer> reference = new ArrayList<Integer>();
        reference.add(-1);
        reference.add(1);

        // Comparare if the list of milestones indexes is the one expected
        printLB(test, reference, "testRandomIndexes");
        assertEquals(test.size(), reference.size());
        Collections.sort(test);
        for (int m = 0; m < test.size(); m++) {
            assertEquals(reference.get(m), test.get(m));
        }
    }

    @Test
    public void testDefault() {
        // Test if the list contains 9 milestones at the begin of the game

        // Define set of milestones to be tested
        ArrayList<Integer> testOrder = ai.milestonesOrder(testGame.getGameBoard().getMilestones());

        // Define reference set of milestones
        ArrayList<Integer> referenceOrder = new ArrayList<Integer>();
        for (int m = 0; m < testGame.getGameBoard().MAX_MILESTONES; m++) {
            referenceOrder.add(m);
        }

        // Comparare if the list of milestones indexes is the one expected
        printLB(testOrder, referenceOrder, "TestDefault");
        assertEquals(testOrder.size(), referenceOrder.size());
    }

    @Test
    public void iterateOverFreeMilestones() {
        // Test if the list does not contain full milestone

        // Define set of milestones to be tested
        for (int m = 0; m < testGame.getGameBoard().MAX_MILESTONES ; m++) {
            if (m != 4) {
                addCards(testGame.getGameBoard().getMilestones().get(m), 3, PlayingPlayerType.TWO);
            }
        }
        ArrayList<Integer> testOrder = ai.milestonesOrder(testGame.getGameBoard().getMilestones());

        // Define reference set of milestones
        ArrayList<Integer> referenceOrder = new ArrayList<Integer>();
        referenceOrder.add(4);

        // Compare if the list of milestones indexes is the one expected
        printLB(testOrder, referenceOrder, "Test0");
        assertEquals(testOrder.size(), referenceOrder.size());
        for (int m = 0; m < testOrder.size(); m++) {
            assertEquals(referenceOrder.get(m), testOrder.get(m));
        }
    }

    @Test
    public void sortFreeMilestones() {
        // Test if the list does not contain full milestone

        // Define set of milestones to be tested1
        addCards(testGame.getGameBoard().getMilestones().get(0), 3, PlayingPlayerType.TWO);
        addCards(testGame.getGameBoard().getMilestones().get(1), 2, PlayingPlayerType.TWO);
        addCards(testGame.getGameBoard().getMilestones().get(2), 3, PlayingPlayerType.TWO);
        addCards(testGame.getGameBoard().getMilestones().get(3), 3, PlayingPlayerType.TWO);
        addCards(testGame.getGameBoard().getMilestones().get(4), 0, PlayingPlayerType.TWO);
        addCards(testGame.getGameBoard().getMilestones().get(5), 3, PlayingPlayerType.TWO);
        addCards(testGame.getGameBoard().getMilestones().get(6), 1, PlayingPlayerType.TWO);
        addCards(testGame.getGameBoard().getMilestones().get(7), 3, PlayingPlayerType.TWO);
        addCards(testGame.getGameBoard().getMilestones().get(8), 3, PlayingPlayerType.TWO);

        ArrayList<Integer> testOrder = ai.milestonesOrder(testGame.getGameBoard().getMilestones());

        // Define reference set of milestones
        ArrayList<Integer> referenceOrder = new ArrayList<Integer>();
        referenceOrder.add(1);
        referenceOrder.add(6);
        referenceOrder.add(4);

        // Compare if the list of milestones indexes is the one expected
        printLB(testOrder, referenceOrder, "Test1");
        assertEquals(testOrder.size(), referenceOrder.size());
        for (int m = 0; m < testOrder.size(); m++) {
            assertEquals(referenceOrder.get(m), testOrder.get(m));
        }
    }

    @Test
    public void chooseNextToFullLeft() {
        // Test if the list does not contain full milestone

        // Define set of milestones to be tested1
        addCards(testGame.getGameBoard().getMilestones().get(0), 3, PlayingPlayerType.TWO);

        ArrayList<Integer> testOrder = ai.milestonesOrder(testGame.getGameBoard().getMilestones());

        // Define reference set of milestones
        ArrayList<Integer> referenceOrder = new ArrayList<Integer>();
        referenceOrder.add(1);

        // Compare if the list of milestones indexes is the one expected
        printLB(testOrder, referenceOrder, "Test2");
        assertEquals(testOrder.size(), 8);
        assertEquals(referenceOrder.get(0), testOrder.get(0));
    }

    @Test
    public void chooseNextToFullRight() {
        // Test if the list does not contain full milestone

        // Define set of milestones to be tested1
        addCards(testGame.getGameBoard().getMilestones().get(8), 3, PlayingPlayerType.TWO);

        ArrayList<Integer> testOrder = ai.milestonesOrder(testGame.getGameBoard().getMilestones());

        // Define reference set of milestones
        ArrayList<Integer> referenceOrder = new ArrayList<Integer>();
        referenceOrder.add(7);

        // Compare if the list of milestones indexes is the one expected
        printLB(testOrder, referenceOrder, "Test3");
        assertEquals(testOrder.size(), 8);
        assertEquals(referenceOrder.get(0), testOrder.get(0));
    }

    @Test
    public void chooseNextToFullMiddle() {
        // Test if the list does not contain full milestone

        // Define set of milestones to be tested1
        addCards(testGame.getGameBoard().getMilestones().get(1), 3, PlayingPlayerType.TWO);

        ArrayList<Integer> testOrder = ai.milestonesOrder(testGame.getGameBoard().getMilestones());

        // Define reference set of milestones
        ArrayList<Integer> referenceOrder = new ArrayList<Integer>();
        referenceOrder.add(0);
        referenceOrder.add(2);

        // Compare if the list of milestones indexes is the one expected
        printLB(testOrder, referenceOrder, "Test4");
        assertEquals(testOrder.size(), 8);
        ArrayList<Integer> testOrder_cut = new ArrayList<Integer>();
        testOrder_cut.add(testOrder.get(0));
        testOrder_cut.add(testOrder.get(1));
        Collections.sort(testOrder_cut);
        assertEquals(referenceOrder.get(0), testOrder_cut.get(0));
        assertEquals(referenceOrder.get(1), testOrder_cut.get(1));
    }

    @Test
    public void chooseNextToFullRight2() {
        // Test if the list does not contain full milestone

        // Define set of milestones to be tested1
        addCards(testGame.getGameBoard().getMilestones().get(1), 3, PlayingPlayerType.TWO);
        addCards(testGame.getGameBoard().getMilestones().get(0), 3, PlayingPlayerType.TWO);

        ArrayList<Integer> testOrder = ai.milestonesOrder(testGame.getGameBoard().getMilestones());

        // Define reference set of milestones
        ArrayList<Integer> referenceOrder = new ArrayList<Integer>();
        referenceOrder.add(2);

        // Compare if the list of milestones indexes is the one expected
        printLB(testOrder, referenceOrder, "Test5");
        assertEquals(testOrder.size(), 7);
        assertEquals(referenceOrder.get(0), testOrder.get(0));
    }
    private void addCards(Milestone milestone, final int number, final PlayingPlayerType pType) {
        for (int m = 0; m < number; m++) {
            // BLE tu peux me dire comment mieux gerer cette exception
            try {
                milestone.addCard(testGame.getGameBoard().getDeck().drawCard(), pType);
            } catch (EmptyDeckException | MilestoneSideMaxReachedException e) {
                // nothing special to do
            }
        }
    }


    private void printLB(final ArrayList<Integer> testOrder, ArrayList<Integer> referenceOrder, String test_name) {
        System.out.println(test_name);
        System.out.println("Test:");
        System.out.println(testOrder);
        System.out.println("Ref:");
        System.out.println(referenceOrder);
    }
}


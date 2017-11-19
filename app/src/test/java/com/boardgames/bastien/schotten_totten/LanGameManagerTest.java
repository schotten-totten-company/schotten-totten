package com.boardgames.bastien.schotten_totten;

import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.exceptions.NotYourTurnException;
import com.boardgames.bastien.schotten_totten.model.PlayingPlayerType;
import com.boardgames.bastien.schotten_totten.server.LanGameManager;
import com.boardgames.bastien.schotten_totten.server.RestGameClient;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by Bastien on 19/11/2017.
 */

public class LanGameManagerTest {

    private static LanGameManager manager;
    private final RestGameClient restGameClient = new RestGameClient("http://localhost:8080");

    @BeforeClass
    public static void Before() {
        manager = new LanGameManager();
        manager.start();
    }

    @AfterClass
    public static void After() {
        manager.stop();
    }

    @Test
    public void TestPing() {
        System.out.println(restGameClient.ping());
    }

    @Test
    public void TestCreateGame() {
        Assert.assertTrue(restGameClient.createGame());
        System.out.println(restGameClient.getPlayingPlayer().getName());
    }

    @Test
    public void TestGetPlayers() {
        Assert.assertTrue(restGameClient.createGame());
        System.out.println(restGameClient.getPlayingPlayer().getName());
        System.out.println(restGameClient.getPlayer(PlayingPlayerType.TWO).getName());
    }

    @Test
    public void TestGetWinner() {
        Assert.assertTrue(restGameClient.createGame());
        try {
            System.out.println(restGameClient.getWinner().getName());
            Assert.fail("no winner shall be found");
        } catch (NoPlayerException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestReclaim() {
        Assert.assertTrue(restGameClient.createGame());
        System.out.println(restGameClient.getPlayingPlayer().getName());
        try {
            Assert.assertFalse(restGameClient.reclaimMilestone(PlayingPlayerType.ONE, 0));
        } catch (NotYourTurnException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void TestPlay() {
        Assert.assertTrue(restGameClient.createGame());
        System.out.println(restGameClient.getPlayingPlayer().getName());
        try {
            Assert.assertTrue(restGameClient.playerPlays(PlayingPlayerType.ONE, 0, 0));
        } catch (NotYourTurnException e) {
            Assert.fail(e.getMessage());
        } catch (MilestoneSideMaxReachedException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void TestMilestones() {
        Assert.assertTrue(restGameClient.createGame());
        System.out.println(restGameClient.getPlayingPlayer().getName());
        try {
            Assert.assertTrue(restGameClient.playerPlays(PlayingPlayerType.ONE, 0, 0));
            Assert.assertEquals(restGameClient.getMilestones().get(0).getPlayer1Side().size(), 1);
        } catch (NotYourTurnException e) {
            Assert.fail(e.getMessage());
        } catch (MilestoneSideMaxReachedException e) {
            Assert.fail(e.getMessage());
        }
    }


}

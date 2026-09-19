package com.boardgames.bastien.schotten_totten;

import android.util.Log;

import com.boardgames.bastien.schotten_totten.server.RestGameClient;
import com.boradgames.bastien.schotten_totten.core.exceptions.GameCreationException;
import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NoPlayerException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NotYourTurnException;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by Bastien on 19/11/2017.
 */

public class RestGameManagerTest {

    private final RestGameClient restGameClient =
            new RestGameClient("http://localhost:8080",
                    "testGame-" + System.currentTimeMillis());

    @Test
    public void TestPing() throws Exception {
        System.out.println(restGameClient.ping());
    }

    @Test
    public void TestCreateGame() throws GameCreationException {
        Assertions.assertTrue(restGameClient.createGame());
        System.out.println(restGameClient.getPlayingPlayer().getName());
    }

    @Test
    public void TestGetPlayers() throws GameCreationException {
        Assertions.assertTrue(restGameClient.createGame());
        System.out.println(restGameClient.getPlayingPlayer().getName());
        Assertions.assertEquals(restGameClient.getPlayingPlayer().getPlayerType(), PlayingPlayerType.ONE);
        Assertions.assertEquals(restGameClient.getPlayingPlayer().getPlayerType(), PlayingPlayerType.TWO);
    }

    @Test
    public void TestSwapPlayers() throws GameCreationException {
        Assertions.assertTrue(restGameClient.createGame());
        System.out.println(restGameClient.getPlayingPlayer().getName());
    }

    @Test
    public void TestGetWinner() throws GameCreationException {
        Assertions.assertTrue(restGameClient.createGame());
//        try {
//            System.out.println(restGameClient.getWinner().getName());
//            Assert.fail("no winner shall be found");
//        } catch (NoPlayerException e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void TestReclaim() throws GameCreationException {
        Assertions.assertTrue(restGameClient.createGame());
//        System.out.println(restGameClient.getPlayingPlayer().getName());
//        try {
//            Assert.assertFalse(restGameClient.reclaimMilestone(PlayingPlayerType.ONE, 0));
//        } catch (NotYourTurnException e) {
//            Assert.fail(e.getMessage());
//        }
    }

    @Test
    public void TestPlay() throws GameCreationException {
        Assertions.assertTrue(restGameClient.createGame());
//        System.out.println(restGameClient.getPlayingPlayer().getName());
//        try {
//            Assert.assertTrue(restGameClient.playerPlays(PlayingPlayerType.ONE, 0, 0));
//        } catch (NotYourTurnException e) {
//            Assert.fail(e.getMessage());
//        } catch (MilestoneSideMaxReachedException e) {
//            Assert.fail(e.getMessage());
//        }
    }

    @Test
    public void TestMilestones() throws GameCreationException {
//        Assert.assertTrue(restGameClient.createGame());
//        System.out.println(restGameClient.getPlayingPlayer().getName());
//        try {
//            Assert.assertTrue(restGameClient.playerPlays(PlayingPlayerType.ONE, 0, 0));
//            Assert.assertEquals(restGameClient.getMilestones().get(0).getPlayer1Side().size(), 1);
//        } catch (NotYourTurnException e) {
//            Assert.fail(e.getMessage());
//        } catch (MilestoneSideMaxReachedException e) {
//            Assert.fail(e.getMessage());
//        }
    }


}

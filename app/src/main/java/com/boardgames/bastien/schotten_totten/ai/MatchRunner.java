package com.boardgames.bastien.schotten_totten.ai;


import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.Player;
import java.util.Random;


/**
 * Created by rvlander on 15/04/17.
 */

public class MatchRunner {

    private GameAI ai1;
    private GameAI ai2;
    private Game game;
    private boolean turn;

    public MatchRunner() throws GameCreationException {
        //Random rand = new Radom();
        ai1 = new GameAiImpl();
        ai2 = new GameAiImpl();
        turn = true; //rand.nextBoolean();
        game = new Game(ai1.getName(), ai2.getName());
    }

    public Player makeTurn() throws CardInitialisationException, MilestoneSideMaxReachedException, HandFullException, NoPlayerException {
        final Player playingPlayer1 = game.getPlayer(game.getPlayingPlayer().getPlayerType());
        System.out.println(playingPlayer1.getName() + " is playing...");
        if (turn) {
            ai1.reclaimAndPlay(game);
        }
        else {
            ai2.reclaimAndPlay(game);
        }
        try {
            return game.getWinner();
         } catch (NoPlayerException e) {
            return null;
        }
    }

    public static void main(String args[]) {
        try {
            MatchRunner runner = new MatchRunner();
            while(true) {
                Player winner = runner.makeTurn();
                if(winner != null) {
                    System.out.println(winner.getName() + " wins.");
                    break;
                }
            }
        } catch (GameCreationException e) {
            System.err.println("System could not be created!");
        } catch (HandFullException e) {
            e.printStackTrace();
        } catch (NoPlayerException e) {
            e.printStackTrace();
        } catch (MilestoneSideMaxReachedException e) {
            e.printStackTrace();
        } catch (CardInitialisationException e) {
            e.printStackTrace();
        }

    }
}

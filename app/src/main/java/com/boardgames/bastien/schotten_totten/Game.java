package com.boardgames.bastien.schotten_totten;

import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.NoTurnException;
import com.boardgames.bastien.schotten_totten.model.GameBoard;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.Player;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

/**
 * Created by Bastien on 29/11/2016.
 */

public class Game implements Runnable {

    private final GameBoard board;

    private final Player player1;

    private final Player player2;

    public Game(final String player1Name, final String player2Name) throws HandFullException, EmptyDeckException {

        board = new GameBoard();

        // create players
        this.player1 = new Player(player1Name, PlayerType.ONE);
        this.player2 = new Player(player2Name, PlayerType.TWO);
        for (int i = 0; i < player1.getHand().MAX_HAND_SIZE; i++) {
            player1.getHand().addCard(board.getDeck().drawCard());
            player2.getHand().addCard(board.getDeck().drawCard());
        }

    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Player getPlayer(final PlayerType t) throws NoTurnException {
        switch (t) {
            case ONE:
                return player1;
            case TWO:
                return player2;
            default:
                throw new NoTurnException(t.toString());
        }
    }

    public PlayerType getWinner() {
        if (playerWinTheGame(player1)) {
            return player1.getPlayerType();
        } else if (playerWinTheGame(player2)) {
            return player2.getPlayerType();
        } else {
            return PlayerType.NONE;
        }
    }

    public GameBoard getGameBoard() {return board; }

    private void play() {

        while(!playerWinTheGame(player1) || !playerWinTheGame(player2)) {

            // player 1 turn
            final int player1InitialHandSize = player1.getHand().getHandSize();
            player1.setPlaying();

            // player 1 can reclaim
            do {
                // check if player 1 wins
                if (playerWinTheGame(player1)) {
                    return;
                }
            } while(player1.getHand().getHandSize() == player1InitialHandSize);

            // player has just 1 played a card

            // automatic draw
            try {
                player1.getHand().addCard(board.getDeck().drawCard());
            } catch (HandFullException e) {
                e.printStackTrace();
            } catch (EmptyDeckException e) {
                e.printStackTrace();
            }

            // player 2 turn
            player2.setNotPlaying();
            final int player2InitialHandSize = player2.getHand().getHandSize();
            player2.setPlaying();

            // player 2 can reclaim
            do {
                // check if player 1 wins
                if (playerWinTheGame(player2)) {
                    return;
                }
            } while(player2.getHand().getHandSize() == player2InitialHandSize);

            // player 2 has just played a card

            // automatic draw
            try {
                player2.getHand().addCard(board.getDeck().drawCard());
            } catch (HandFullException e) {
                e.printStackTrace();
            } catch (EmptyDeckException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean playerWinTheGame(final Player p) {

        // wins if 3 successives milestones has been captured
        for (int i = 0; i< p.getCapturedMilestones().size()-2; i++) {
            final Milestone m1 = p.getCapturedMilestones().get(i);
            final Milestone m2 = p.getCapturedMilestones().get(i+1);
            final Milestone m3 = p.getCapturedMilestones().get(i+2);
            if (m1 != null && m2 != null && m3 != null) {
                return true;
            }
        }
        // also wins if 5 milestones has been captured
        return (p.getCapturedMilestones().size() == 5);
    }

    @Override
    public void run() {
        play();
    }
}

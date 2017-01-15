package com.boardgames.bastien.schotten_totten.model;

import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.NoTurnException;
import com.boardgames.bastien.schotten_totten.model.GameBoard;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.Player;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 29/11/2016.
 */

public class Game {

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

    private boolean playerWinTheGame(final Player p) {

        final List<Milestone> playCapturedMilestones = new ArrayList<>();
        for (final Milestone m : board.getMilestones()) {
            if (m.getCaptured().equals(p.getPlayerType())) {
                playCapturedMilestones.add(m);
            }
        }

        if (playCapturedMilestones.size() >= 3) {
            // wins if 3 successive milestones have been captured
            for (int i = 0; i< playCapturedMilestones.size()-2; i++) {
                final Milestone m1 = playCapturedMilestones.get(i);
                final Milestone m2 = playCapturedMilestones.get(i+1);
                final Milestone m3 = playCapturedMilestones.get(i+2);
                if (m1.getId()+1 == m2.getId() && m1.getId()+2 == m3.getId()) {
                    return true;
                }
            }
        }

        // wins if 5 milestones have been captured
        return (playCapturedMilestones.size() == 5);
    }

}

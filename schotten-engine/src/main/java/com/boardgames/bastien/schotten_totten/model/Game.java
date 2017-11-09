package com.boardgames.bastien.schotten_totten.model;

import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bastien on 29/11/2016.
 */

public class Game implements Serializable {

    private final GameBoard board;

    private PlayerType playingPlayer;

    private final Player player1;

    private final Player player2;

    public Game(final String player1Name, final String player2Name) throws GameCreationException {

        board = new GameBoard();

        // create players
        this.player1 = new Player(player1Name, PlayerType.ONE);
        this.player2 = new Player(player2Name, PlayerType.TWO);

        try {
            for (int i = 0; i < player1.getHand().MAX_HAND_SIZE; i++) {
                player1.getHand().addCard(board.getDeck().drawCard());
                player2.getHand().addCard(board.getDeck().drawCard());
            }
        } catch (final EmptyDeckException | HandFullException e) {
            throw new GameCreationException(e);
        }

        playingPlayer = PlayerType.ONE;

    }

    public PlayerType getPlayingPlayerType() {
        return this.playingPlayer;
    }

    public void setPlayingPlayerType(final PlayerType pType) {
        this.playingPlayer = pType;
    }

    public void swapPlayingPlayerType() {
        switch (playingPlayer) {
            case ONE:
                playingPlayer = PlayerType.TWO;
                break;
            case TWO:
                playingPlayer = PlayerType.ONE;
                break;
            default:
                break;
        }
    }

    public Player getPlayingPlayer() throws NoPlayerException {
        return getPlayer(getPlayingPlayerType());
    }

    public Player getPlayer(final PlayerType t) throws NoPlayerException {
        switch (t) {
            case ONE:
                return player1;
            case TWO:
                return player2;
            default:
                throw new NoPlayerException(t.toString());
        }
    }

    public Player getWinner() throws NoPlayerException {
        if (playerWinTheGame(player1)) {
            return player1;
        } else if (playerWinTheGame(player2)) {
            return player2;
        } else {
            throw new NoPlayerException(PlayerType.NONE.toString());
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

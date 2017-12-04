package com.utils.bastien.schotten_totten.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bastien on 18/03/2017.
 */

public class TicTacToe implements Serializable {

    private final Map<Integer, String> grid = new HashMap<Integer, String>();
    private final String player1Name;
    private final String player2Name;
    private String playingPlayer;

    public TicTacToe(final String p1, final String p2) {
        player1Name = p1;
        player2Name = p2;
        playingPlayer = player1Name;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                grid.put(10*i+j, " ");
            }
        }
    }

    public String getPlayingPlayer() {
        return this.playingPlayer;
    }

    public void switchPlayer() {
        playingPlayer = (playingPlayer == player1Name) ? player2Name : player1Name ;
    }

    public void playCross(final int i, final int j) {
        play(i, j, "X");
    }

    public void playCircle(final int i, final int j) {
        play(i, j, "O");
    }

    public void playCross(final int index) {
        play(index,"X");
    }

    public void playCircle(final int index) {
        play(index, "O");
    }

    private void play(final int i, final int j, final String symbol) {
        if (i >=0 && i < 3 && j >=0 && j < 3) {
            final int position = 10*i+j;
            if (grid.get(position).equals(" ")) {
                grid.put(position, symbol);
            }
        }
    }

    public void play(final int index, String symbol) {
        if (grid.get(index).equals(" ")) {
            grid.put(index, symbol);
        }
    }

    public Map<Integer, String> getGameState() {
        return this.grid;
    }

    @Override
    public String toString() {
        final StringBuilder sb  = new StringBuilder("Grid:\n");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                sb.append("[" + grid.get(10*i+j) + "]");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String isFinished() {
        for (int i = 0; i < 3; i++) {
            if (!grid.get(10*i).equals(" ") &&
                    grid.get(10*i).equals(grid.get(10*i+1))
                    && grid.get(10*i+2).equals(grid.get(10*i+1))) {
                return grid.get(10*i);
            }
        }
        for (int j = 0; j < 3; j++) {
            if (!grid.get(j).equals(" ") &&
                    grid.get(j).equals(grid.get(j+10))
                    && grid.get(j+20).equals(grid.get(j+10))) {
                return grid.get(j);
            }
        }
        if (!grid.get(0).equals(" ") &&
                grid.get(0).equals(grid.get(11))
                && grid.get(11).equals(grid.get(22))) {
            return grid.get(0);
        }
        if (!grid.get(20).equals(" ") &&
                grid.get(20).equals(grid.get(11))
                && grid.get(11).equals(grid.get(2))) {
            return grid.get(20);
        }
        return "";
    }
}

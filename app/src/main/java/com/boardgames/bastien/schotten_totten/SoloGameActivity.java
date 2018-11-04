package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;

import com.boardgames.bastien.schotten_totten.ai.AiGameManager;
import com.boardgames.bastien.schotten_totten.ai.GameAI;
import com.boardgames.bastien.schotten_totten.ai.GameAiImpl;
import com.boradgames.bastien.schotten_totten.core.exceptions.EmptyDeckException;
import com.boradgames.bastien.schotten_totten.core.exceptions.HandFullException;
import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NotYourTurnException;

public class SoloGameActivity extends GameActivity {

    private final GameAI ai = new GameAiImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.gameManager = new AiGameManager(getString(R.string.player1name), getString(R.string.player2name));
            initUI(this.gameManager.getPlayingPlayer().getPlayerType());

        } catch (final Exception e) {
            showErrorMessage(e);
        }
    }

    @Override
    protected void endOfTurn() {
        updateUI(this.gameManager.getPlayingPlayer().getPlayerType());
        disableClick();
        gameManager.swapPlayers();
        try {
            ai.reclaimAndPlay((AiGameManager) gameManager);
        } catch (MilestoneSideMaxReachedException | HandFullException | EmptyDeckException | NotYourTurnException e) {
           showErrorMessage(e);
        }
        gameManager.swapPlayers();
        enableClick();
        updateUI(this.gameManager.getPlayingPlayer().getPlayerType());
    }
}

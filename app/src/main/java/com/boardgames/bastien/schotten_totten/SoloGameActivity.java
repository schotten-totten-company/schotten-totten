package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;

import com.boardgames.bastien.schotten_totten.ai.GameAI;
import com.boardgames.bastien.schotten_totten.ai.GameAiImpl;
import com.boradgames.bastien.schotten_totten.core.controllers.SimpleGameManager;
import com.boradgames.bastien.schotten_totten.core.exceptions.CardInitialisationException;
import com.boradgames.bastien.schotten_totten.core.exceptions.HandFullException;
import com.boradgames.bastien.schotten_totten.core.exceptions.MilestoneSideMaxReachedException;
import com.boradgames.bastien.schotten_totten.core.exceptions.NoPlayerException;

public class SoloGameActivity extends GameActivity {

    private final GameAI ai = new GameAiImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.gameManager = new SimpleGameManager(getString(R.string.player1name), getString(R.string.player2name));
            initUI(this.gameManager.getPlayingPlayer().getPlayerType());

        } catch (final Exception e) {
            showErrorMessage(e);
        }
    }

    @Override
    protected void endOfTurn() {
        updateUI(this.gameManager.getPlayingPlayer().getPlayerType());
        disableClick();
        //gameManager.swapPlayingPlayer();
        try {
            ai.reclaimAndPlay(gameManager);
        } catch (final MilestoneSideMaxReachedException | NoPlayerException |
                CardInitialisationException |HandFullException e) {
            showErrorMessage(e);
        }
        enableClick();
        updateUI(this.gameManager.getPlayingPlayer().getPlayerType());
    }
}

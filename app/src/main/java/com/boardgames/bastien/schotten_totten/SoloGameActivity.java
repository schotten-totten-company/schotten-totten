package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;

import com.boardgames.bastien.schotten_totten.ai.GameAI;
import com.boardgames.bastien.schotten_totten.ai.GameAiImpl;
import com.boardgames.bastien.schotten_totten.controllers.SimpleGameManager;
import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

public class SoloGameActivity extends GameActivity {

    private final GameAI ai = new GameAiImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.gameManager = new SimpleGameManager(getString(R.string.player1name), getString(R.string.player2name));
            initUI(this.gameManager.getGame().getPlayer(PlayerType.ONE).getHand());

        } catch (final Exception e) {
            showErrorMessage(e);
        }
    }

    @Override
    protected void endOfTurn() throws NoPlayerException {
        updateUI();
        disableClick();
        gameManager.swapPlayingPlayer();
        try {
            ai.reclaimAndPlay(gameManager.getGame());
        } catch (final MilestoneSideMaxReachedException |
                CardInitialisationException |HandFullException e) {
            showErrorMessage(e);
        }
        enableClick();
        updateUI();
    }
}

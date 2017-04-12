package com.boardgames.bastien.schotten_totten;

import android.content.Intent;
import android.os.Bundle;

import com.boardgames.bastien.schotten_totten.ai.GameAI;
import com.boardgames.bastien.schotten_totten.ai.GameAiImpl;
import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

public class SoloGameActivity extends GameActivity {

    private final GameAI ai = new GameAiImpl();

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SoloGameActivity.this, LauncherActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.game = new Game(getString(R.string.player1name), getString(R.string.player2name));
            initUI(this.game.getPlayer(PlayerType.ONE).getHand());

        } catch (final Exception e) {
            showErrorMessage(e);
        }
    }

    @Override
    protected void endOfTurn() throws NoPlayerException {
        updateUI();
        disableClick();
        game.swapPlayingPlayerType();
        try {
            ai.reclaimAndPlay(game);
        } catch (final MilestoneSideMaxReachedException |
                CardInitialisationException |HandFullException e) {
            showErrorMessage(e);
        }
        enableClick();
        updateUI();
    }
}

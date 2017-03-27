package com.boardgames.bastien.schotten_totten;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.ai.GameAI;
import com.boardgames.bastien.schotten_totten.ai.GameAiImpl;
import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Game;

public class SoloGameActivity extends GameActivity {

    private final GameAI ai = new GameAiImpl();

    @Override
    public void onBackPressed() {
        startActivity(new Intent(SoloGameActivity.this, LauncherActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_seat_game);

        try {
            this.game = new Game(getString(R.string.player1name), getString(R.string.player2name));
            initUI(SoloGameActivity.this);

        } catch (final Exception e) {
            showErrorMessage(e);
        }
    }

    @Override
    protected void endOfTurn() throws NoPlayerException {
        game.swapPlayingPlayerType();
        try {
            ai.reclaimAndPlay(game);
        } catch (final MilestoneSideMaxReachedException |
                CardInitialisationException |HandFullException e) {
            showErrorMessage(e);
        }
        super.performEnfOfTheTurnActions();
    }
}

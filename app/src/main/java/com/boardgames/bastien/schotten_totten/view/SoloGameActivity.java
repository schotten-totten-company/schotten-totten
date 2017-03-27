package com.boardgames.bastien.schotten_totten.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.GameActivity;
import com.boardgames.bastien.schotten_totten.LauncherActivity;
import com.boardgames.bastien.schotten_totten.MemoActivity;
import com.boardgames.bastien.schotten_totten.R;
import com.boardgames.bastien.schotten_totten.ai.GameAI;
import com.boardgames.bastien.schotten_totten.ai.GameAiImpl;
import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
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
            selectedCard = -1;

            ((TextView) findViewById(R.id.textView)).setText(game.getPlayingPlayer().getName());
            findViewById(R.id.memoButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(SoloGameActivity.this, MemoActivity.class));
                }
            });

            initSkipButton();

            initBoard();

            initHand();

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
        super.endOfTurn();
    }
}

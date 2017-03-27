package com.boardgames.bastien.schotten_totten;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Game;

public class HotSeatGameActivity extends GameActivity {

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HotSeatGameActivity.this, LauncherActivity.class));
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
                    startActivity(new Intent(HotSeatGameActivity.this, MemoActivity.class));
                }
            });

            initSkipButton();

            initBoard();

            initHand();

        } catch (final GameCreationException e) {
            showErrorMessage(e);
        } catch (final NoPlayerException e) {
            showErrorMessage(e);
        }
    }

    @Override
    protected void endOfTurn() throws NoPlayerException {
        game.swapPlayingPlayerType();
        super.endOfTurn();
    }
}

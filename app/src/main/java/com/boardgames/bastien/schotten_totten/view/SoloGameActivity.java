package com.boardgames.bastien.schotten_totten.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.GameActivity;
import com.boardgames.bastien.schotten_totten.LauncherActivity;
import com.boardgames.bastien.schotten_totten.MemoActivity;
import com.boardgames.bastien.schotten_totten.R;
import com.boardgames.bastien.schotten_totten.ai.GameAI;
import com.boardgames.bastien.schotten_totten.ai.GameAiImpl;
import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.HandFullException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.Hand;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.List;

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

        } catch (final GameCreationException e) {
            showErrorMessage(e);
        } catch (final NoPlayerException e) {
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
        endOfTurn();
    }
}

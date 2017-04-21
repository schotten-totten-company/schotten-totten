package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Game;

public class HotSeatGameActivity extends GameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.game = new Game(getString(R.string.player1name), getString(R.string.player2name));
            initUI(this.game.getPlayingPlayer().getHand());

        } catch (final NoPlayerException | GameCreationException e) {
            showErrorMessage(e);
        }
    }

    @Override
    protected void endOfTurn() throws NoPlayerException {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.end_of_the_turn_title));
        alertDialog.setMessage(getString(R.string.end_of_the_turn_hotseat_message) + game.getPlayingPlayer().getName());
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            // swap player
                            game.swapPlayingPlayerType();
                            updateUI();
                            enableClick();
                            dialog.dismiss();
                        } catch (final NoPlayerException e) {
                            showErrorMessage(e);
                        }
                    }
                });
        alertDialog.setCancelable(false);

        updateUI();
        disableClick();

        // hide hand
        findViewById(R.id.handLayout).setVisibility(View.INVISIBLE);

        alertDialog.show();
    }
}

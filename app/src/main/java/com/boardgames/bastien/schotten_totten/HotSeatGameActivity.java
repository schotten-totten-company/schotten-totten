package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.boardgames.bastien.schotten_totten.controllers.SimpleGameManager;
import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;

public class HotSeatGameActivity extends GameActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            this.gameManager = new SimpleGameManager(getIntent().getStringExtra("player1Name"),
                    getIntent().getStringExtra("player2Name"));
            initUI(this.gameManager.getPlayingPlayer().getHand());

        } catch (final GameCreationException e) {
            showErrorMessage(e);
        }
    }

    @Override
    protected void endOfTurn() throws NoPlayerException {
        //gameManager.swapPlayingPlayer();
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.end_of_the_turn_title));
        alertDialog.setMessage(getString(R.string.end_of_the_turn_hotseat_message) + gameManager.getPlayingPlayer().getName());
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            // swap player
                            updateUI();
                            enableClick();
                            dialog.dismiss();
                        } catch (final NoPlayerException e) {
                            showErrorMessage(e);
                        }
                    }
                });
        alertDialog.setCancelable(false);

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                final Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEUTRAL);
                button.setTextSize((float)30);
            }
        });

        updateUI();
        disableClick();

        // hide hand
        handLayout.setVisibility(View.INVISIBLE);

        alertDialog.show();
    }
}

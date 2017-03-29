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
    public void onBackPressed() {
        startActivity(new Intent(HotSeatGameActivity.this, LauncherActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_seat_game);

        try {
            this.game = new Game(getString(R.string.player1name), getString(R.string.player2name));
            initUI(HotSeatGameActivity.this);

        } catch (final GameCreationException e) {
            showErrorMessage(e);
        }
    }

    @Override
    protected void endOfTurn() throws NoPlayerException {
        game.swapPlayingPlayerType();
        findViewById(R.id.handLayout).setVisibility(View.INVISIBLE);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.end_of_the_turn_title));
        alertDialog.setMessage(getString(R.string.end_of_the_turn_hotseat_message) + game.getPlayingPlayer().getName());
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            performEnfOfTheTurnActions();
                            dialog.dismiss();
                        } catch (final NoPlayerException e) {
                            showErrorMessage(e);
                        }
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}

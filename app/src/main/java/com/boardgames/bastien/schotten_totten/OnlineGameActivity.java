package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.exceptions.CardInitialisationException;
import com.boardgames.bastien.schotten_totten.exceptions.EmptyDeckException;
import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.exceptions.MilestoneSideMaxReachedException;
import com.boardgames.bastien.schotten_totten.exceptions.NoPlayerException;
import com.boardgames.bastien.schotten_totten.model.Card;
import com.boardgames.bastien.schotten_totten.model.Deck;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.Hand;
import com.boardgames.bastien.schotten_totten.model.Milestone;
import com.boardgames.bastien.schotten_totten.model.PlayerType;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.map.HashedMap;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OnlineGameActivity extends AppCompatActivity {

    private final Map<String, String> grid = new HashMap<>();

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OnlineGameActivity.this, LauncherActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_online_test);
        ((TextView)findViewById(R.id.playingPlayerText)).setText("");

        final ImageButton grid00Button = ((ImageButton)findViewById(R.id.grid00Button));
        grid00Button.setBackgroundColor(Color.GRAY);
        grid00Button.setOnClickListener(gridListener);

    }

    private View.OnClickListener gridListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final ImageButton gridButton = ((ImageButton)v);
        }
    };

    private void showErrorMessage(final Exception e) {
        final StringWriter message = new StringWriter();
        e.printStackTrace(new PrintWriter(message));
        showAlertMessage("Error : " + e.getMessage(), message.toString(), true, true);
    }

    private void showAlertMessage(final String message) {
        showAlertMessage(getString(R.string.warning_title), message, false, false);
    }

    private void showAlertMessage(final String title, final String message, final boolean finish, final boolean hideBoard) {
        if (hideBoard) {
            findViewById(R.id.gameLayout).setVisibility(View.INVISIBLE);
        }
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        findViewById(R.id.gameLayout).setVisibility(View.VISIBLE);
                        dialog.dismiss();
                        if (finish) {
                            finish();
                        }
                    }
                });
        alertDialog.show();

    }
}

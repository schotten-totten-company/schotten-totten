package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.EditText;
import android.widget.TextView;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        final TextView hotSeatLauncherText = (TextView) findViewById(R.id.hotSeatLauncherText);
        hotSeatLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherActivity.this, HotSeatGameActivity.class));
            }
        });

        final TextView onlineLauncherText = (TextView) findViewById(R.id.onLineLauncherText);
        onlineLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherActivity.this, OnlineGameActivity.class));
            }
        });

        findViewById(R.id.rulesLauncherText).setAlpha((float)0.5);
        findViewById(R.id.aboutLauncherText).setAlpha((float)0.5);
        findViewById(R.id.quitLauncherText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

    }

    private void enterPlayersNames() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Players Names");

        // Set up the input
        final EditText player1NameInput = new EditText(this);
        player1NameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        player1NameInput.setText("player1");
        builder.setView(player1NameInput);
        final EditText player2NameInput = new EditText(this);
        player2NameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        player2NameInput.setText("player2");
        builder.setView(player2NameInput);

        // Set up the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String player1Name = player1NameInput.getText().toString();
                final String player2Name = player2NameInput.getText().toString();
                startActivity(new Intent(LauncherActivity.this, HotSeatGameActivity.class));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}

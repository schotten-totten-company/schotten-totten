package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.tictactoetest.CreateOnlineTestGameActivity;
import com.boardgames.bastien.schotten_totten.tictactoetest.JoinOnlineTestGameActivity;
import com.boardgames.bastien.schotten_totten.view.SoloGameActivity;

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

        final TextView createOnLineLauncherText = (TextView) findViewById(R.id.createOnLineLauncherText);
        createOnLineLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherActivity.this, CreateOnlineGameActivity.class));
            }
        });

        final TextView joinOnlineLauncherText = (TextView) findViewById(R.id.joinOnlineLauncherText);
        joinOnlineLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterDistantIp();
            }
        });

        final TextView soloLauncherText = (TextView) findViewById(R.id.soloLauncherText);
        soloLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherActivity.this, SoloGameActivity.class));
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

    private void enterDistantIp() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter player1 IP: ");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String distantIp = input.getText().toString();
                final Intent joinIntent = new Intent(LauncherActivity.this, JoinOnlineGameActivity.class);
                joinIntent.putExtra("distantIp", distantIp);
                startActivity(joinIntent);
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

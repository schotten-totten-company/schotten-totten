package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.exceptions.GameCreationException;
import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.PlayerType;
import com.boardgames.bastien.schotten_totten.server.GameClient;

import java.util.concurrent.ExecutionException;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        final TextView hotSeatLauncherText = (TextView) findViewById(R.id.hotSeatLauncherText);
        hotSeatLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(LauncherActivity.this, HotSeatGameActivity.class));
                enterPlayersNames();
            }
        });

        final TextView createOnLineLauncherText = (TextView) findViewById(R.id.createOnLineLauncherText);
        createOnLineLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent createIntent = new Intent(LauncherActivity.this, CreateLanGameActivity.class);
                startActivity(createIntent);
            }
        });

        final TextView createServerLauncherText = (TextView) findViewById(R.id.createServerLauncherTest);
        createServerLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                final Intent joinServerIntent = new Intent(LauncherActivity.this, ServerGameActivity.class);
                new GameClient().createGame("game01", new Game("P1", "P2"));
                joinServerIntent.putExtra("gameName", "game01");
                joinServerIntent.putExtra("type", PlayerType.ONE);
                startActivity(joinServerIntent);
                } catch (final GameCreationException e) {
                    e.printStackTrace();
                } catch (final ExecutionException e) {
                    e.printStackTrace();
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        final TextView joinServerLauncherText = (TextView) findViewById(R.id.joinServerLauncherText);
        joinServerLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent joinServerIntent = new Intent(LauncherActivity.this, ServerGameActivity.class);
                joinServerIntent.putExtra("gameName", "game01");
                joinServerIntent.putExtra("type", PlayerType.TWO);
                startActivity(joinServerIntent);
            }
        });

        mangeJoinButton();

        final TextView soloLauncherText = (TextView) findViewById(R.id.soloLauncherText);
        soloLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherActivity.this, SoloGameActivity.class));
            }
        });

        findViewById(R.id.aboutLauncherText).setAlpha((float)0.5);
        findViewById(R.id.quitLauncherText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        mangeJoinButton();
    }

    private void mangeJoinButton() {
        final TextView joinOnlineLauncherText = (TextView) findViewById(R.id.joinOnlineLauncherText);
        if (JoinLanGameActivity.joinAlreadyLaunched) {
            joinOnlineLauncherText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LauncherActivity.this, JoinLanGameActivity.class));
                }
            });
        } else {
            joinOnlineLauncherText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterDistantIp();
                }
            });
        }
    }

    private void enterDistantIp() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enter_ip_message));

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String distantIp = input.getText().toString();
                final Intent joinIntent = new Intent(LauncherActivity.this, JoinLanGameActivity.class);
                joinIntent.putExtra("distantIp", distantIp);
                startActivity(joinIntent);
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set up the input
        final EditText player1NameInput = new EditText(this);
        player1NameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        player1NameInput.setText(getString(R.string.player1name));
        layout.addView(player1NameInput);
        final EditText player2NameInput = new EditText(this);
        player2NameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        player2NameInput.setText(getString(R.string.player2name));
        layout.addView(player2NameInput);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String player1Name = player1NameInput.getText().toString();
                final String player2Name = player2NameInput.getText().toString();
                final Intent hotSeatIntent = new Intent(LauncherActivity.this, HotSeatGameActivity.class);
                hotSeatIntent.putExtra("player1Name", player1Name);
                hotSeatIntent.putExtra("player2Name", player2Name);
                startActivity(hotSeatIntent);
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

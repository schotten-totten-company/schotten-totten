package com.boardgames.bastien.schotten_totten;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.model.Game;
import com.boardgames.bastien.schotten_totten.model.PlayerType;
import com.boardgames.bastien.schotten_totten.server.GameAlreadyExistsException;
import com.boardgames.bastien.schotten_totten.server.GameClient;
import com.boardgames.bastien.schotten_totten.server.GameDoNotExistException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
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
                enterGameName();
            }
        });

        final TextView joinServerLauncherText = (TextView) findViewById(R.id.joinServerLauncherText);
        joinServerLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGame();
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

    private void enterGameName() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_game_name));

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(getString(R.string.game_name) + System.currentTimeMillis());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String gameName = input.getText().toString();
                try {
                    new GameClient().createGame(gameName, new Game("P1", "P2"));
                    final Intent joinIntent = new Intent(LauncherActivity.this, ServerGameActivity.class);
                    joinIntent.putExtra("gameName", gameName);
                    joinIntent.putExtra("type", PlayerType.ONE.toString());
                    startActivity(joinIntent);
                } catch (final ExecutionException e) {
                    if (e.getCause() instanceof GameAlreadyExistsException) {
                        showError(getString(R.string.warning_title),
                                gameName + getString(R.string.game_already_exist));
                    } else {
                        showError(e);
                    }
                } catch (Exception e) {
                    showError(e);
                }
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

    private void joinGame() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_game_name));

        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Set up the input
        final Spinner spinner = new Spinner(this);
        try {
            final Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow =
                    (android.widget.ListPopupWindow) popup.get(spinner);
            popupWindow.setHeight(1000);

            final ArrayList<String> list =  new GameClient().listGame();
            final ArrayAdapter<String> spinnerArrayAdapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
            spinner.setAdapter(spinnerArrayAdapter);
            layout.addView(spinner);
        } catch (Exception e) {
            showError(e);
        }

        //set player type
        final RadioGroup radioGroup = new RadioGroup(this);
        final RadioButton p1Button = new RadioButton(this);
        p1Button.setText("ONE");
        radioGroup.addView(p1Button);
        final RadioButton p2Button = new RadioButton(this);
        p2Button.setText("TWO");
        radioGroup.addView(p2Button);
        radioGroup.check(p2Button.getId());
        layout.addView(radioGroup);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String gameName = spinner.getSelectedItem().toString();
                final Intent joinIntent = new Intent(LauncherActivity.this, ServerGameActivity.class);
                joinIntent.putExtra("gameName", gameName);
                final RadioButton selectedButton =
                        (RadioButton) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                joinIntent.putExtra("type", selectedButton.getText().toString());
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

    private final void showError(final Exception e) {
        final StringWriter message = new StringWriter();
        e.printStackTrace(new PrintWriter(message));
        showError("Error : " + e.getMessage(), message.toString());
    }

    private final void showError(final String title, final String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}

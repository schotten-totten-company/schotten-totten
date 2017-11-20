package com.boardgames.bastien.schotten_totten;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.controllers.SimpleGameManager;
import com.boardgames.bastien.schotten_totten.model.MilestonePlayerType;
import com.boardgames.bastien.schotten_totten.model.PlayingPlayerType;
import com.boardgames.bastien.schotten_totten.server.GameAlreadyExistsException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class LauncherActivity extends Activity {

    private ProgressDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);

        waitingDialog = new ProgressDialog(LauncherActivity.this);
        waitingDialog.setTitle(getString(R.string.contacting_server));
        waitingDialog.setMessage(getString(R.string.please_wait));
        waitingDialog.setCanceledOnTouchOutside(false);
        waitingDialog.setCancelable(false);
        waitingDialog.dismiss();


        final TextView hotSeatLauncherText = (TextView) findViewById(R.id.hotSeatLauncherText);
        hotSeatLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        findViewById(R.id.aboutLauncherText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDialog();
            }


        });

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
        waitingDialog.dismiss();
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
                final String gameName = input.getText().toString().trim();
                waitingDialog.show();
                try {
//                    final Future<Boolean> future =
//                            new GameClient().createGame(gameName, new SimpleGameManager("P1", "P2"));
//                    // show waiting pop up
//                    future.get();
                    final Intent joinIntent = new Intent(LauncherActivity.this, ServerGameActivity.class);
                    joinIntent.putExtra("gameName", gameName);
                    joinIntent.putExtra("type", PlayingPlayerType.ONE.toString());
                    startActivity(joinIntent);
//                } catch (final ExecutionException e) {
//                    if (e.getCause() instanceof GameAlreadyExistsException) {
//                        showError(getString(R.string.warning_title),
//                                gameName + getString(R.string.game_already_exist));
//                    } else {
//                        showError(e);
//                    }
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
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //waitingDialog.dismiss();
            }
        });
        builder.show();
    }

    private void joinGame() {
        try {
            final Future<ArrayList<String>> future =  null;//new GameClient().listGame();
            // show waiting pop up
            waitingDialog.show();
            final ArrayList<String> list = future.get();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.choose_game_name));

            final LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            // Set up the input
            final Spinner spinner = new Spinner(this);
            final Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);
            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow =
                    (android.widget.ListPopupWindow) popup.get(spinner);
            popupWindow.setHeight(1000);

            final ArrayAdapter<String> spinnerArrayAdapter =
                    new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
            spinner.setAdapter(spinnerArrayAdapter);
            layout.addView(spinner);

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
                    // dismiss waiting pop up
                    waitingDialog.dismiss();

                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    // dismiss waiting pop up
                    waitingDialog.dismiss();
                }
            });

            builder.show();

        } catch (Exception e) {
            showError(e);
        }
    }

    private void enterPlayersNames() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.enter_game_name));

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
                final String player1Name = player1NameInput.getText().toString().trim();
                final String player2Name = player2NameInput.getText().toString().trim();
                final Intent hotSeatIntent = new Intent(LauncherActivity.this, HotSeatGameActivity.class);
                hotSeatIntent.putExtra("player1Name", player1Name);
                hotSeatIntent.putExtra("player2Name", player2Name);
                startActivity(hotSeatIntent);
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

    private final void showError(final Exception e) {
        final StringWriter message = new StringWriter();
        e.printStackTrace(new PrintWriter(message));
        showError(getString(R.string.error_title) + e.getMessage(), message.toString());
    }

    private final void showError(final String title, final String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private final void showAboutDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.about_title));
        alertDialog.setMessage(getString(R.string.about_content));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setCancelable(true);
        alertDialog.show();
    }
}

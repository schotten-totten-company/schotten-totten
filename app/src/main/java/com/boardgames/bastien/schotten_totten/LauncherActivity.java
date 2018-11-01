package com.boardgames.bastien.schotten_totten;

import android.app.Activity;
import android.app.AlertDialog;
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

import com.boardgames.bastien.schotten_totten.server.RestGameClient;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;

public class LauncherActivity extends Activity {

    protected String onlineUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);

        onlineUrl = getString(R.string.online_url);

        // hot seat
        final TextView hotSeatLauncherText = findViewById(R.id.hotSeatLauncherText);
        hotSeatLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterPlayersNames();
            }
        });

        // solo vs IA
        final TextView soloLauncherText = findViewById(R.id.soloLauncherText);
        soloLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LauncherActivity.this, SoloGameActivity.class));
            }
        });

        // create lan game
        final TextView createOnLineLauncherText = findViewById(R.id.createOnLineLauncherText);
        createOnLineLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent createIntent = new Intent(LauncherActivity.this, ServerGameActivity.class);
                createIntent.putExtra(getString(R.string.server_url_key), getString(R.string.localhost_url));
                createIntent.putExtra(getString(R.string.game_name_key), getString(R.string.lan_game));
                createIntent.putExtra(getString(R.string.type_key), PlayingPlayerType.ONE.toString());
                startActivity(createIntent);
            }
        });
        // join lan game
        final TextView joinOnlineLauncherText = findViewById(R.id.joinOnlineLauncherText);

        joinOnlineLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ScanForLanServerBackgroundTask task =
                        new ScanForLanServerBackgroundTask(LauncherActivity.this);
                task.execute();
            }
        });

        // create online game
        final TextView createServerLauncherText = findViewById(R.id.createServerLauncherTest);
        createServerLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterGameName();
            }
        });
        // join online game
        final TextView joinServerLauncherText = findViewById(R.id.joinServerLauncherText);
        joinServerLauncherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinGame();
            }
        });

        // about
        findViewById(R.id.aboutLauncherText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAboutDialog();
            }


        });
        // guit
        findViewById(R.id.quitLauncherText).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

    }

//    private void enterDistantIp() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle(getString(R.string.enter_ip_message));
//
//        // Set up the input
//        final EditText input = new EditText(this);
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//
//        // Set up the buttons
//        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                final String distantIp = input.getText().toString();
//                final Intent joinIntent = new Intent(LauncherActivity.this, ServerGameActivity.class);
//                joinIntent.putExtra(getString(R.string.server_url_key), getString(R.string.http_prefix) + distantIp + getString(R.string.localhost_port));
//                joinIntent.putExtra(getString(R.string.game_name_key), getString(R.string.lan_game));
//                joinIntent.putExtra(getString(R.string.type_key), PlayingPlayerType.TWO.toString());
//                startActivity(joinIntent);
//            }
//        });
//        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show();
//    }

    private void enterGameName() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.choose_game_name));

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        final String message = getString(R.string.game_name) + System.currentTimeMillis();
        input.setText(message);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                final String gameName = input.getText().toString().trim();
                final CreateOnlineGameBackgroundTask task =
                        new CreateOnlineGameBackgroundTask(LauncherActivity.this, gameName);
                task.execute();
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
            // show waiting pop up
            //waitingDialog.show();
            final List<String> list = new RestGameClient(onlineUrl, "").listGames();
            //waitingDialog.dismiss();
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
            p1Button.setText(PlayingPlayerType.ONE.toString());
            radioGroup.addView(p1Button);
            final RadioButton p2Button = new RadioButton(this);
            p2Button.setText(PlayingPlayerType.TWO.toString());
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
                    joinIntent.putExtra(getString(R.string.game_name_key), gameName);
                    final RadioButton selectedButton =
                            radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                    joinIntent.putExtra(getString(R.string.type_key), selectedButton.getText().toString());
                    joinIntent.putExtra(getString(R.string.server_url_key), onlineUrl);
                    startActivity(joinIntent);
                    // dismiss waiting pop up
                    //waitingDialog.dismiss();

                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    // dismiss waiting pop up
                    //waitingDialog.dismiss();
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
                hotSeatIntent.putExtra(getString(R.string.player1_name_key), player1Name);
                hotSeatIntent.putExtra(getString(R.string.player2_name_key), player2Name);
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

    public final void showError(final Exception e) {
        final StringWriter message = new StringWriter();
        e.printStackTrace(new PrintWriter(message));
        showError(getString(R.string.error_title) + e.getMessage(), message.toString());
    }

    public final void showError(final String title, final String message) {
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

    private void showAboutDialog() {
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

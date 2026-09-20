package com.boardgames.bastien.schotten_totten;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextThemeWrapper;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.boardgames.bastien.schotten_totten.ai.GameAiImpl;
import com.boardgames.bastien.schotten_totten.ai.GameAiLucieImpl;
import com.boardgames.bastien.schotten_totten.server.RestGameClient;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class LauncherActivity extends Activity {

    protected String onlineUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);

        onlineUrl = getString(R.string.online_url);

        // hot seat
        final TextView hotSeatLauncherText = findViewById(R.id.hotSeatLauncherText);
        hotSeatLauncherText.setOnClickListener(v -> enterPlayersNames());

        // solo vs IA
        final TextView soloLauncherText = findViewById(R.id.soloLauncherText);
        soloLauncherText.setOnClickListener(v -> chooseAI());

        // create lan game
        final TextView createOnLineLauncherText = findViewById(R.id.createOnLineLauncherText);
        createOnLineLauncherText.setOnClickListener(v -> {
            final WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiMgr.isWifiEnabled()) {
                final Intent createIntent = new Intent(LauncherActivity.this, ServerGameActivity.class);
                createIntent.putExtra(getString(R.string.server_url_key), getString(R.string.localhost_url));
                createIntent.putExtra(getString(R.string.game_name_key), getString(R.string.lan_game));
                createIntent.putExtra(getString(R.string.type_key), PlayingPlayerType.ONE.toString());
                startActivity(createIntent);
            } else {
                showError(getString(R.string.unknown_host_title), getString(R.string.unknown_host_message));
            }
        });

        // join lan game
        final TextView joinOnlineLauncherText = findViewById(R.id.joinOnlineLauncherText);
        joinOnlineLauncherText.setOnClickListener(v -> {
            final WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifiMgr.isWifiEnabled()) {
                final ScanForLanServerBackgroundTask task =
                        new ScanForLanServerBackgroundTask(LauncherActivity.this);
                task.execute();
            } else {
                showError(getString(R.string.unknown_host_title), getString(R.string.unknown_host_message));
            }
        });

        // create online game
        final TextView createServerLauncherText = findViewById(R.id.createServerLauncherTest);
        createServerLauncherText.setOnClickListener(v -> enterGameName());
        // join online game
        final TextView joinServerLauncherText = findViewById(R.id.joinServerLauncherText);
        joinServerLauncherText.setOnClickListener(v -> joinGame());

        // about
        findViewById(R.id.aboutLauncherText).setOnClickListener(v -> showAboutDialog());
        // quit
        findViewById(R.id.quitLauncherText).setOnClickListener(v -> finishAffinity());

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

        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialog));
        builder.setTitle(getString(R.string.choose_game_name));

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        final String currentDate = new SimpleDateFormat(" (dd-MM-yyyy, HH:mm:ss)", Locale.FRANCE).format(new Date());
        final String message = getString(R.string.game_name) + currentDate;
        input.setText(message);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            dialog.dismiss();
            final String gameName = input.getText().toString().trim();
            final CreateOnlineGameBackgroundTask task =
                    new CreateOnlineGameBackgroundTask(LauncherActivity.this, LauncherActivity.this.onlineUrl, gameName);
            task.execute();

        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.setOnDismissListener(dialog -> {
        });

        if (!this.isFinishing() && !this.isDestroyed()) {
            builder.show();
        }
    }

    private void joinGame() {
        try {
            // show waiting pop up
            //waitingDialog.show();
            final List<String> list = new RestGameClient(onlineUrl, "").listGames();
            //waitingDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialog));
            builder.setTitle(getString(R.string.choose_game_name));

            final LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            final Spinner spinner = new Spinner(this);
            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    list
            );

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
            builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
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

            });
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
                dialog.cancel();
            });

            spinnerArrayAdapter.notifyDataSetChanged();
            if (!this.isFinishing() && !this.isDestroyed()) {
                builder.show();
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void chooseAI() {
        try {
            // show waiting pop up
            final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialog));
            builder.setTitle(getString(R.string.choose_ai_title));

            final LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            //set player type
            final RadioGroup radioGroup = new RadioGroup(this);
            final RadioButton easyAI = new RadioButton(this);
            easyAI.setText(new GameAiImpl(PlayingPlayerType.TWO).getName());
            radioGroup.addView(easyAI);
            final RadioButton lucyAI = new RadioButton(this);
            lucyAI.setText(new GameAiLucieImpl(PlayingPlayerType.TWO).getName());
            radioGroup.addView(lucyAI);
            radioGroup.check(easyAI.getId());
            layout.addView(radioGroup);

            builder.setView(layout);

            // Set up the buttons
            builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
                final Intent joinIntent = new Intent(LauncherActivity.this, SoloGameActivity.class);
                final RadioButton selectedButton =
                        radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
                joinIntent.putExtra(getString(R.string.chosen_ai_name_key), selectedButton.getText().toString());
                startActivity(joinIntent);

            });
            builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

            if (!this.isFinishing() && !this.isDestroyed()) {
                builder.show();
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    private void enterPlayersNames() {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialog));
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
        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            final String player1Name = player1NameInput.getText().toString().trim();
            final String player2Name = player2NameInput.getText().toString().trim();
            final Intent hotSeatIntent = new Intent(LauncherActivity.this, HotSeatGameActivity.class);
            hotSeatIntent.putExtra(getString(R.string.player1_name_key), player1Name);
            hotSeatIntent.putExtra(getString(R.string.player2_name_key), player2Name);
            startActivity(hotSeatIntent);
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.cancel());

        if (!this.isFinishing() && !this.isDestroyed()) {
            builder.show();
        }
    }

    public final void showError(final Exception e) {
        final StringWriter message = new StringWriter();
        e.printStackTrace(new PrintWriter(message));
        showError(getString(R.string.error_title) + e.getMessage(), message.toString());
    }

    public final void showError(final String title, final String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(LauncherActivity.this, R.style.CustomAlertDialog)).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                (dialog, which) -> dialog.dismiss());
        alertDialog.setCancelable(false);
        if (!this.isFinishing() && !this.isDestroyed()) {
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }    }

    private void showAboutDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.CustomAlertDialog)).create();
        alertDialog.setTitle(getString(R.string.about_title));
        alertDialog.setMessage(getString(R.string.about_content));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                (dialog, which) -> dialog.dismiss());
        alertDialog.setCancelable(true);
        if (!this.isFinishing() && !this.isDestroyed()) {
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

}

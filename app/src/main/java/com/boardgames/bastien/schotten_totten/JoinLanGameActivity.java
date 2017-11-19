package com.boardgames.bastien.schotten_totten;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.boardgames.bastien.schotten_totten.model.PlayingPlayerType;
import com.boardgames.bastien.schotten_totten.server.RestGameClient;

public class JoinLanGameActivity extends LanGameActivity {

    protected static boolean joinAlreadyLaunched = false;
    private String distantIp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!joinAlreadyLaunched) {
            playerName = getString(R.string.player2name);
            playerType = PlayingPlayerType.TWO;
            distantIp = getIntent().getStringExtra("distantIp");
            gameManager = new RestGameClient("http://" + distantIp + ":8080");

            try {

                waitingDialog.setTitle(R.string.contacting_server);
                waitingDialog.setMessage(getString(R.string.please_wait));
                waitingDialog.show();
                textView.setText(getString(R.string.please_wait));

                runOnUiThread(new Runnable() {
                    public void run() {
                        waitingDialog.dismiss();
                        gameLayout.setVisibility(View.VISIBLE);
                        initUI(PlayingPlayerType.TWO);
                        Toast.makeText(JoinLanGameActivity.this,
                                getString(R.string.connection_ok), Toast.LENGTH_LONG).show();
                    }
                });

                disableClick();

            } catch (final Exception e) {
                showErrorMessage(e);
            }
            //
            joinAlreadyLaunched = true;
        }
    }

}

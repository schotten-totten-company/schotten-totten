package com.boardgames.bastien.schotten_totten;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.boardgames.bastien.schotten_totten.server.RestGameClient;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

public class CreateOnlineGameBackgroundTask extends AsyncTask<Void, Void, Boolean> {

    private ProgressDialog waitingDialog;
    private LauncherActivity activity;
    private String gameName;

    public CreateOnlineGameBackgroundTask(final LauncherActivity activity, final String gameName) {
        waitingDialog = new ProgressDialog(activity);
        waitingDialog.setTitle(activity.getString(R.string.contacting_server));
        waitingDialog.setMessage(activity.getString(R.string.please_wait));
        this.activity = activity;
        this.gameName = gameName;
    }

    @Override
    protected void onPreExecute() {
        waitingDialog.setCanceledOnTouchOutside(false);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        final Intent joinIntent = new Intent(activity, ServerGameActivity.class);
        joinIntent.putExtra(activity.getString(R.string.game_name_key), gameName);
        joinIntent.putExtra(activity.getString(R.string.server_url_key), activity.onlineUrl);
        joinIntent.putExtra(activity.getString(R.string.type_key), PlayingPlayerType.ONE.toString());
        activity.startActivity(joinIntent);
        if (waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        final RestGameClient restGameClient = new RestGameClient(activity.onlineUrl, gameName);
        restGameClient.createGame();
        return Boolean.TRUE;
    }

}

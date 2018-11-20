package com.boardgames.bastien.schotten_totten;

import android.content.Intent;

import com.boardgames.bastien.schotten_totten.server.RestGameClient;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

public class CreateOnlineGameBackgroundTask extends AbstractBackgroundTask {

    protected final String gameName;

    public CreateOnlineGameBackgroundTask(LauncherActivity activity, String gameName) {
        super(activity);
        this.gameName = gameName;
    }

    @Override
    protected void onPostExecute(String result) {
        final Intent joinIntent = new Intent(activity, ServerGameActivity.class);
        joinIntent.putExtra(activity.getString(R.string.game_name_key), gameName);
        joinIntent.putExtra(activity.getString(R.string.server_url_key), ((LauncherActivity)activity).onlineUrl);
        joinIntent.putExtra(activity.getString(R.string.type_key), PlayingPlayerType.ONE.toString());
        activity.startActivity(joinIntent);
        if (waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        final RestGameClient restGameClient = new RestGameClient(((LauncherActivity)activity).onlineUrl, gameName);
        restGameClient.createGame();
        return "";
    }

}

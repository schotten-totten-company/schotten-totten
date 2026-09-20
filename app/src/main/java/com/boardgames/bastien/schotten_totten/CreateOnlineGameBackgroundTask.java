package com.boardgames.bastien.schotten_totten;

import android.app.Activity;
import android.content.Intent;

import com.boardgames.bastien.schotten_totten.server.RestGameClient;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

public class CreateOnlineGameBackgroundTask extends AbstractBackgroundTask {

    protected final String gameName;
    protected final String onlineUrl;

    public CreateOnlineGameBackgroundTask(Activity activity, String onlineUrl, String gameName) {
        super(activity);
        this.gameName = gameName;
        this.onlineUrl = onlineUrl;
    }

    @Override
    protected void onSuccess(String result) {
        final Activity activity = activityRef.get();
        final Intent joinIntent = new Intent(activity, ServerGameActivity.class);
        joinIntent.putExtra(activity.getString(R.string.game_name_key), gameName);
        joinIntent.putExtra(activity.getString(R.string.server_url_key), this.onlineUrl);
        joinIntent.putExtra(activity.getString(R.string.type_key), PlayingPlayerType.ONE.toString());
        activity.startActivity(joinIntent);
        if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    @Override
    protected String doInBackground() {
        final RestGameClient restGameClient = new RestGameClient(this.onlineUrl, gameName);
        restGameClient.createGame();
        return "";
    }

}

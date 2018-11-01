package com.boardgames.bastien.schotten_totten;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.boardgames.bastien.schotten_totten.server.RestGameClient;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

public class WaitingBackgroundTask extends AsyncTask<Void, Void, String> {

    protected final int delay;
    protected final ProgressDialog w;
    protected final ServerGameActivity activity;


    public WaitingBackgroundTask(final ServerGameActivity activity, final int delay) {
        w = new ProgressDialog(activity);
        w.setTitle(activity.getString(R.string.contacting_server));
        w.setMessage(activity.getString(R.string.please_wait));
        this.activity = activity;
        this.delay = delay;
    }

    @Override
    protected void onPreExecute() {
        w.setCanceledOnTouchOutside(false);
        w.setCancelable(false);
        w.show();
    }

    @Override
    protected void onPostExecute(String result) {
        if (w.isShowing()) {
            w.dismiss();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

}

package com.boardgames.bastien.schotten_totten;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;

import com.boardgames.bastien.schotten_totten.server.RestGameClient;
import com.boradgames.bastien.schotten_totten.core.model.PlayingPlayerType;

public class WaitingBackgroundTask extends AsyncTask<Void, Void, String> {

    protected final int delay;
    protected final ProgressDialog waitingDialog;
    protected final ServerGameActivity activity;


    public WaitingBackgroundTask(final ServerGameActivity activity, final int delay) {
        waitingDialog = new ProgressDialog(activity);
        waitingDialog.setTitle(activity.getString(R.string.contacting_server));
        waitingDialog.setMessage(activity.getString(R.string.please_wait));
        this.activity = activity;
        this.delay = delay;
    }

    @Override
    protected void onPreExecute() {
        waitingDialog.setCanceledOnTouchOutside(false);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }

    @Override
    protected void onPostExecute(String result) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        return "";
    }

}

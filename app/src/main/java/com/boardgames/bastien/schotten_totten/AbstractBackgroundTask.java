package com.boardgames.bastien.schotten_totten;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public abstract class AbstractBackgroundTask extends AsyncTask<Void, Void, String> {

    protected final ProgressDialog waitingDialog;
    protected final LauncherActivity activity;

    public AbstractBackgroundTask(final LauncherActivity activity) {
        waitingDialog = new ProgressDialog(activity);
        waitingDialog.setTitle(activity.getString(R.string.contacting_server));
        waitingDialog.setMessage(activity.getString(R.string.please_wait));
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        waitingDialog.setCanceledOnTouchOutside(false);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }

}

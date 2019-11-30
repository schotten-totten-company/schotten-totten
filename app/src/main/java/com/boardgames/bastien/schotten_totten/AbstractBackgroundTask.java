package com.boardgames.bastien.schotten_totten;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.WindowManager;

public abstract class AbstractBackgroundTask extends AsyncTask<Void, Void, String> {

    protected final ProgressDialog waitingDialog;
    protected final Activity activity;

    public AbstractBackgroundTask(final Activity activity) {
        waitingDialog = new ProgressDialog(activity, R.style.CustomAlertDialog);
        waitingDialog.setTitle(activity.getString(R.string.contacting_server));
        waitingDialog.setMessage(activity.getString(R.string.please_wait));
        waitingDialog.getWindow().setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        waitingDialog.setCanceledOnTouchOutside(false);
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }

    @Override
    protected void onPostExecute(String result) {
        if (waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
        activity.finish();
    }

}

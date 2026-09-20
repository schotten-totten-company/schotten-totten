package com.boardgames.bastien.schotten_totten;

import android.app.Activity;

public class WaitingBackgroundTask extends AbstractBackgroundTask {

    protected final int delay;


    public WaitingBackgroundTask(final GameActivity activity, final int delay) {
        super(activity);
        this.delay = delay;
    }

    @Override
    protected String doInBackground() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            final Activity activity = activityRef.get();
            ((GameActivity)activity).showErrorMessage(e);
        }
        return "";
    }

    @Override
    protected void onSuccess(String result) {
        dismissDialog();
        final Activity activity = activityRef.get();
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            activity.finish();
        }
    }

}

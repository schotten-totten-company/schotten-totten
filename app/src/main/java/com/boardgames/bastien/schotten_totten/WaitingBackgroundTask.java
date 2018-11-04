package com.boardgames.bastien.schotten_totten;

public class WaitingBackgroundTask extends AbstractBackgroundTask {

    protected final int delay;


    public WaitingBackgroundTask(final GameActivity activity, final int delay) {
        super(activity);
        this.delay = delay;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        activity.finish();
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            ((GameActivity)activity).showErrorMessage(e);
        }
        return "";
    }

}

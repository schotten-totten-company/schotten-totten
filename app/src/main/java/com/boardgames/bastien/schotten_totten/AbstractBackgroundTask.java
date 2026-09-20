package com.boardgames.bastien.schotten_totten;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AbstractBackgroundTask {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final Handler MAIN_HANDLER = new Handler(Looper.getMainLooper());

    protected final WeakReference<Activity> activityRef;
    protected AlertDialog waitingDialog;

    public AbstractBackgroundTask(@NonNull final Activity activity) {
        this.activityRef = new WeakReference<>(activity);

        if (!activity.isFinishing() && !activity.isDestroyed()) {
            final View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_progress, null);
            final TextView messageView = dialogView.findViewById(R.id.dialog_message);
            if (messageView != null) {
                messageView.setText(activity.getString(R.string.please_wait));
            }

            this.waitingDialog = new AlertDialog.Builder(activity)
                    .setTitle(activity.getString(R.string.contacting_server))
                    .setView(dialogView)
                    .setCancelable(false)
                    .create();
        }
    }

    protected abstract String doInBackground() throws Exception;

    public void execute() {
        Activity activity = activityRef.get();
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        showDialog();

        CompletableFuture.supplyAsync(() -> {
            try {
                return doInBackground();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }, EXECUTOR).thenAccept(result -> {
            MAIN_HANDLER.post(() -> onSuccess(result));
        }).exceptionally(throwable -> {
            MAIN_HANDLER.post(() -> onError(throwable.getCause()));
            return null;
        });
    }

    private void showDialog() {
        if (waitingDialog != null && !waitingDialog.isShowing()) {
            waitingDialog.setCanceledOnTouchOutside(false);
            waitingDialog.show();
        }
    }

    protected void dismissDialog() {
        if (waitingDialog != null && waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    protected void onSuccess(String result) {
        dismissDialog();
    }

    protected void onError(Throwable error) {
        dismissDialog();
    }
}
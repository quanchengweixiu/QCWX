package com.funyoung.quickrepair.utils;

import android.os.AsyncTask;

/**
 * Created by yangfeng on 13-9-3.
 */
public class AsyncTaskUtils {
    private AsyncTaskUtils() {

    }
    public static boolean isReadyToRun(AsyncTask task) {
        if (null == task || task.getStatus() == AsyncTask.Status.RUNNING) {
            return false;
        }
        return true;
    }
}

/**
 +--------------------------------------------------------------------+
 | WriteFileAsync.java
 +--------------------------------------------------------------------+
 | Copyright DarkOverlordOfData (c) 2014
 +--------------------------------------------------------------------+
 |
 | This file is a part of Malleus
 |
 | Malleus is free software; you can copy, modify, and distribute
 | it under the terms of the MIT License
 |
 +--------------------------------------------------------------------+
 */
package com.darkoverlordofdata.malleus;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

/**
 * An Async task to clean up the malleus files
 *
 *
 */
public class DeleteFileAsync extends AsyncTask<Void, Integer, String> {

    private DeviceModel         model;
    private HammerFragment      view;
    private HammerActivity      ctrl;

    /**
     * Acts as the constructor to inject dependencies
     *
     * @param ctrl
     * @param view
     * @return
     */
    DeleteFileAsync inject(DeviceModel model, HammerFragment view, HammerActivity ctrl) {

        this.model = model;
        this.view = view;
        this.ctrl = ctrl;
        return this;
    }

    /**
     * Fill up the hard drives with random bytes.
     * 
     * @param aurl
     * @return
     */
    @Override
    protected String doInBackground(Void... params) {

        Context ctx = ctrl.getApplicationContext();

        /**
         * Delete from Internal Storage
         */
        try {
            if (ctx.deleteFile(HammerActivity.FILENAME)) {
                if (HammerActivity.BETA)
                    Log.i("DeleteFileAsync", "deleted "+HammerActivity.FILENAME);
            } else {
                if (HammerActivity.BETA)
                    Log.i("DeleteFileAsync", HammerActivity.FILENAME + " Not Found");
            }
        } catch (Exception e) {
            Log.e("DeleteFileAsync", e.getMessage());
        }

        /**
         * Delete from SD Card / External Storage
         */
        for (int i=1; i<model.path.length; i++) {
            if (model.isAvail[i]) {
                File file = new File(model.path[i]+ "/" + HammerActivity.FILENAME);
                try {
                    if (file.exists()) {
                        file.delete();
                        if (HammerActivity.BETA)
                            Log.i("DeleteFileAsync", "deleted "+model.path[i]+ "/" + HammerActivity.FILENAME);
                    } else {
                        if (HammerActivity.BETA)
                            Log.i("DeleteFileAsync", model.path[i]+ "/" + HammerActivity.FILENAME + " Not Found");

                    }
                } catch (Exception e) {
                    Log.e("DeleteFileAsync", e.getMessage());
                }
            }
        }
        return null;
    }


    /**
     * Before: Display the Progress Bar
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ctrl.showDialog(HammerActivity.DIALOG_DELETE_PROGRESS);
    }

    /**
     * During: Update the progressbar
     * @param progress
     */
    protected void onProgressUpdate(Integer... progress) {
        ctrl.progress.setProgress(progress[0]);
    }

    /**
     * After: Kill the progressbar
     * @param result
     */
    @Override
    protected void onPostExecute(String result) {
        ctrl.dismissDialog(HammerActivity.DIALOG_DELETE_PROGRESS);
    }


}
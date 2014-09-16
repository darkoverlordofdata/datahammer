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
 * Delete the malleus files
 *
 *
 */
public class DeleteFileAsync extends AsyncTask<Void, Integer, String> {

    final String FILENAME   = "darkoverlordofdata.malleus";

    DeviceModel model;
    HammerFragment view;
    HammerActivity ctrl;

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
        String path = FILENAME;
        File file = new File(model.extPath + "/" + FILENAME);

        /**
         * Delete from Internal Storage
         */
        try {
            ctx.deleteFile(path);
        } catch (Exception e) {
            Log.e("doInBackground", e.getMessage());
        }

        /**
         * Delete from SD Card / External Storage
         */
        try {
            if (file.exists())
                file.delete();
        } catch (Exception e) {
            Log.e("doInBackground", e.getMessage());
        }

        return null;
    }


    /**
     * Before: Display the Progress Bar
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        ctrl.showDialog(HammerActivity.DIALOG_WRITE_PROGRESS);
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
        ctrl.dismissDialog(HammerActivity.DIALOG_WRITE_PROGRESS);
        view.status.setText("Malleus temporary work files deleted.");
    }


}
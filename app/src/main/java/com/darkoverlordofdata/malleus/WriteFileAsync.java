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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * Write the files in an async task
 */
public class WriteFileAsync extends AsyncTask<Void, Integer, String> {

    DeviceModel model;
    HammerFragment view;
    HammerActivity ctrl;
    Random rnd;
    byte[] buffer = new byte[1024];

    /**
     * Acts as the constructor to inject dependencies
     *
     * @param ctrl
     * @param view
     * @return
     */
    WriteFileAsync inject(DeviceModel model, HammerFragment view, HammerActivity ctrl) {

        this.model = model;
        this.view = view;
        this.ctrl = ctrl;
        this.rnd = new Random();
        return this;
    }

    /**
     * Write files
     * 
     * @param aurl
     * @return
     */
    @Override
    protected String doInBackground(Void... params) {

        String path = "malleus.txt";
        Context ctx = ctrl.getApplicationContext();
        boolean ok = false;
        OutputStream bw = null;
        FileOutputStream fw;
        int k = 0;

        /**
         * Internal Storage
         */
        try {
            fw = ctx.openFileOutput(path, Context.MODE_PRIVATE);
            bw = new BufferedOutputStream(fw);
            ok = true;
        } catch (IOException e) {
            Log.e("doInBackground", e.getMessage());
        }

        /** Keep writing until there is an error */
        try {
            while (ok) {
                new Random().nextBytes(buffer);
                bw.write(buffer);
                publishProgress((Integer) k++);
            }
        } catch (IOException e) {
            Log.i("doInBackground", "Finished writing "+k+" internal storage blocks");
        } catch (NullPointerException e) {
            Log.e("doInBackground", e.getMessage());
        }

        /** Close the file */
        if (ok) {
            try {
                bw.close();
            } catch (IOException e) {
                Log.e("doInBackground", e.getMessage());
            } catch (NullPointerException e) {
                Log.e("doInBackground", e.getMessage());
            }
        }

        if (!model.isWriteable) return null;
        /**
         * SD Card / External Storage
         */
        path = model.extPath + "/malleus.txt";
        ok = false;

        /** Open a file */
        try {
            File file = new File(path);
            if (!file.exists()) ok = file.createNewFile();
            fw = new FileOutputStream(file.getAbsoluteFile());
            bw = new BufferedOutputStream(fw);
            ok = true;
        } catch (IOException e) {
            Log.e("doInBackground", e.getMessage());
        }

        /** Keep writing until there is an error */
        try {
            while (ok) {
                new Random().nextBytes(buffer);
                bw.write(buffer);
                publishProgress((Integer) k++);
            }
        } catch (IOException e) {
            Log.i("doInBackground", "Finished writing "+k+" external storage blocks");
        } catch (NullPointerException e) {
            Log.e("doInBackground", e.getMessage());
        }

        /** Close the file */
        if (ok) {
            try {
                bw.close();
            } catch (IOException e) {
                Log.e("doInBackground", e.getMessage());
            } catch (NullPointerException e) {
                Log.e("doInBackground", e.getMessage());
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
    }


}
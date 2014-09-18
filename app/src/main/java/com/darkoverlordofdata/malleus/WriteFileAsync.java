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
 * An Async task to write the malleus files
 *
 *
 */
public class WriteFileAsync extends AsyncTask<Void, Integer, String> {

    private int                 total;
    private long                startTime;
    private long                endTime;
    private byte[]              buffer = new byte[HammerActivity.CHUNK_SIZE];
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
    WriteFileAsync inject(DeviceModel model, HammerFragment view, HammerActivity ctrl) {

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

        startTime = System.currentTimeMillis();
        total = 0;

        if (model.isAvail[0]) {
            total += writeInternal();
        }
        for (int i=1; i<model.path.length; i++) {
            if (model.isAvail[i]) {
                total += writeExternal(model.path[i]);
            }
        }
        endTime = System.currentTimeMillis();
        return null;
    }

    /**
     * Write Internal Storage
     */
    private int writeInternal() {

        Context ctx = ctrl.getApplicationContext();
        FileOutputStream fw = null;
        OutputStream bw = null;
        String path = HammerActivity.FILENAME;
        int count = 0;

        try {
            ctx.deleteFile(path);
        } finally {

            try {
                /**
                 * Try to open a file
                 * */
                try {
                    fw = ctx.openFileOutput(path, Context.MODE_PRIVATE);
                    bw = new BufferedOutputStream(fw);
                    if (HammerActivity.BETA) 
                        Log.i("WriteFileAsync", path + " Created");
                } catch (IOException e) {
                    Log.e("doInBackground", e.getMessage());
                }
                /**
                 * Write random bytes until we
                 * run out of space on the disk
                 * */
                try {
                    while (!(bw == null)) {
                        new Random().nextBytes(buffer);
                        bw.write(buffer);
                        fw.getFD().sync();
                        publishProgress((Integer) count++);
                        if (HammerActivity.BETA) 
                            if (count > HammerActivity.THROTTLE)
                                break;
                    }
                } catch (IOException e) {
                    if (HammerActivity.BETA)
                        Log.i("doInBackground", "Finished writing " + count + " internal storage blocks");
                } catch (NullPointerException e) {
                    Log.e("doInBackground", e.getMessage());
                }

                /**
                 * Close the file
                 * */
                if (!(bw == null)) {
                    try {
                        /**
                         * The close method of OutputStream does nothing.
                         * Just close the FileOutputStream.
                         */
                        fw.close();
                    } catch (IOException e) {
                        if (HammerActivity.BETA) 
                            Log.i("doInBackground", e.getMessage());
                    } catch (NullPointerException e) {
                        Log.e("doInBackground", e.getMessage());
                    }
                }
            } finally {
                if (HammerActivity.FREE_MEMORY) {
                    //ctx.deleteFile(path);
                    if (HammerActivity.BETA) 
                        Log.i("WriteFileAsync", path + " Deleted");
                }
            }
        }
        return count;
    }

    /**
     * Write SD Card / External Storage
     */
    private int writeExternal(String path) {

        int count = 0;
        OutputStream bw = null;
        FileOutputStream fw = null;
        File file = new File(path + "/" + HammerActivity.FILENAME);

        try {
            if (file.exists())
                file.delete();
        } finally {
            try {
                /**
                 * Try to open a file
                 * */
                try {
                    if (!file.exists()) file.createNewFile();
                    fw = new FileOutputStream(file.getAbsoluteFile());
                    bw = new BufferedOutputStream(fw);
                    if (HammerActivity.BETA) 
                        Log.i("WriteFileAsync", path + "/" + HammerActivity.FILENAME + " Created");
                } catch (IOException e) {
                    Log.e("doInBackground", e.getMessage());
                }

                /**
                 * Write random bytes until we
                 * run out of space on the disk
                 * */
                try {
                    while (!(bw == null)) {
                        new Random().nextBytes(buffer);
                        bw.write(buffer);
                        fw.getFD().sync();
                        publishProgress((Integer) count++);
                        if (HammerActivity.BETA) 
                            if (count > HammerActivity.THROTTLE)
                                break;
                    }
                } catch (IOException e) {
                    if (HammerActivity.BETA) 
                        Log.i("doInBackground", "Finished writing " + count + " external storage blocks");
                } catch (NullPointerException e) {
                    Log.e("doInBackground", e.getMessage());
                }

                /**
                 * Close the file
                 * */
                if (!(bw == null)) {
                    try {
                        /**
                         * The close method of OutputStream does nothing.
                         * Just close the FileOutputStream.
                         */
                        fw.close();
                    } catch (IOException e) {
                        if (HammerActivity.BETA) 
                            Log.i("doInBackground", e.getMessage());
                    } catch (NullPointerException e) {
                        Log.e("doInBackground", e.getMessage());
                    }
                }
            } finally {
                if (HammerActivity.FREE_MEMORY) {
                    if (file.exists()) {
                        //file.delete();
                        if (HammerActivity.BETA) 
                            Log.i("WriteFileAsync", path + "/" + HammerActivity.FILENAME + " Deleted");
                    }
                }
            }
        }
        return count;
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
        if (HammerActivity.BETA) 
            Log.i("onProgressUpdate", ""+progress[0]);
    }

    /**
     * After: Kill the progressbar
     * @param result
     */
    @Override
    protected void onPostExecute(String result) {
        long seconds = (endTime - startTime) / 1000;
        String duration = String.format("%02d:%02d", seconds / 60, seconds % 60);

        ctrl.dismissDialog(HammerActivity.DIALOG_WRITE_PROGRESS);
        view.status.setText("Shredded "+(total*4)+"kb in "+duration+" mm:ss");
        if (HammerActivity.BETA) 
            Log.i("onPostExecute", "Count = "+total);
    }


}
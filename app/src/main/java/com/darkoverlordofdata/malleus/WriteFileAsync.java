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
 *
 *
 */
public class WriteFileAsync extends AsyncTask<Void, Integer, String> {

    final String FILENAME   = "malleus.d16a";   //  write to filename
    final int CHUNK_SIZE    = 4096;             //  write out sector sized chunks
    boolean freeMemory      = true;             //  delete the files when done

    byte[] buffer = new byte[CHUNK_SIZE];
    DeviceModel model;
    HammerFragment view;
    HammerActivity ctrl;
    Random rnd;

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
     * Fill up the hard drives with random bytes.
     * 
     * @param aurl
     * @return
     */
    @Override
    protected String doInBackground(Void... params) {

        if (model.isWriteable) {
            try {
                writeExternal();
            } finally {

            }
        }
        writeInternal();
        return null;
    }

    /**
     * Write Internal Storage
     */
    private int writeInternal() {

        Context ctx = ctrl.getApplicationContext();
        FileOutputStream fw = null;
        OutputStream bw = null;
        String path = FILENAME;
        int count = 0;

        try {
            /**
             * Try to open a file
             * */
            try {
                fw = ctx.openFileOutput(path, Context.MODE_PRIVATE);
                bw = new BufferedOutputStream(fw);
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
                }
            } catch (IOException e) {
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
                     * @see http://docs.oracle.com/javase/7/docs/api/java/io/OutputStream.html#method_detail
                     * The close method of OutputStream does nothing.
                     */
                    fw.close();
                } catch (IOException e) {
                    Log.i("doInBackground", e.getMessage());
                } catch (NullPointerException e) {
                    Log.e("doInBackground", e.getMessage());
                }
            }
        } finally {
            if (freeMemory) {
                ctx.deleteFile(path);
            }
        }
        return count;
    }

    /**
     * Write SD Card / External Storage
     */
    private int writeExternal() {

        FileOutputStream fw = null;
        OutputStream bw = null;
        String path = model.extPath + "/" + FILENAME;
        int count = 0;
        File file = new File(path);

        try {
            /**
             * Try to open a file
             * */
            try {
                if (!file.exists()) file.createNewFile();
                fw = new FileOutputStream(file.getAbsoluteFile());
                bw = new BufferedOutputStream(fw);
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
                }
            } catch (IOException e) {
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
                     * @see http://docs.oracle.com/javase/7/docs/api/java/io/OutputStream.html#method_detail
                     * The close method of OutputStream does nothing.
                     */
                    fw.close();
                } catch (IOException e) {
                    Log.i("doInBackground", e.getMessage());
                } catch (NullPointerException e) {
                    Log.e("doInBackground", e.getMessage());
                }
            }
        } finally {
            if (freeMemory) {
                if (file.exists())
                    file.delete();
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
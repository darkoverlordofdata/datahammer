/**
 +--------------------------------------------------------------------+
 | WriteRandomAsync.java
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

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

/**
 * An Async task to write the malleus files
 *
 *
 */
public class WriteRandomAsync extends AsyncTask<Void, Integer, String> {

    private int                 total;
    private long                startTime;
    private long                endTime;
    private byte[]              buffer = new byte[HammerActivity.PAGE_SIZE];
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
    WriteRandomAsync inject(DeviceModel model, HammerFragment view, HammerActivity ctrl) {

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
        for (int i = 1; i < model.path.length; i++) {
            if (model.isAvail[i]) {
                total += writeExternal(model.path[i]);
            }
        }
        endTime = System.currentTimeMillis();
        notifyDone();
        return null;
    }


    /**
     * Notify that we are done, if that is possible.
     */
    private void notifyDone() {

        NotificationManager nm = (NotificationManager) ctrl.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        Notification done = new Notification();
        done.ledARGB = Color.argb(255, 0, 255, 0);
        done.flags |= Notification.FLAG_SHOW_LIGHTS;
        done.flags |= Notification.FLAG_AUTO_CANCEL;
        done.ledOnMS = 1;
        done.ledOffMS = 0;
        done.defaults = Notification.DEFAULT_ALL;

        nm.notify(null, 0xd16a, done);

    }

    /**
     * Write Internal Storage
     */
    private int writeInternal() {

        Context ctx = ctrl.getApplicationContext();
        RandomAccessFile output = null;
        File file = ctx.getFileStreamPath(HammerActivity.FILENAME);
        long s = file.length();
        if (HammerActivity.BETA)
            Log.i("WriteRandomAsync", "file.length = "+s);

        try {
            if (!file.exists()) {
                FileOutputStream fs = ctx.openFileOutput(HammerActivity.FILENAME, Context.MODE_PRIVATE);
                fs.close();
                if (HammerActivity.BETA)
                    Log.i("WriteRandomAsync", file.getAbsolutePath() + " Created");
            }
            // open for random access
            output = new RandomAccessFile(file.getAbsolutePath(), "rw");
            if (HammerActivity.BETA)
                Log.i("WriteRandomAsync", file.getAbsolutePath() + " Opened");
        } catch (IOException e) {
            Log.e("doInBackground", e.getMessage());
        }
        return shred(file, output);
    }

    /**
     * Write SD Card / External Storage
     */
    private int writeExternal(String store) {

        Context ctx = ctrl.getApplicationContext();
        RandomAccessFile output = null;
        File file = new File(store + "/" + HammerActivity.FILENAME);

        try {
            if (!file.exists()) file.createNewFile();
            output = new RandomAccessFile(file.getAbsolutePath(), "rw");
            if (HammerActivity.BETA)
                Log.i("WriteRandomAsync", file.getAbsolutePath() + " Created");
        } catch (IOException e) {
            Log.e("doInBackground", e.getMessage());
        }
        return shred(file, output);
    }


    /**
     * Shred
     */
    private int shred(File file, RandomAccessFile output) {

        int count = 0;
        try {

            /**
             * Write random bytes until we
             * run out of space on the disk
             * */
            try {
                new Random().nextBytes(buffer);

                if (HammerActivity.BETA)
                    Log.i("WriteRandomAsync", "bytes: "+output.length());
                output.skipBytes((int)output.length());

                while (!(output == null)) {
                    output.write(buffer, 0, HammerActivity.PAGE_SIZE);
                    publishProgress((Integer) count++);
                    if (HammerActivity.THROTTLE > 0)
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
            if (!(output == null)) {
                try {
                    output.close();
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
                    file.delete();
                    if (HammerActivity.BETA)
                        Log.i("WriteRandomAsync", file.getAbsolutePath() + " Deleted");
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

        if ((progress[0] % HammerActivity.PROGRESS) == 0) {
            ctrl.progress.setProgress(progress[0]);
            if (HammerActivity.BETA)
                Log.i("onProgressUpdate", ""+progress[0]);
        }

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
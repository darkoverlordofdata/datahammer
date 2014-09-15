package com.darkoverlordofdata.malleus;

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

        try {
            String path;

            for (int i=0; i<1024; i++) {

                path = model.extPath + "/Download/malleus/file" + i + ".txt";
                new Random().nextBytes(buffer);

                File file = new File(path);
                // If file does not exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream fw = new FileOutputStream(file.getAbsoluteFile());
                OutputStream bw = new BufferedOutputStream(fw);
                bw.write(buffer);
                bw.close();
                publishProgress((Integer)i);
            }
        } catch (IOException e) {
            Log.e("error", e.getMessage());
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
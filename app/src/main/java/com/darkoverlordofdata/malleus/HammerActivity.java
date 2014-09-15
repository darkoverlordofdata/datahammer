package com.darkoverlordofdata.malleus;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


/**
 * Hammer Activity
 *
 * This is the main program
 *
 */
public class HammerActivity extends ActionBarActivity {

    public static final int DIALOG_WRITE_PROGRESS = 0;
    ProgressDialog progress;
    HammerFragment view;
    DeviceModel model = new DeviceModel();

    /**
     * Initialize the main screen
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hammer);
        if (savedInstanceState == null) {
            view = new HammerFragment();
            Bundle args = new Bundle();
            args.putSerializable("model", model);
            view.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, view)
                    .commit();
        }
    }


    /**
     * Initialize the main menu
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.hammer, menu);
        return true;
    }

    /**
     * Menu Action
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initialize the Progress Bar
     *
     * @param id
     * @return
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_WRITE_PROGRESS:
                progress = new ProgressDialog(this);
                progress.setMessage("Hammering the hard drive..");
                progress.setIndeterminate(false);
                progress.setMax(1024);
                progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progress.setCancelable(false);
                progress.show();
                return progress;
            default:
                return null;
        }
    }

    /**
     * It's Hammer Time!
     *
     * @param view
     */
    public void hammerTime(View v) {

        WriteFileAsync task = new WriteFileAsync().inject(model, view, this);
        task.execute();
    }

}

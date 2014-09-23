/**
 +--------------------------------------------------------------------+
 | HammerActivity.java
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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


/**
 * Hammer Activity
 *
 * This is the main program
 *
 */
public class HammerActivity extends ActionBarActivity {

    public static final int         DIALOG_WRITE_PROGRESS   = 0;

    /**
     * Configuration
     */
    public static final int         THROTTLE                = 0;        // limit # of blocks to process
    public static final int         PROGRESS                = 10000;    // progress update modulo
    public static final boolean     BETA                    = false;    // log status messages
    public static final boolean     FREE_MEMORY             = true;     // delete the files when we're done
    public static final int         PAGE_SIZE               = 1024 * 4;
    public static final String      FILENAME                = "darkoverlordofdata.malleus";


    private DeviceModel         model;
    private HammerFragment      view;
    public ProgressDialog       progress;

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
            model = new DeviceModel(this);
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
        if (id == R.id.action_about) {
            showAbout();
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

                progress = new ProgressDialog(this, R.style.MalleusDialog);
                progress.setMessage("Shredding...");
                progress.setIndeterminate(false);
                progress.setMax(model.shredBlocks);
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

        final HammerActivity self = this;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        WriteRandomAsync task = new WriteRandomAsync().inject(model, view, self);
                        task.execute();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        String message =
                "It will take about "+model.estimatedTime()+
                " to shred "+model.totalSpace()+
                ". Do you want to continue?";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    protected void showAbout() {


        // Inflate the about message contents
        View messageView = getLayoutInflater().inflate(R.layout.about, null, false);

        // When linking text, force to always use default color. This works
        // around a pressed color state bug.
        TextView textView = (TextView) messageView.findViewById(R.id.about_credits);
        int defaultColor = textView.getTextColors().getDefaultColor();
        textView.setTextColor(defaultColor);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(R.string.app_name);
        builder.setView(messageView);
        builder.create();
        builder.show();
    }

}

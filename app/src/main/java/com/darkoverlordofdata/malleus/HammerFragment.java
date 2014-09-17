/**
 +--------------------------------------------------------------------+
 | HammerFragment.java
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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Hammer Fragment binds the model data to the main view
 */
public class HammerFragment extends Fragment {


    TextView intStore;
    TextView intTotal;
    TextView intUsed;
    TextView intFree;

    TextView extStore;
    TextView extTotal;
    TextView extUsed;
    TextView extFree;
    TextView status;

    DeviceModel model;


    /**
     * Update the main view
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hammer, container, false);
        model = (DeviceModel)getArguments().getSerializable("model");

        /* Bind all of the controls */

        intStore   = (TextView)rootView.findViewById(R.id.int_title);
        intTotal   = (TextView)rootView.findViewById(R.id.int_total_value);
        intUsed    = (TextView)rootView.findViewById(R.id.int_used_value);
        intFree    = (TextView)rootView.findViewById(R.id.int_free_value);

        extStore    = (TextView)rootView.findViewById(R.id.ext_title);
        extTotal    = (TextView)rootView.findViewById(R.id.ext_total_value);
        extUsed     = (TextView)rootView.findViewById(R.id.ext_used_value);
        extFree     = (TextView)rootView.findViewById(R.id.ext_free_value);

        status      = (TextView)rootView.findViewById(R.id.status_value);

        intStore.setText(model.path[0]);
        intTotal.setText(model.totalKb[0]);
        intUsed.setText(model.usedKb[0]);
        intFree.setText(model.freeKb[0]);

        extStore.setText(model.path[1]);
        extTotal.setText(model.totalKb[1]);
        extUsed.setText(model.usedKb[1]);
        extFree.setText(model.freeKb[1]);

        return rootView;
    }
}

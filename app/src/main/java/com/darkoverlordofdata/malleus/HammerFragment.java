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
import android.widget.ListView;
import android.widget.TextView;

/**
 * Hammer Fragment binds the model data to the main view
 */
public class HammerFragment extends Fragment {

    public TextView             analysis;
    public TextView             status;
    private DeviceModel         model;

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
        ListView list = (ListView)rootView.findViewById(R.id.list);
        analysis    = (TextView)rootView.findViewById(R.id.analysis_value);
        status      = (TextView)rootView.findViewById(R.id.status_value);

        StorageList adapter = new StorageList(getActivity(), model);

        list.setAdapter(adapter);

        analysis.setText(model.analysis());

        return rootView;
    }

    public void reselect() {
        analysis.setText(model.analysis());
    }
}

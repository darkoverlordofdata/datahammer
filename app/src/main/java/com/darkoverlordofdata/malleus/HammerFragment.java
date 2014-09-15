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


    TextView rootStore;
    TextView rootTotal;
    TextView rootUsed;
    TextView rootFree;
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

        rootStore   = (TextView)rootView.findViewById(R.id.root_title);
        rootTotal   = (TextView)rootView.findViewById(R.id.root_total_value);
        rootUsed    = (TextView)rootView.findViewById(R.id.root_used_value);
        rootFree    = (TextView)rootView.findViewById(R.id.root_free_value);

        extStore    = (TextView)rootView.findViewById(R.id.ext_title);
        extTotal    = (TextView)rootView.findViewById(R.id.ext_total_value);
        extUsed     = (TextView)rootView.findViewById(R.id.ext_used_value);
        extFree     = (TextView)rootView.findViewById(R.id.ext_free_value);

        status      = (TextView)rootView.findViewById(R.id.status_value);

        rootStore.setText(model.rootPath);
        rootTotal.setText(model.rootTotalKb);
        rootUsed.setText(model.rootUsedKb);
        rootFree.setText(model.rootFreeKb);

        extStore.setText(model.extPath);
        extTotal.setText(model.extTotalKb);
        extUsed.setText(model.extUsedKb);
        extFree.setText(model.extFreeKb);
        status.append("External Media:"
                +" readable="+model.externalStorageAvailable
                +", writable="+model.externalStorageWriteable);

        return rootView;
    }



}

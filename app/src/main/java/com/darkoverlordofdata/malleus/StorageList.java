package com.darkoverlordofdata.malleus;

import android.widget.ArrayAdapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


public class StorageList extends ArrayAdapter <String> {

    private final Activity context;
    private final DeviceModel model;


    /**
     *
     * @param context
     * @param model
     */
    public StorageList(Activity context, DeviceModel model) {
        super(context, R.layout.storage, model.path);
        this.context = context;
        this.model = model;
    }

    /**
     *
     * @param position
     * @param view
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final int index = position;

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.storage, null, true);
        TextView pathText = (TextView) rowView.findViewById(R.id.path);
        CheckBox shredFlag = (CheckBox) rowView.findViewById(R.id.shred);

        pathText.setText(model.store[position]);
        shredFlag.setChecked(model.isAvail[position]);

        shredFlag.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                model.isAvail[index] = arg1;
            }
        });

        return rowView;
    }
}

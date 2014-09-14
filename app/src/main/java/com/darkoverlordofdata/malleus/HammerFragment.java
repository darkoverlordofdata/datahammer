package com.darkoverlordofdata.malleus;

import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.text.DecimalFormat;

public class HammerFragment extends Fragment {

    TextView totalMemory;
    TextView freeMemory;
    TextView busyMemory;
    TextView extStorage;
    TextView filesList;

    public HammerFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hammer, container, false);

        totalMemory = (TextView)rootView.findViewById(R.id.total_value);
        totalMemory.setText(bytesToHuman(totalMemory()));

        freeMemory = (TextView)rootView.findViewById(R.id.free_value);
        freeMemory.setText(bytesToHuman(freeMemory()));

        busyMemory = (TextView)rootView.findViewById(R.id.busy_value);
        busyMemory.setText(bytesToHuman(busyMemory()));

        File extStore = Environment.getExternalStorageDirectory();

        extStorage = (TextView)rootView.findViewById(R.id.ext_value);
        extStorage.setText(extStore.getAbsolutePath());

        File file[] = Environment.getExternalStorageDirectory().listFiles();
        String file_list = "";
//        try {
//            file_list = "" + file.length + " files";
//        }
//        catch (Exception e) {
//            file_list = e.toString();
//        }


        for (File f : file) {
            if (!f.isDirectory()) {
                file_list+= f.getName()+"\n";
            } else {
                file_list+= f.getName()+"\n";
            }
        }
        //file_list += file.length+" files";

        filesList = (TextView)rootView.findViewById(R.id.files_list);
        filesList.setText(file_list);
        filesList.setMovementMethod(new ScrollingMovementMethod());

        return rootView;
    }


    /*************************************************************************************************
     * http://stackoverflow.com/questions/7115016/how-to-find-the-amount-of-free-storage-disk-space-left-on-android/8826357#8826357
     Returns size in bytes.

     If you need calculate external memory, change this:
     StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
     to this:
     StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
     **************************************************************************************************/
    public long totalMemory()
    {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long   total  = (statFs.getBlockCount() * statFs.getBlockSize());
        return total;
    }

    public long freeMemory()
    {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long   free   = (statFs.getAvailableBlocks() * statFs.getBlockSize());
        return free;
    }

    public long busyMemory()
    {
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long   total  = (statFs.getBlockCount() * statFs.getBlockSize());
        long   free   = (statFs.getAvailableBlocks() * statFs.getBlockSize());
        long   busy   = total - free;
        return busy;
    }
    public static String floatForm (double d)
    {
        return new DecimalFormat("#.##").format(d);
    }


    public static String bytesToHuman (long size)
    {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)                 return floatForm(        size     ) + " byte";
        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " Kb";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " Mb";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " Gb";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " Tb";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " Pb";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " Eb";

        return "???";
    }
}

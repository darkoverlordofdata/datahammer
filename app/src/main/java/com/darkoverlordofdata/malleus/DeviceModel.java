package com.darkoverlordofdata.malleus;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;

public class DeviceModel implements Serializable {

    private static final long serialVersionUID = 0L;

    String rootPath;        //  path to folder
    long rootTotal;         //  total bytes available
    long rootUsed;          //  bytes used
    long rootFree;          //  bytes free
    String rootTotalKb;
    String rootUsedKb;
    String rootFreeKb;

    String extPath;         //  path to folder
    long extTotal;          //  total bytes available
    long extUsed;           //  bytes used
    long extFree;           //  bytes free
    String extTotalKb;
    String extUsedKb;
    String extFreeKb;

    boolean externalStorageAvailable = false;
    boolean externalStorageWriteable = false;

    public DeviceModel() {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            // Can't read or write
            externalStorageAvailable = externalStorageWriteable = false;
        }

        StatFs statFs;
        File root = Environment.getRootDirectory();
        statFs = new StatFs(rootPath = root.getAbsolutePath());
        rootTotal  = (statFs.getBlockCount() * statFs.getBlockSize());
        rootFree   = (statFs.getAvailableBlocks() * statFs.getBlockSize());
        rootUsed   = rootTotal - rootFree;
        rootTotalKb = bytesToHuman(rootTotal);
        rootFreeKb = bytesToHuman(rootFree);
        rootUsedKb = bytesToHuman(rootUsed);


        File ext = Environment.getExternalStorageDirectory();
        statFs = new StatFs(extPath = ext.getAbsolutePath());
        extTotal  = (statFs.getBlockCount() * statFs.getBlockSize());
        extFree   = (statFs.getAvailableBlocks() * statFs.getBlockSize());
        extUsed   = extTotal - extFree;
        extTotalKb = bytesToHuman(extTotal);
        extFreeKb = bytesToHuman(extFree);
        extUsedKb = bytesToHuman(extUsed);

    }

    public static String floatForm (double d) {
        return new DecimalFormat("#.00").format(d);
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

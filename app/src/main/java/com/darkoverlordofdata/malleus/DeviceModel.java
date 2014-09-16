/**
 +--------------------------------------------------------------------+
 | DeviceModel.java
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
    String rootTotalKb;     //  readable total bytes available
    String rootUsedKb;      //  readable bytes used
    String rootFreeKb;      //  readable bytes free

    String extPath;         //  path to folder
    long extTotal;          //  total bytes available
    long extUsed;           //  bytes used
    long extFree;           //  bytes free
    String extTotalKb;      //  readable total bytes available
    String extUsedKb;       //  readable bytes used
    String extFreeKb;       //  readable bytes free

    boolean isAvailable;    //  SD card available
    boolean isWriteable;    //  SD card writeable

    /**
     * DeviceModel
     * wraps the phone file system
     * 
     */
    public DeviceModel() {

        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            isAvailable = isWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            isAvailable = true;
            isWriteable = false;
        } else {
            // Can't read or write
            isAvailable = isWriteable = false;
        }

        StatFs statFs;
        File root = Environment.getRootDirectory();
        statFs = new StatFs(rootPath = root.getAbsolutePath());
        rootTotal  = (statFs.getBlockCount() * statFs.getBlockSize());
        rootFree   = (statFs.getAvailableBlocks() * statFs.getBlockSize());
        rootUsed   = rootTotal - rootFree;
        rootTotalKb = humanize(rootTotal);
        rootFreeKb = humanize(rootFree);
        rootUsedKb = humanize(rootUsed);


        File ext = Environment.getExternalStorageDirectory();
        statFs = new StatFs(extPath = ext.getAbsolutePath());
        extTotal  = (statFs.getBlockCount() * statFs.getBlockSize());
        extFree   = (statFs.getAvailableBlocks() * statFs.getBlockSize());
        extUsed   = extTotal - extFree;
        extTotalKb = humanize(extTotal);
        extFreeKb = humanize(extFree);
        extUsedKb = humanize(extUsed);

    }

    /**
     * Format with 2 decimal places
     *
     * @param d
     * @return
     */
    private static String format2(double d) {
        return new DecimalFormat("#.00").format(d);
    }


    /**
     * Humanize the file size
     *
     * @param size
     * @return
     */
    private static String humanize (long size)
    {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)                 return format2(        size     ) + " byte";
        if (size >= Kb && size < Mb)    return format2((double)size / Kb) + " Kb";
        if (size >= Mb && size < Gb)    return format2((double)size / Mb) + " Mb";
        if (size >= Gb && size < Tb)    return format2((double)size / Gb) + " Gb";
        if (size >= Tb && size < Pb)    return format2((double)size / Tb) + " Tb";
        if (size >= Pb && size < Eb)    return format2((double)size / Pb) + " Pb";
        if (size >= Eb)                 return format2((double)size / Eb) + " Eb";

        return "???";
    }

}

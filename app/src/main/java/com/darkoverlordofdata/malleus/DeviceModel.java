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

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.Serializable;
import java.text.DecimalFormat;

public class DeviceModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String TAIL = "/com.darkoverlordofdata.malleus/files";
    /**
     * Normalize the device storage locations:
     *
     *  [0]     = Internal Storage
     *  [1]     = Primary External Storage
     *  [2...]  = Secondary External Storage (starting with KitKat)
     */
    public long free[];            //  bytes free
    public long used[];            //  bytes used
    public long total[];           //  total bytes available
    public String path[];          //  absolute storage path
    public String store[];         //  absolute storage path
    public String freeKb[];        //  readable bytes free
    public String usedKb[];        //  readable bytes used
    public String totalKb[];       //  readable total bytes available
    public boolean isAvail[];      //  is it ready to use?

    /**
     * DeviceModel
     * wraps the phone file system
     * 
     */
    public DeviceModel(Context ctx) {

        int count = (Integer.valueOf(android.os.Build.VERSION.SDK) < 19)
                    ? 2 : ctx.getExternalFilesDirs(null).length+1;

        free    = new long[count];
        used    = new long[count];
        total   = new long[count];
        path    = new String[count];
        store   = new String[count];
        freeKb  = new String[count];
        usedKb  = new String[count];
        totalKb = new String[count];
        isAvail = new boolean[count];

        StatFs statFs;

        /**
         * Internal Storage
         */
        path[0]     = Environment.getDataDirectory().getAbsolutePath();
        statFs      = new StatFs(path[0]);

        store[0]    = path[0];
        total[0]    = (statFs.getBlockCount() * statFs.getBlockSize());
        free[0]     = (statFs.getAvailableBlocks() * statFs.getBlockSize());
        used[0]     = total[0] - free[0];
        freeKb[0]   = humanize(used[0]);
        usedKb[0]   = humanize(free[0]);
        totalKb[0]  = humanize(total[0]);
        isAvail[0]  = true;

        /**
         * External Storage
         */
        if (Integer.valueOf(android.os.Build.VERSION.SDK) < 19) {

            path[1]     = Environment.getExternalStorageDirectory().getAbsolutePath();
            statFs      = new StatFs(path[1]);


            store[1]    = path[1].replace(TAIL, "");
            total[1]    = (statFs.getBlockCount() * statFs.getBlockSize());
            free[1]     = (statFs.getAvailableBlocks() * statFs.getBlockSize());
            used[1]     = total[1] - free[1];
            freeKb[1]   = humanize(used[1]);
            usedKb[1]   = humanize(free[1]);
            totalKb[1]  = humanize(total[1]);

            String state = Environment.getExternalStorageState();
            isAvail[1] = Environment.MEDIA_MOUNTED.equals(state);

        } else { /** starting with KitKat */

            File ext[] = ctx.getExternalFilesDirs(null);
            for (int i=1; i<=ext.length; i++) {

                path[i]     = ext[i-1].getAbsolutePath();
                statFs      = new StatFs(path[i]);

                store[i]    = path[i].replace(TAIL, "");
                total[i]    = (statFs.getBlockCount() * statFs.getBlockSize());
                free[i]     = (statFs.getAvailableBlocks() * statFs.getBlockSize());
                used[i]     = total[i] - free[i];
                usedKb[i]   = humanize(free[i]);
                freeKb[i]   = humanize(used[i]);
                totalKb[i]  = humanize(total[i]);

                String state = Environment.getStorageState(ext[i-1]);
                isAvail[i] = Environment.MEDIA_MOUNTED.equals(state);

            }
        }

        if (HammerActivity.BETA)
            Log.i("DeviceModel", "SDK Version = "+android.os.Build.VERSION.SDK);

        if (Integer.valueOf(android.os.Build.VERSION.SDK) > 10) {
            /**
             * Emulated primary:
             * This means that we only need to write over the internal
             * storage because they both map to the same memory card.
             */
            if (Environment.isExternalStorageEmulated()) {
                isAvail[1] = false;
            }
        }
    }

    public String analysis() {
        String res;

        switch (path.length) {
            case 0:
                res = "Impossible - you have no storage";
                break;

            case 1:
                res = "You have Internal Storage only";
                break;

            case 2:
                res = "You have Internal and External storage";
                break;

            default:
                res = "You have Internal, External Primary and Secondary storage";

        }
        if (!isAvail[1]) {
            res += "\nYour External storage is emulated on the Internal storage card";
        }
        return res;
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

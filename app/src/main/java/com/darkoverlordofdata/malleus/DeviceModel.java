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

/**
 * Model the device storage
 *
 *
 */
public class DeviceModel implements Serializable {

    /**
     * Normalize the device storage locations
     * regardless of the SDK version:
     *
     *  [0]     = Internal Storage
     *  [1]     = Primary External Storage
     *  [2...]  = Secondary External Storage (starting with KitKat)
     */
    public long free[];            //  bytes free
    public long used[];            //  bytes used
    public long total[];           //  total bytes available
    public String path[];          //  absolute storage path
    public String store[];         //  absolute storage label
    public String freeKb[];        //  readable bytes free
    public String usedKb[];        //  readable bytes used
    public String totalKb[];       //  readable total bytes available
    public boolean isAvail[];      //  is it available to us?

    public long shredBytes;
    public int shredBlocks;

    /**
     * Model version
     */
    private static final long serialVersionUID = 1L;

    /**
     * Tail of the external path is /packagename/files
     */
    private static final String TAIL = "/com.darkoverlordofdata.malleus/files";

    /**
     * Starting with 3.0 Honeycomb we have emulated primary.
     * This means that we only need to write over the internal
     * storage because they both map to the same memory card.
     */
    private static final boolean isHoneyComb = (Integer.valueOf(android.os.Build.VERSION.SDK) >= 11);

    /**
     * Starting with 4.3 JellyBean there are changes to the StatFs api
     */
    private static final boolean isJellyBean = (Integer.valueOf(android.os.Build.VERSION.SDK) >= 18);

    /**
     * starting with 4.4 KitKat there is an array of External Storage
     * locations. The first corresponds to the Primary External.
     * The rest are Secondary External.
     */
    private static final boolean isKitKat = (Integer.valueOf(android.os.Build.VERSION.SDK) >= 19);


    /**
     * DeviceModel
     * wraps the phone file system
     * 
     */
    public DeviceModel(Context ctx) {

        if (HammerActivity.BETA)
            Log.i("DeviceModel", "SDK Version = "+android.os.Build.VERSION.SDK);

        boolean emulated = (isHoneyComb) && Environment.isExternalStorageEmulated();
        int count = (isKitKat) ? ctx.getExternalFilesDirs(null).length+1 : 2;

        free    = new long[count];
        used    = new long[count];
        total   = new long[count];
        path    = new String[count];
        store   = new String[count];
        freeKb  = new String[count];
        usedKb  = new String[count];
        totalKb = new String[count];
        isAvail = new boolean[count];

        /**
         * Internal Storage
         */
        path[0]     = Environment.getDataDirectory().getAbsolutePath();
        store[0]    = path[0];
        total[0]    = getTotal(path[0]);
        free[0]     = getFree(path[0]);
        used[0]     = total[0] - free[0];
        freeKb[0]   = humanize(used[0]);
        usedKb[0]   = humanize(free[0]);
        totalKb[0]  = humanize(total[0]);
        isAvail[0]  = true;

        /**
         * External Storage
         */
        if (isKitKat) {

            File ext[] = ctx.getExternalFilesDirs(null);
            for (int i=1; i<=ext.length; i++) {

                path[i]     = ext[i-1].getAbsolutePath();
                store[i]    = path[i].replace(TAIL, "");
                total[i]    = getTotal(path[i]);
                free[i]     = getFree(path[i]);
                used[i]     = total[i] - free[i];
                usedKb[i]   = humanize(free[i]);
                freeKb[i]   = humanize(used[i]);
                totalKb[i]  = humanize(total[i]);

                String state = Environment.getStorageState(ext[i-1]);
                isAvail[i]  = !emulated
                            && Environment.MEDIA_MOUNTED.equals(state)
                            && !(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
            }

        } else {

            path[1]     = Environment.getExternalStorageDirectory().getAbsolutePath();
            store[1]    = path[1].replace(TAIL, "");
            total[1]    = getTotal(path[1]);
            free[1]     = getFree(path[1]);
            used[1]     = total[1] - free[1];
            freeKb[1]   = humanize(used[1]);
            usedKb[1]   = humanize(free[1]);
            totalKb[1]  = humanize(total[1]);

            String state = Environment.getExternalStorageState();
            isAvail[1]  = !emulated
                        && Environment.MEDIA_MOUNTED.equals(state)
                        && !(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));

        }

        shredBytes = 0;
        for (int i=0; i < free.length; i++) {
            shredBytes += (isAvail[i]) ? free[i] : 0;
        }
        shredBlocks = (int) (shredBytes / HammerActivity.PAGE_SIZE);

    }

    /**
     * Get Free byte count
     *
     * @param path
     * @return
     */
    private long getFree(String path) {
        StatFs statFs = new StatFs(path);

        if (HammerActivity.BETA)
            Log.i("DeviceModel", "Free on "+path+"="+statFs.getBlockSize());
        return (isJellyBean)
                ? statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong()
                : (long)statFs.getAvailableBlocks() * (long)statFs.getBlockSize();
    }

    /**
     * Get Total byte count
     *
     * @param path
     * @return
     */
    private long getTotal(String path) {
        StatFs statFs = new StatFs(path);

        if (HammerActivity.BETA)
            Log.i("DeviceModel", "Free on "+path+"="+statFs.getBlockSize());
        return (isJellyBean)
                ? statFs.getBlockCountLong() * statFs.getBlockSizeLong()
                : (long)statFs.getBlockCount() * (long)statFs.getBlockSize();
    }


    /**
     * Estimated Time to run
     * best guess is about 2 min per gb.
     *
     * @return
     */
    public String estimatedTime() {
        int m = (int)(shredBytes/500000000);
        return (m <= 1) ? "a minute" : m+" minutes";
    }

    public String totalSpace() {
        return humanize(shredBytes);
    }

    /**
     * Analyze the volumetrics
     *
     * @return
     */
    public String analysis() {
        String res;

        switch (path.length) {
            case 0:
                res = "Impossible - you have no storage.";
                break;

            case 1:
                res = "You have Internal Storage only.";
                break;

            case 2:
                res = "You have Internal and External storage.";
                break;

            default:
                res = "You have Internal, External Primary and Secondary storage.";

        }
        if (!isAvail[1]) {
//            res += " Your External storage is emulated on the Internal storage card.";
            res += " Skipping emulated drives.";
        }
        res += "Ready to shred " + totalSpace() + " of storage.\n";
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
     * use KB, not KiB
     *
     * @param size
     * @return
     */
    private static String humanize (long size)
    {

        long Kb = 1  * 1000;
        long Mb = Kb * 1000;
        long Gb = Mb * 1000;
        long Tb = Gb * 1000;
        long Pb = Tb * 1000;
        long Eb = Pb * 1000;

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

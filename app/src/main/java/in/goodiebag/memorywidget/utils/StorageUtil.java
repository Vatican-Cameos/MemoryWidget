package in.goodiebag.memorywidget.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by pavan on 05/02/17.
 */

public class StorageUtil {


    public static String getOccupiedInternalMemorySize(File[] dirs) {
        StatFs stat = new StatFs(dirs[0].getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long totalBlocks = stat.getBlockCountLong();
        return readableFileSize((totalBlocks*blockSize) - (availableBlocks * blockSize));
    }

    public static String getOccupiedExternalMemorySize(File[] dirs){
        if (dirs.length > 1) {
            StatFs stat = new StatFs(dirs[1].getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            long totalBlocks = stat.getBlockCountLong();
            return readableFileSize((totalBlocks - availableBlocks) * blockSize);
        } else {
            return "NA";
        }

    }

    public static String getAvailableInternalMemorySize(File[] dirs) {
        StatFs stat = new StatFs(dirs[0].getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return readableFileSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize(File[] dirs) {
        StatFs stat = new StatFs(dirs[0].getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return readableFileSize(totalBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize(File[] dirs) {
        if (dirs.length > 1) {
            StatFs stat = new StatFs(dirs[1].getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return readableFileSize(availableBlocks * blockSize);
        } else {
            return "NA";
        }
    }

    public static String getTotalExternalMemorySize(File dirs[]) {
        if (dirs.length > 1) {
            StatFs stat = new StatFs(dirs[1].getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return readableFileSize(totalBlocks * blockSize);
        } else {
            return "NA";
        }
    }



    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }
}

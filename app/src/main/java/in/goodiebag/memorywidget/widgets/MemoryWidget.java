package in.goodiebag.memorywidget.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.File;
import java.text.DecimalFormat;

import in.goodiebag.memorywidget.R;
import in.goodiebag.memorywidget.services.UpdateWidgetsService;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class MemoryWidget extends AppWidgetProvider {
    static  File dirs[];


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        CharSequence widgetText = "Internal  " +getOccupiedInternalMemorySize() + "/" + getTotalInternalMemorySize() + " \n" + " External : " + getAvailableExternalMemorySize(context) + "/" + getTotalExternalMemorySize();
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.memory_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        dirs = ContextCompat.getExternalFilesDirs(context, null);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
        Log.d("MemoryWidget","onUpdate");
        Intent intentService = new Intent(context , UpdateWidgetsService.class);
        context.startService(intentService);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    public static String getOccupiedInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(dirs[0].getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long totalBlocks = stat.getBlockCountLong();
        return readableFileSize((totalBlocks*blockSize) - (availableBlocks * blockSize));
    }


    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(dirs[0].getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return readableFileSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(dirs[0].getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return readableFileSize(totalBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize(Context c) {
        if (dirs.length > 1) {
            StatFs stat = new StatFs(dirs[1].getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return readableFileSize(availableBlocks * blockSize);
        } else {
            return "NA";
        }
    }

    public static String getTotalExternalMemorySize() {
        if (dirs.length > 1) {
            //TODO :: Find a method to find external storage space
            StatFs stat = new StatFs(dirs[1].getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            return readableFileSize(totalBlocks * blockSize);
        } else {
            return "NA";
        }
    }

    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }



    public static String readableFileSize(long size) {
        if(size <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
        return new DecimalFormat("#,##0.##").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= (1024);
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

}


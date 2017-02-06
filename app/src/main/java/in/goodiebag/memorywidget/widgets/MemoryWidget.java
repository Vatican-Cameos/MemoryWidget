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
import in.goodiebag.memorywidget.utils.StorageUtil;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class MemoryWidget extends AppWidgetProvider {
    static  File dirs[];
    private static long internalMemoryOccupied;
    private static long externalMemoryOccupied;


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        externalMemoryOccupied  = StorageUtil.getOccupiedExternalMemorySizeAsLong(dirs);
        internalMemoryOccupied = StorageUtil.getOccupiedInternalMemorySizeAsLong(dirs);
        CharSequence widgetText = "Internal  " +StorageUtil.readableFileSize(internalMemoryOccupied) + "/" + StorageUtil.getTotalInternalMemorySize(dirs) + " \n" + " External : " + StorageUtil.readableFileSize(externalMemoryOccupied) + "/" + StorageUtil.getTotalExternalMemorySize(dirs);
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
        SharedPreferences.Editor editor = context.getSharedPreferences("memory", MODE_PRIVATE).edit();
        editor.putString("external", ""+Double.parseDouble(StorageUtil.readableFileSizeWithoutDigitGroup(externalMemoryOccupied)));
        editor.putString("internal", ""+Double.parseDouble(StorageUtil.readableFileSizeWithoutDigitGroup(internalMemoryOccupied)));
        editor.apply();
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

}


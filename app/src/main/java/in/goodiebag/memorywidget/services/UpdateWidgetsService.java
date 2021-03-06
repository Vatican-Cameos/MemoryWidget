package in.goodiebag.memorywidget.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;

import in.goodiebag.memorywidget.R;
import in.goodiebag.memorywidget.utils.StorageUtil;
import in.goodiebag.memorywidget.widgets.MemoryWidget;

public class UpdateWidgetsService extends Service {
    static File dirs[];
    boolean memoryChanged = false;

    public UpdateWidgetsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        dirs = ContextCompat.getExternalFilesDirs(this, null);
        Log.d("UpdateService", "IntentFired");
        memoryChanged = isMemoryChanged();
        if (memoryChanged) {
            //If there is a change fire the broadcast receiver to redraw the widget.
            Intent intentS = new Intent(this, MemoryWidget.class);
            intentS.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
            // since it seems the onUpdate() is only fired on that:
            int ids[] = AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(getApplication(), MemoryWidget.class));
            //int ids[] = {R.xml.memory_widget_info};
            intentS.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            sendBroadcast(intentS);
            Log.d("UpdateService", "IntentFired. Service should be restarted from the AppWidget.");
        } else {
            //IF there is no change detected then re schedule the service after 1 minutes
            Intent myIntent = new Intent(this, UpdateWidgetsService.class);
            PendingIntent pendingIntent = PendingIntent.getService(this, 0, myIntent, 0);
            AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.add(Calendar.SECOND, 60); // first time
            long frequency = 60 * 1000; // in ms
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), frequency, pendingIntent);
            Log.d("UpdateService", "PendingIntent scheduled. Service should be restarted after a minute.");
        }

        return START_STICKY;
    }

    private boolean isMemoryChanged() {
        //TODO : Implement this method which checks for memory change
        SharedPreferences prefs = getSharedPreferences("memory", MODE_PRIVATE);
        double previousExternalDouble = Double.valueOf(prefs.getString("external", ""));//"No name defined" is the default value.
        double previousInternalDouble = Double.valueOf(prefs.getString("internal", ""));

        if(previousExternalDouble != 0L){
            long currentExternalDouble = StorageUtil.getOccupiedExternalMemorySizeAsLong(dirs);
            double formattedCurrentExternalDouble = Double.valueOf(StorageUtil.readableFileSizeWithoutDigitGroup(currentExternalDouble));
            if(previousExternalDouble != formattedCurrentExternalDouble){
                return true;
            }
        }

        if(previousInternalDouble != 0L){
            long currentInternalDouble = StorageUtil.getOccupiedInternalMemorySizeAsLong(dirs);
            double formattedCurrentInternalDouble = Double.valueOf(StorageUtil.readableFileSizeWithoutDigitGroup(currentInternalDouble));
            Log.d("Internal","current : " + formattedCurrentInternalDouble + " external : " + previousInternalDouble);
            if(previousInternalDouble != formattedCurrentInternalDouble){
                return true;
            }
        }

        return false;

    }

}

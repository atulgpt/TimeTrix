package com.atulgpt.www.timetrix.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.PowerManager;
import android.os.Vibrator;

import com.atulgpt.www.timetrix.R;

/**
 * Created by atulgupta on 05-03-2016 at 02:19 AM.
 * To make pending intents
 */
public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        MediaPlayer mp = MediaPlayer.create(context, R.raw.ferry_sound);
        mp.start();
        PowerManager pm = (PowerManager)     context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My GlobalData");
        wl.acquire();
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] s = { 0, 100, 10, 500, 10, 100, 0, 500, 10, 100, 10, 500 };
        vibrator.vibrate(s, -1);
    }
}
//Calendar cal = Calendar.getInstance();
//Intent activate = new Intent(context, Alarm.class);
//AlarmManager alarms ;
//PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, activate, 0);
//alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        cal.set(Calendar.HOUR_OF_DAY, hour);
//        cal.set(Calendar.MINUTE, minute);
//        cal.set(Calendar.SECOND, 00);
//        alarms.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);
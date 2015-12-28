package com.anuj_garg.mancon;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;

/**
 * Created by Anuj Garg on 3/20/2015.
 */
public class DBprovider extends Service {

    private final int UPDATE_INTERVAL = 60 * 1000;
    private Timer timer = new Timer();
    private static final int NOTIFICATION_EX = 1;
    private NotificationManager notificationManager;
    public static Context context=null;

    public DBprovider() {}

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        context=getApplicationContext();
        // code to execute when the service is first created
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startid) {


/*        notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        int icon = android.R.drawable.stat_notify_sync;
        CharSequence tickerText = "Hello";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        Context context = getApplicationContext();
        CharSequence contentTitle = "My notification";
        CharSequence contentText = "Hello World!";
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);
        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // Check if there are updates here and notify if true
            }
        }, 0, UPDATE_INTERVAL);*/
        return START_STICKY;
    }

    private void stopService() {
        if (timer != null) timer.cancel();
    }
}

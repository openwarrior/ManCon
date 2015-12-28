package com.anuj_garg.mancon;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CallReceiver extends PhoneCallReceiver {

    @Override
    protected void onIncomingCallStarted(Context ctx, String number, Date start,int type) {
         {
             if(type==0)
             {
                 return;
             }
            Intent i = new Intent(ctx, floating.class);
            i.putExtra("number",number);
            i.putExtra("state","in");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
           ctx.startActivity(i);

        }
    }

    @Override
    protected void onOutgoingCallStarted(Context ctx, String number, Date start,int type) {
        if(type==0)
        {
            return;
        }
        Intent i = new Intent(ctx, floating.class);
        i.putExtra("number",number);
        i.putExtra("state","out");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);

    }


    @Override
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end,int type) {

        Intent i = new Intent(ctx, floating.class);
        i.putExtra("state","kill");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);

        (new Log(ctx)).start();
    }

    @Override
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end,int type) {
        SharedPreferences sharedPreferences= ctx.getSharedPreferences("general",Context.MODE_PRIVATE);
        if(number.equals(sharedPreferences.getString("hider","1234")))
        {
            Intent intent1=new Intent(ctx,MainActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(intent1);
        }
        Intent i = new Intent(ctx, floating.class);
        i.putExtra("state","kill");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);

        (new Log(ctx)).start();
    }

    @Override
    protected void onMissedCall(Context ctx, String number, Date start, int type) {

        Intent i = new Intent(ctx, floating.class);
        i.putExtra("state","kill");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(i);

        (new Log(ctx)).start();




    }

}

class Log extends Thread
{
    String phNum;
    String callType;
    String callDate;
    Date callDayTime;
    String callDuration;
    Activity activity;
    Context context;
    Log(Context context)
    {
        this.context=context;
    }
       public void run()
       {
           try {
               Thread.sleep(5000);
           } catch (InterruptedException e) {
               e.printStackTrace();
           }

           Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DEFAULT_SORT_ORDER);
          if(managedCursor.getCount()>0) {
               managedCursor.moveToFirst();
               int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
               int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
               int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
               int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
               int id = managedCursor.getColumnIndex(CallLog.Calls._ID);



               // while (managedCursor.moveToNext())

                   String phNum = managedCursor.getString(number);
                   String callType = managedCursor.getString(type);
                   String callDate = managedCursor.getString(date);
                   Date callDayTime = new Date(Long.valueOf(callDate));
                   String callDuration = managedCursor.getString(duration);
                    String idstr=managedCursor.getString(id);




               SimpleDateFormat dateTime = new SimpleDateFormat("MMM-dd   hh:mm aa");
               phNum=ContactItem.hygenicNumber(phNum);
               DatabaseHandler db=new DatabaseHandler(context);
               Contact contact=db.getContactByPhone(phNum);
                db.close();
               int hr = Integer.parseInt(callDuration)/3600;
               int rem = Integer.parseInt(callDuration)%3600;
               int mn = rem/60;
               int sec = rem%60;

               String durationStr="";
               if(hr>0)
               {
                   durationStr+=hr+" hour ";
               }
               if(mn>0)
               {
                   durationStr+=mn+" minutes ";
               }

                   durationStr+=sec+" seconds ";


               if(contact!=null)
               {

                   db=new DatabaseHandler(context);
                   db.createLog(new Call(0, contact.contact_id, callType, dateTime.format(callDayTime), durationStr, phNum));
                   db.close();
                   String strUriCalls="content://call_log/calls";

                   Uri UriCalls = Uri.parse(strUriCalls);
                   String queryString= "_ID=" + idstr ;


                   int i=context.getContentResolver().delete(UriCalls, queryString, null);

                   SharedPreferences sharedPreferences=context.getSharedPreferences("general", Context.MODE_PRIVATE);

                   boolean value=sharedPreferences.getBoolean("notificationMissedCall",false);

                   if(value==true && callType.equals(""+CallLog.Calls.MISSED_TYPE))

                   {
                       NotificationManager notificationManager = (NotificationManager)
                       context.getSystemService(Context.NOTIFICATION_SERVICE);
                       int icon = android.R.drawable.stat_notify_sync;
                       CharSequence tickerText = "missed call";
                       long when = System.currentTimeMillis();
                       Notification notification = new Notification(icon, tickerText, when);
                       notification.flags |= Notification.FLAG_AUTO_CANCEL;
                       CharSequence contentTitle = "Missed Call";
                       CharSequence contentText;

                       db=new DatabaseHandler(context);
                       contentText = db.getContactByPhone(phNum).name;
                       db.close();
                       Intent notificationIntent = new Intent(context, NotificationPassword.class);
                       notificationIntent.putExtra("type", "CallLog");
                       PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
                       notification.setLatestEventInfo(context, contentTitle, contentText,contentIntent);
                       notificationManager.notify(1, notification);
                   }

               }
               else
               {
                   db.createLog(new Call(0, -1, callType, dateTime.format(callDayTime), durationStr, phNum));
               }
               db.close();

           }


       }
}

package com.anuj_garg.mancon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

import java.util.Date;

public class PhoneCallReceiver extends BroadcastReceiver {
    private static  int lastState= TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;
    private static String testing;



    @Override
    public void onReceive(Context context, Intent intent) {
    if(intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")){
        savedNumber=intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        testing=intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");


    }
        else {
        String stateStr=intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        String number=intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
        testing=intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

        int state = 0;
        if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
            state = TelephonyManager.CALL_STATE_IDLE;
        }
        else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            state = TelephonyManager.CALL_STATE_OFFHOOK;
        }
        else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            state = TelephonyManager.CALL_STATE_RINGING;
        }
       if(lastState==TelephonyManager.CALL_STATE_OFFHOOK && state==TelephonyManager.CALL_STATE_IDLE)
       {
          /* */
       }


        onCallStateChanged(context, state, number);
    }

    }
    //Derived classes should override these to respond to specific events of interest
    protected void onIncomingCallStarted(Context ctx, String number, Date start, int type){}
    protected void onIncomingCallPicked(Context ctx, String number, Date start, int type){}
    protected void onOutgoingCallStarted(Context ctx, String number, Date start, int type){}
    protected void onIncomingCallEnded(Context ctx, String number, Date start, Date end, int type){}
    protected void onOutgoingCallEnded(Context ctx, String number, Date start, Date end, int type){}
    protected void onMissedCall(Context ctx, String number, Date start, int type){}


    //Deals with actual events

    //Incoming call-  goes from IDLE to RINGING when it rings, to OFFHOOK when it's answered, to IDLE when its hung up
    //Outgoing call-  goes from IDLE to OFFHOOK when it dials out, to IDLE when hung up
    public void onCallStateChanged(Context context, int state, String number) {


        SharedPreferences sharedPreferences= context.getSharedPreferences("general",Context.MODE_PRIVATE);

        boolean value=sharedPreferences.getBoolean("service",true);
        if(value==false)
        {
            return;
        }

        if(lastState == state){
            return;
        }

        DatabaseHandler db=new DatabaseHandler(context);
        if(savedNumber!=null) {
            savedNumber = ContactItem.hygenicNumber(savedNumber);
        }
        else
        {
            savedNumber = ContactItem.hygenicNumber(number);
        }
        Contact c=db.getContactByPhone(savedNumber);
        db.close();
        int type=1;
        if(c==null)
        {
                type=0;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                lastState=TelephonyManager.CALL_STATE_RINGING;
                onIncomingCallStarted(context, number, callStartTime,type);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                //Transition of ringing->offhook are pickups of incoming calls.  Nothing done on them
                if(lastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, savedNumber, callStartTime,type);
                }
                else
                {
                    onIncomingCallPicked(context, savedNumber, callStartTime,type);

                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                //Went to idle-  this is the end of a call.  What type depends on previous state(s)
                if(lastState == TelephonyManager.CALL_STATE_RINGING){
                    //Ring but no pickup-  a miss
                    onMissedCall(context, savedNumber, callStartTime,type);
                }
                else if(isIncoming){
                    onIncomingCallEnded(context, savedNumber, callStartTime, new Date(),type);
                }
                else{

                    onOutgoingCallEnded(context, savedNumber, callStartTime, new Date(),type);
                }
                break;
        }
        lastState = state;
        db.close();
    }
}
package com.anuj_garg.mancon;

/**
 * Created by Anuj Garg on 3/26/2015.
 */
public class Call {

    int id;
    int callerID;
    String callType;
    String time;
    String duration;
    String number;

    public Call(int id,int callerID,String callType,String time,String duration,String number)
    {
        this.id=id;
        this.callerID=callerID;
        this.duration=duration;
        this.time=time;
        this.callType=callType;
        this.number=number;
    }
}

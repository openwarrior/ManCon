package com.anuj_garg.mancon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Anuj Garg on 3/20/2015.
 */
public class RawContactsRefresh extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1=new Intent(context, DBprovider.class);
        //context.startService(intent);

    }
}

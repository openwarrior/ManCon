package com.anuj_garg.mancon;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Anuj Garg on 4/1/2015.
 */
public class AppControler extends Application {

    public long mLastPause;
    static boolean locked;
    static Context context;

    public static boolean bLauncher=false;
    public static boolean  bMainActivity=false;
    public static boolean bCallLog=false;
    public static boolean bContacts=false;
    public static boolean bAllContacts=false;
    public static boolean bSettings=false;
    public static boolean bCreate=false;
    public static boolean bAbout=false;
    public static boolean bHelp=false;
    public static boolean bEdit=false;
    public static boolean minimised=false;


    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();

    }

    public static void unlock(Context context) {


        if(locked&&((minimised)) ){
    Intent intent1 = new Intent(context, Lock.class);
    intent1.putExtra("text", "Enter password");
    intent1.putExtra("type", "enter");
    intent1.putExtra("typeInEnter", "unlock");
    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent1);
}
    }

    public static void setUnsetMinimised()
    {

        minimised= !(bLauncher&&bMainActivity&&bCallLog&&bContacts&&bAllContacts&&bSettings&&bCreate&&bAbout&&bHelp&&bEdit);
    }


}

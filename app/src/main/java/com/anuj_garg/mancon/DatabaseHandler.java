package com.anuj_garg.mancon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.LinkedList;

/**
 * Created by Anuj Garg on 2/21/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION=1;
    private static final String  DATABASE_NAME="Mancon",
    ////////// Table details to Contacts Table
    TABLE_CONTACTS="Contacts",
            KEY_CONTACT_ID="id",
            KEY_CONTACT_NAME="name",
            KEY_CONTACT_PHONE="phone",
            TABLE_ALL_CONTACTS="ALLContacts",
            KEY_ALL_CONTACT_ID="ALLid",
            KEY_ALL_CONTACT_NAME="ALLname",
            KEY_ALL_CONTACT_PHONE="ALLphone",
            KEY_ALL_CONTACT_ORIG="ALLORIG",

    TABLE_USER="User",
            KEY_USER_PASSWORD="Password",
            KEY_USER_ATTEMPT1="attempt1",
            KEY_USER_ATTEMPT2="attempt2",


    TABLE_CALL_LOG="callLog",
            KEY_LOG_CALL_ID="id",
            KEY_LOG_CALLER_ID="callerid",
            KEY_LOG_CALL_TYPE="type",
            KEY_LOG_TIME="time",
            KEY_LOG_DURATION="duration",
            KEY_LOG_NUMBER="number";


    public DatabaseHandler(Context context)

    {

        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE  TABLE IF NOT EXISTS "+TABLE_CONTACTS+" ("+KEY_CONTACT_ID+" INTEGER  PRIMARY KEY  AUTOINCREMENT  NOT NULL, "+KEY_CONTACT_NAME+" VARCHAR, "+KEY_CONTACT_PHONE+" VARCHAR)");
        db.execSQL("CREATE  TABLE IF NOT EXISTS "+TABLE_ALL_CONTACTS+" ("+KEY_ALL_CONTACT_ID+" INTEGER  PRIMARY KEY  AUTOINCREMENT  NOT NULL, "+KEY_ALL_CONTACT_NAME+" VARCHAR, "+KEY_ALL_CONTACT_PHONE+" VARCHAR, "+KEY_ALL_CONTACT_ORIG+" VARCHAR)");
        db.execSQL("CREATE  TABLE IF NOT EXISTS "+TABLE_CALL_LOG+" ("+KEY_LOG_CALL_ID+" INTEGER  PRIMARY KEY  AUTOINCREMENT  NOT NULL, "+KEY_LOG_CALLER_ID+" INTEGER, "+KEY_LOG_CALL_TYPE+" VARCHAR, "+KEY_LOG_TIME+" VARCHAR, "+KEY_LOG_DURATION+" VARCHAR, "+KEY_LOG_NUMBER+" VARCHAR)");
        db.execSQL("CREATE  TABLE IF NOT EXISTS "+TABLE_USER+" ("+KEY_USER_PASSWORD+" VARCHAR, "+KEY_USER_ATTEMPT1+" VARCHAR, "+KEY_USER_ATTEMPT2+" VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean isthereanyold()
    {
        boolean b=false;

        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM "+TABLE_USER,new String[] {});
        if(cursor!=null)
        {
            if(cursor.getCount()==0)
            {
            b=false;
                return b;
            }
            else
            {
                cursor.moveToFirst();
                if(cursor.getString(0).equals("null"))
                {
                    return false;
                }
            }
        }
        return true;

    }

    public boolean compare(String password)
    {
        boolean equal=false;
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM "+TABLE_USER,new String[] {});

        if(cursor!=null)
        {
            cursor.moveToFirst();
            equal=password.equals(cursor.getString(0));
        }
        cursor.close();

        return equal;
    }


    public void passwordsetattempt1(String attempt)
    {
        SQLiteDatabase db=getWritableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM "+TABLE_USER,new String[] {});
        if(cursor!=null)
        {
            if(cursor.getCount()==0)
            {
                ContentValues values=new ContentValues();
                values.put(KEY_USER_ATTEMPT1,attempt);
                values.put(KEY_USER_PASSWORD, "null");
                db.insert(TABLE_USER,null,values);
                db.close();
                return;
            }
        }
        db.execSQL("UPDATE "+TABLE_USER+ " SET "+KEY_USER_ATTEMPT1+" = '"+attempt+"'");
        db.close();
    }

    public boolean passwordsetattempt2(String attempt)
    {
        boolean equal=false;
        SQLiteDatabase db=getWritableDatabase();

        Cursor cursor =db.rawQuery("SELECT * FROM "+TABLE_USER,new String[] {});

        if(cursor!=null)
        {
            cursor.moveToFirst();
            equal=attempt.equals(cursor.getString(1));
        }
        cursor.close();

        if(equal)
        {
            db.execSQL("UPDATE "+TABLE_USER+ " SET "+KEY_USER_PASSWORD+" = '"+attempt+"'");
            db.close();
        }
        db.close();
        return equal;
    }



////////////////
    //////////////
    ////////////
    //////////////
    //////////////


    public void createContact(Contact contect)
    {
        SQLiteDatabase db=getWritableDatabase();


        Cursor cursor =db.rawQuery("SELECT "+KEY_CONTACT_ID+","+KEY_CONTACT_NAME+","+KEY_CONTACT_PHONE+ " FROM "+TABLE_CONTACTS+" WHERE "+KEY_CONTACT_PHONE+" = '"+contect.number+"'",new String[] {});

        if(cursor.getCount()>0)
        {
            cursor.close();
            return;
        }
        cursor.close();
        if(contect.name.length()==1)
        {
            contect.name=(contect.name.substring(0,1).toUpperCase());
        }
        else {
            contect.name = (contect.name.substring(0, 1).toUpperCase() + contect.name.substring(1));
        }
        ContentValues values=new ContentValues();
        values.put(KEY_CONTACT_NAME,contect.getName());
        values.put(KEY_CONTACT_PHONE, contect.getNumber());
        db.insert(TABLE_CONTACTS,null,values);
        db.close();

    }

    public Contact getContact(int id)
    {
        Contact contact=null;
        SQLiteDatabase db=getWritableDatabase();

        Cursor cursor =db.rawQuery("SELECT "+KEY_CONTACT_ID+","+KEY_CONTACT_NAME+","+KEY_CONTACT_PHONE+ " FROM "+TABLE_CONTACTS+" WHERE "+KEY_CONTACT_ID+" = "+id,new String[] {});


        if(cursor!=null)
        {

            cursor.moveToFirst();
            contact =new Contact(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2));
        }
        cursor.close();
        db.close();
        return contact;
    }

    public Contact getContactByPhone(String number)
    {
        Contact contact=null;
        SQLiteDatabase db=getWritableDatabase();

        Cursor cursor =db.rawQuery("SELECT "+KEY_CONTACT_ID+","+KEY_CONTACT_NAME+","+KEY_CONTACT_PHONE+ " FROM "+TABLE_CONTACTS+" WHERE "+KEY_CONTACT_PHONE+" = '"+number+"'",new String[] {});
        if(cursor.getCount()==0)
        {
            return null;
        }

        if(cursor!=null)
        {

            cursor.moveToFirst();
            contact =new Contact(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2));
        }
        cursor.close();
        db.close();
        return contact;
    }

    public LinkedList<Contact> getSpecificContacts(String s)
    {
        SQLiteDatabase db=getWritableDatabase();
        Contact contact;
        LinkedList<Contact> list =new LinkedList<>();

        Cursor cursor =db.rawQuery("SELECT * FROM "+TABLE_CONTACTS+" WHERE "+KEY_CONTACT_NAME+" LIKE '%"+s+"%' ORDER BY "+KEY_CONTACT_NAME,new String[] {});
        if(cursor!=null)
        {

            cursor.moveToFirst();
            for(int i=0;i<cursor.getCount();i++)
            {
                contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));


                list.add(contact);
                if(i==cursor.getCount())
                {
                    break;
                }
                cursor.moveToNext();
            }
        }


        cursor.close();
        db.close();
        return list;
    }


    public LinkedList<Contact> getAllContacts()
    {
        SQLiteDatabase db=getWritableDatabase();
        Contact contact;
        LinkedList<Contact> list =new LinkedList<>();


        Cursor cursor =db.rawQuery("SELECT "+KEY_CONTACT_ID+","+KEY_CONTACT_NAME+","+KEY_CONTACT_PHONE+ " FROM "+TABLE_CONTACTS,new String[] {});

        if(cursor!=null)
        {
            cursor.moveToFirst();
            for(int i=0;i<cursor.getCount();i++)
            {
                contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
                list.add(contact);
                if(i==cursor.getCount())
                {
                    break;
                }
                cursor.moveToNext();
            }
        }
        cursor.close();


        db.close();
        return list;
    }



    public boolean deleteContact(int id)
    {
        SQLiteDatabase db=getWritableDatabase();
        Contact contact;
        Cursor cursor=null;
        try {
            db.delete(TABLE_CONTACTS,KEY_CONTACT_ID+" = "+id,new String[]{});
        }
        catch (Exception e)
        {

            Toast.makeText(MainActivity.context, e.toString(), Toast.LENGTH_LONG).show();


        }
        db.close();
        return true;
    }

    public void updateContact(String name,String number,int id)
    {
        SQLiteDatabase db=getWritableDatabase();
        db.execSQL("UPDATE "+TABLE_CONTACTS+ " SET "+KEY_CONTACT_NAME+" = '"+name+"', "+KEY_CONTACT_PHONE+" = '"+number+"' WHERE "+KEY_CONTACT_ID+" = "+id);
        db.close();
    }


    ////////////////////
    ////////////////////////
    /////////////////////



    public void createALLContact(Contact contect,String orig)
    {
        SQLiteDatabase db=getWritableDatabase();


        Cursor cursor =db.rawQuery("SELECT "+KEY_CONTACT_ID+","+KEY_CONTACT_NAME+","+KEY_CONTACT_PHONE+ " FROM "+TABLE_CONTACTS+" WHERE "+KEY_CONTACT_PHONE+" = '"+contect.number+"'",new String[] {});

        if(cursor.getCount()>0)
        {
            cursor.close();
            return;
        }

       cursor =db.rawQuery("SELECT "+KEY_ALL_CONTACT_ID+","+KEY_ALL_CONTACT_NAME+","+KEY_ALL_CONTACT_PHONE+ " FROM "+TABLE_ALL_CONTACTS+" WHERE "+KEY_ALL_CONTACT_PHONE+" = '"+contect.number+"'",new String[] {});

        if(cursor.getCount()>0)
        {
            cursor.close();
            return;
        }

        cursor.close();

        if(contect.name.length()==1)
        {
            contect.name=(contect.name.substring(0,1).toUpperCase());
        }
        else {
            contect.name = (contect.name.substring(0, 1).toUpperCase() + contect.name.substring(1));
        }


        ContentValues values=new ContentValues();
        values.put(KEY_ALL_CONTACT_NAME,contect.getName());
        values.put(KEY_ALL_CONTACT_PHONE, contect.getNumber());
        values.put(KEY_ALL_CONTACT_ORIG,orig);
        db.insert(TABLE_ALL_CONTACTS,null,values);
        db.close();
    }

    public String getALLContactToOrign(int id)
    {
        String orig=null;
        SQLiteDatabase db=getWritableDatabase();

        Cursor cursor =db.rawQuery("SELECT "+KEY_ALL_CONTACT_ORIG+ " FROM "+TABLE_ALL_CONTACTS+" WHERE "+KEY_ALL_CONTACT_ID+" = "+id,new String[] {});

        if(cursor!=null)
        {

            if(cursor.getCount()==0)
            {
                cursor.close();
                db.close();
                return null;
            }

            cursor.moveToFirst();
            orig=cursor.getString(0);


        }
        cursor.close();
        db.close();
        return orig;
    }

    public Contact getALLContact(int id)
    {
        Contact contact=null;
        SQLiteDatabase db=getWritableDatabase();

        Cursor cursor =db.rawQuery("SELECT "+KEY_ALL_CONTACT_ID+","+KEY_ALL_CONTACT_NAME+","+KEY_ALL_CONTACT_PHONE+ " FROM "+TABLE_ALL_CONTACTS+" WHERE "+KEY_ALL_CONTACT_ID+" = "+id,new String[] {});

        if(cursor!=null)
        {

            if(cursor.getCount()==0)
            {
                cursor.close();
                db.close();
                return null;
            }

            cursor.moveToFirst();
            contact =new Contact(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2));
        }
        cursor.close();
        db.close();
        return contact;
    }

    public Contact getALLContactByPhone(String number)
    {
        Contact contact=null;
        SQLiteDatabase db=getWritableDatabase();

        Cursor cursor =db.rawQuery("SELECT "+KEY_ALL_CONTACT_ID+","+KEY_ALL_CONTACT_NAME+","+KEY_ALL_CONTACT_PHONE+ " FROM "+TABLE_ALL_CONTACTS+" WHERE "+KEY_ALL_CONTACT_PHONE+" = '"+number+"'",new String[] {});
        if(cursor.getCount()==0)
        {
            return null;
        }

        if(cursor!=null)
        {

            cursor.moveToFirst();
            contact =new Contact(Integer.parseInt(cursor.getString(0)),cursor.getString(1),cursor.getString(2));
        }
        cursor.close();
        db.close();
        return contact;
    }

    public LinkedList<Contact> getSpecificALLContacts(String s)
    {
        SQLiteDatabase db=getWritableDatabase();
        Contact contact;
        LinkedList<Contact> list =new LinkedList<>();
        Cursor cursor =db.rawQuery("SELECT * FROM "+TABLE_ALL_CONTACTS+" WHERE "+KEY_ALL_CONTACT_NAME+" LIKE '%"+s+"%' ORDER BY "+KEY_ALL_CONTACT_NAME,new String[] {});

        if(cursor!=null)
        {
            cursor.moveToFirst();
            for(int i=0;i<cursor.getCount();i++)
            {
                contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));


                list.add(contact);
                if(i==cursor.getCount())
                {
                    break;
                }
                cursor.moveToNext();
            }
        }


        cursor.close();
        db.close();
        return list;
    }


    public LinkedList<Contact> getAllALLContacts()
    {
        SQLiteDatabase db=getWritableDatabase();
        Contact contact;
        LinkedList<Contact> list =new LinkedList<>();


        Cursor cursor =db.rawQuery("SELECT "+KEY_ALL_CONTACT_ID+","+KEY_ALL_CONTACT_NAME+","+KEY_ALL_CONTACT_PHONE+ " FROM "+TABLE_ALL_CONTACTS,new String[] {});

        if(cursor!=null)
        {
            cursor.moveToFirst();
            for(int i=0;i<cursor.getCount();i++)
            {
                contact = new Contact(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
                list.add(contact);
                if(i==cursor.getCount())
                {
                    break;
                }
                cursor.moveToNext();
            }
        }
        cursor.close();


        db.close();
        return list;
    }



    public boolean deleteALLContact(int id)
    {
        SQLiteDatabase db=getWritableDatabase();
        Contact contact;
        Cursor cursor=null;
        try {
            db.delete(TABLE_ALL_CONTACTS,KEY_ALL_CONTACT_ID+" = "+id,new String[]{});
        }
        catch (Exception e)
        {

            Toast.makeText(MainActivity.context, e.toString(), Toast.LENGTH_LONG).show();


        }
        db.close();
        return true;
    }
    public boolean deleteALLContactComplete()
    {
        SQLiteDatabase db=getWritableDatabase();
        Call call;
        Cursor cursor=null;
        try {
            db.delete(TABLE_ALL_CONTACTS,null,new String[]{});
        }
        catch (Exception e)
        {

            Toast.makeText(MainActivity.context, e.toString(), Toast.LENGTH_LONG).show();


        }
        db.close();
        return true;
    }


///////////////////
    ////////////////
    //////////////
    /////////////////
    /////////////////



    public void createLog(Call call)
    {
        SQLiteDatabase db=getWritableDatabase();




        ContentValues values=new ContentValues();
       // values.put(KEY_LOG_CALL_ID,call.id);
        values.put(KEY_LOG_CALLER_ID,call.callerID);
        values.put(KEY_LOG_TIME,call.time);
        values.put(KEY_LOG_DURATION,call.duration);
        values.put(KEY_LOG_CALL_TYPE,call.callType);
        values.put(KEY_LOG_NUMBER,call.number);
        db.insert(TABLE_CALL_LOG,null,values);
        db.close();
    }

    public Call getLog(int id)
    {
        Call call=null;
        SQLiteDatabase db=getWritableDatabase();

        Cursor cursor =db.rawQuery("SELECT * FROM "+TABLE_CALL_LOG+" WHERE "+KEY_LOG_CALL_ID+" = "+id,new String[] {});


        if(cursor!=null)
        {

            cursor.moveToFirst();
            call =new Call(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
        }
        cursor.close();
        db.close();
        return call;
    }

    public LinkedList<Call> getAllLog()
    {
        SQLiteDatabase db=getWritableDatabase();
        Call call;
        LinkedList<Call> list =new LinkedList<>();


       // Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_CALL_LOG+" GROUP BY "+KEY_LOG_NUMBER+" ORDER BY "+KEY_LOG_CALL_ID+" DESC", new String[]{});
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_CALL_LOG+" ORDER BY "+KEY_LOG_CALL_ID+" DESC", new String[]{});

        if(cursor!=null)
        {
            cursor.moveToFirst();
            for(int i=0;i<cursor.getCount();i++)
            {
                call =new Call(Integer.parseInt(cursor.getString(0)),Integer.parseInt(cursor.getString(1)),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5));
                list.add(call);
                if(i==cursor.getCount())
                {
                    break;
                }
                cursor.moveToNext();
            }
        }
        cursor.close();


        db.close();
        return list;
    }



    public boolean deleteLog(int id)
    {
        SQLiteDatabase db=getWritableDatabase();
        Call call;
        Cursor cursor=null;
        try {
            db.delete(TABLE_CALL_LOG,KEY_LOG_CALL_ID+" = "+id,new String[]{});
        }
        catch (Exception e)
        {

            Toast.makeText(MainActivity.context, e.toString(), Toast.LENGTH_LONG).show();


        }
        db.close();
        return true;
    }

    public boolean deleteLogByCallerid(int id)
    {
        SQLiteDatabase db=getWritableDatabase();
        Call call;
        Cursor cursor=null;
        try {
            db.delete(TABLE_CALL_LOG,KEY_LOG_CALLER_ID+" = "+id,new String[]{});
        }
        catch (Exception e)
        {

            Toast.makeText(MainActivity.context, e.toString(), Toast.LENGTH_LONG).show();


        }
        db.close();
        return true;
    }

    public boolean deleteLogByNumber(String number)
    {
        SQLiteDatabase db=getWritableDatabase();
        Call call;
        Cursor cursor=null;
        try {
            db.delete(TABLE_CALL_LOG,KEY_LOG_NUMBER+" = '"+number+"'",new String[]{});
        }
        catch (Exception e)
        {

            Toast.makeText(MainActivity.context, e.toString(), Toast.LENGTH_LONG).show();


        }
        db.close();
        return true;
    }

    public boolean deleteLogComplete()
    {
        SQLiteDatabase db=getWritableDatabase();
        Call call;
        Cursor cursor=null;
        try {
            db.delete(TABLE_CALL_LOG,null,new String[]{});
        }
        catch (Exception e)
        {

            Toast.makeText(MainActivity.context, e.toString(), Toast.LENGTH_LONG).show();


        }
        db.close();
        return true;
    }



    ////////////////////
    ////////////////////////
    /////////////////////



}

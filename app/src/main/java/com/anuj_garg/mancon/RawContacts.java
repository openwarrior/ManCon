package com.anuj_garg.mancon;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.LinkedList;

/**
 * Created by Anuj Garg on 2/28/2015.
 */

class ContactItem
{
    public String name;
    public String orig_number;
    public String number;
    public ContactItem(String name, String number)
    {
        this.name=name;
        this.orig_number=number;
        this.number=hygenicNumber(number);


    }

    public static String hygenicNumber(String number)
    {
        if(number==null)
        {
            return ("");
        }
        String code= null;
        if(number.length()<8)
        {
            return number;
        }


        try {
            SharedPreferences sharedPreferences=AppControler.context.getSharedPreferences("general", Context.MODE_PRIVATE);
            code = sharedPreferences.getString("country","");
        } catch (Exception e) {
            e.printStackTrace();
        }

        char ch[]=number.toCharArray();
        StringBuilder s=new StringBuilder();
        for(int i=0;i< ch.length;i++)
        {
            if(i==0)
            {
                if(ch[i]=='0') {
                    s.append(code);
                    continue;
                }
                if(ch[i]!='+')
                {
                    s.append(code);

                }
            }
            if((ch[i]>='0'&&(ch[i]<='9'))||(ch[i]=='+'))
            {
                s.append(ch[i]);
            }

        }
        return s.toString();
    }
}

public class RawContacts extends Thread{

    public Context context;
    LinkedList<ContactItem> list=new LinkedList<>();

    RawContacts(Context context)
    {
        this.context=context;
    }

    public void run()
    {
        String phoneNumber = null;
        String email = null;
        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;
        Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
        StringBuffer output = new StringBuffer();
        ContentResolver contentResolver = MainActivity.contentResolver;
        Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
    DatabaseHandler db=new DatabaseHandler(context);
        // Loop for every contact in the phone
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);
                    while (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        if(phoneNumber.length()>=10) {

                            list.add(new ContactItem(name, phoneNumber));

                        }
                    }
                    phoneCursor.close();

                }
            }
        }

        for(int i=0;i<list.size();i++)
        {
            db.createALLContact(new Contact(i,list.get(i).name,list.get(i).number),list.get(i).orig_number);

        }
        db.close();
    }


}

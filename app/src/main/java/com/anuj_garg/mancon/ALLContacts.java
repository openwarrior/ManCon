package com.anuj_garg.mancon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;


class CustomListALLContacts extends ArrayAdapter<String> {
    private final Activity context;
    private  final LinkedList<Contact> list;
    private  final ArrayList<String> contactIDlist;
    int counter=0;
    public CustomListALLContacts(Activity context,
                              LinkedList<Contact> list, ArrayList<String> contactIDlist) {
        super(context, R.layout.contact_list_item, contactIDlist);
        this.context = context;
        this.contactIDlist = contactIDlist;
        this.list=list;

    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        View rowView= null;
        try {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.contact_list_item, null, true);
            TextView txtName = (TextView) rowView.findViewById(R.id.nameContact);
            TextView txtStatus = (TextView) rowView.findViewById(R.id.statusContact);
            TextView txtView = (TextView) rowView.findViewById(R.id.txtContact);

            char ch=list.get(position).name.charAt(0);
            txtName.setText(list.get(position).name);
            txtView.setText(""+ch);
            //txtView.setBackgroundColor(ch%4);
            //imageView.setBackgroundColor((int)ch%5);


            txtStatus.setText(list.get(position).number);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowView;
    }
}

public class ALLContacts extends Activity{

    public ScrollView s;
    ListView listView;
    int x;
    LinkedList<Contact> list=new LinkedList<>();
    LinearLayout l;
    public static String number;
    CustomListALLContacts anotherAdapter;
    EditText search;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppControler.unlock(getApplicationContext());
        AppControler.bAllContacts=true;
        setContentView(R.layout.activity_allcontacts);
        ContentResolver cr = getContentResolver();
        activity=this;
        final DatabaseHandler db=new DatabaseHandler(getApplicationContext());
        search=(EditText)findViewById(R.id.ALLsearcher);

        list=db.getSpecificALLContacts("");

        db.close();
        showContacts(list);

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                list=db.getSpecificALLContacts(search.getText().toString());
                db.close();
                // listView.removeAllViews();
                showContacts(list);



            }

        });




    }

    public void showContacts(final LinkedList<Contact> list)
    {
        try {


            final ArrayList<String> contactIDList=new ArrayList<>();
            for(int i=0;i<list.size();i++)
            {
                contactIDList.add(""+list.get(i).contact_id);
            }
            final CustomListALLContacts adapter = new
                    CustomListALLContacts(this, list,contactIDList);
            anotherAdapter=adapter;
            listView=(ListView)findViewById(R.id.ALLContactList);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        final int position, long id) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);

                    alert.setTitle("Are you sure ");
                    alert.setMessage("This will hide contact from main contacts on your android");



                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            // Canceled.
                        }
                    });

                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DatabaseHandler db = new DatabaseHandler(MainActivity.context);
                            db.createContact(list.get(position));

                            String orig=db.getALLContactToOrign(Integer.parseInt(contactIDList.get(position)));
                            db.deleteALLContact(Integer.parseInt(contactIDList.get(position)));
                            db.close();
                            contactIDList.remove(position);
                            list.remove(position);
                            //view.setClickable(false);
                            adapter.notifyDataSetChanged();
                            deleteContact(getApplicationContext(),orig,list.get(position).getName());

                        }
                    });

                    alert.show();


                }
            });



        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),ex.toString()+"it is this one",Toast.LENGTH_LONG).show();
        }

        search.clearFocus();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

    }

    public static boolean deleteContact(Context ctx, String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));

        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);

        try {


            if (cur.moveToFirst()) {
                do {
                    {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);

                        int i=ctx.getContentResolver().delete(uri, null, null);

                        return true;
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppControler.bAllContacts=false;
        AppControler.setUnsetMinimised();
    }

    @Override public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
        {

            startActivity(new Intent(getApplicationContext(),Contacts.class));
            finish();
        }

        else if(keyCode== KeyEvent.KEYCODE_MENU)
        {
            openOptionsMenu();
        }

        //onBackPressed();
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_allcontacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId())
        {
            case R.id.create:
                intent = new Intent(MainActivity.context, CreateContact.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.selected:
                intent = new Intent(MainActivity.context, Contacts.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.refresh:
                DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                db.deleteALLContactComplete();
                LinkedList<Contact> list=db.getAllALLContacts();
                db.close();
                showContacts(list);
                Toast.makeText(getApplicationContext(),"Come back few seconds later",Toast.LENGTH_SHORT).show();
                try {

                    Thread t=new RawContacts(getApplicationContext());
                    t.start();
                } catch (Exception e) {

                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                }

                return true;
            case R.id.log:
                intent = new Intent(MainActivity.context, CallLog.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.setting:
                intent = new Intent(MainActivity.context, Setting.class);
                Setting.intent=new Intent(MainActivity.context, ALLContacts.class);
                startActivity(intent);
                return true;
            default:
                return true;

    }


    }


}

class Live extends Thread
{
    CustomListALLContacts adapter;
    Context context;
    int counter=0;
    Live(CustomListALLContacts adapter,Context context)
    {
        this.adapter=adapter;
        this.context=context;
    }

    @Override
    public void run()
    {
        while (true)
        {
            if(counter==0)
            {
                continue;
            }
            if (context != null) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}

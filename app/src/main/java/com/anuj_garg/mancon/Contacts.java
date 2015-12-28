package com.anuj_garg.mancon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;


class CustomListContacts extends ArrayAdapter<String> {
    private final Activity context;
    private  final LinkedList<Contact> list;
    private  final ArrayList<String> contactIDlist;


    public CustomListContacts(Activity context,
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
            rowView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {


                    return false;
                }
            });

            TextView txtName = (TextView) rowView.findViewById(R.id.nameContact);
            TextView txtStatus = (TextView) rowView.findViewById(R.id.statusContact);
            TextView txtView = (TextView) rowView.findViewById(R.id.txtContact);

            char ch=list.get(position).name.charAt(0);
            txtView.setText(""+ch);
            txtName.setText(list.get(position).name);

           // imageView.setImageResource(R.drawable.ic_launcher);
            txtStatus.setText(list.get(position).number);





        } catch (Exception e) {
            e.printStackTrace();
        }



        return rowView;
    }
}


public class Contacts extends Activity {

    public ScrollView s;
    ListView listView;
    int x;
    LinkedList<Contact> list=new LinkedList<>();
    LinearLayout l;
    public static String number;
    boolean persist=true;
    EditText search;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppControler.unlock(getApplicationContext());
        AppControler.bContacts=true;
        setContentView(R.layout.activity_contacts);
        activity=this;
        persist=true;
        ContentResolver cr = getContentResolver();

        final DatabaseHandler db=new DatabaseHandler(getApplicationContext());
        search=(EditText)findViewById(R.id.searcher);

        list=db.getSpecificContacts("");
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
                list=db.getSpecificContacts(search.getText().toString());
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
            final CustomListContacts adapter = new
                    CustomListContacts(this, list,contactIDList);
            listView=(ListView)findViewById(R.id.ContactList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {


                    DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                    String uri="tel:"+db.getContact(Integer.parseInt(contactIDList.get(position))).number;
                    db.close();
                    Intent intent=new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {


                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    if(Build.VERSION.SDK_INT > 10) {
                    PopupMenu popupMenu=new PopupMenu(getApplicationContext(),view);
                    popupMenu.getMenuInflater().inflate(R.menu.popupconttacts,popupMenu.getMenu());

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getTitle().equals("Delete Contact"))
                            {

                                AlertDialog.Builder alert = new AlertDialog.Builder(activity);

                                alert.setTitle("Are you sure ");
                                alert.setMessage("This will permanently delete this contact. Make sure you saved it back to your phone");



                                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // Canceled.
                                    }
                                });

                                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        if(contactIDList.get(position).equals("1"))
                                        {
                                            Toast.makeText(getApplicationContext(),"Edit Hider in settings",Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                                        db.deleteContact(Integer.parseInt(contactIDList.get(position)));
                                        db.close();
                                        contactIDList.remove(position);
                                        list.remove(position);
                                        //view.setClickable(false);
                                        adapter.notifyDataSetChanged();

                                    }
                                });

                                alert.show();



                            }
                            if(item.getTitle().equals("Send back to phone"))
                            {

                                Intent intent = new Intent(Intent.ACTION_INSERT);
                                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

                                DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                                Contact temp=db.getContact(Integer.parseInt(contactIDList.get(position)));
                                db.close();
// Just two examples of information you can send to pre-fill out data for the
// user.  See android.provider.ContactsContract.Intents.Insert for the complete
// list.
                                intent.putExtra(ContactsContract.Intents.Insert.NAME, temp.name);
                                intent.putExtra(ContactsContract.Intents.Insert.PHONE, temp.number);

// Send with it a unique request code, so when you get called back, you can
// check to make sure it is from the intent you launched (ideally should be
// some public static final so receiver can check against it)
                                int PICK_CONTACT = 100;
                                startActivityForResult(intent, PICK_CONTACT);
                            }
                            if(item.getTitle().equals("Edit Contact"))
                            {
                                if(contactIDList.get(position).equals("1"))
                                {
                                    Toast.makeText(getApplicationContext(),"Edit Hider in settings",Toast.LENGTH_SHORT).show();
                                    return true;
                                }
                                Intent intent=new Intent(getApplicationContext(),EditContact.class);
                                intent.putExtra("id",contactIDList.get(position));
                                startActivity(intent);
                            }
                            return true;
                        }
                    });

                        popupMenu.show();
                    }
                    else
                    {
                        AlertDialog.Builder alert = new AlertDialog.Builder(activity);

                        alert.setTitle("Operations on contact");



                        alert.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(contactIDList.get(position).equals("1"))
                                {
                                    Toast.makeText(getApplicationContext(),"Edit Hider in settings",Toast.LENGTH_SHORT).show();

                                }
                                Intent intent=new Intent(getApplicationContext(),EditContact.class);
                                intent.putExtra("id",contactIDList.get(position));
                                startActivity(intent);
                            }
                        });

                        alert.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if(contactIDList.get(position).equals("1"))
                                {
                                    Toast.makeText(getApplicationContext(),"Edit Hider in settings",Toast.LENGTH_SHORT).show();

                                }

                                DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                                db.deleteContact(Integer.parseInt(contactIDList.get(position)));
                                db.close();
                                contactIDList.remove(position);
                                list.remove(position);
                                //view.setClickable(false);
                                adapter.notifyDataSetChanged();
                            }
                        });

                        alert.show();
                    }
                    return true;
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

    @Override
    protected void onPause() {
        super.onPause();
        AppControler.bContacts=false;
        AppControler.setUnsetMinimised();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
/*
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);

        actionBar.setTitle("  Contacts");*/
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor((getString(R.string.actionbarcolor)))));
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId())
        {
            case R.id.create:
                intent = new Intent(getApplicationContext(), CreateContact.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.all:
                intent = new Intent(getApplicationContext(), ALLContacts.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.log:
                intent = new Intent(getApplicationContext(), CallLog.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.setting:
                intent = new Intent(getApplicationContext(), Setting.class);
                Setting.intent=new Intent(MainActivity.context, Contacts.class);

                startActivity(intent);
                return true;
            default:
                return true;

        }


    }




    @Override public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&persist==true)
        {
            persist=false;
            Toast.makeText(getApplicationContext(),"Press one more time to exit",Toast.LENGTH_SHORT).show();
        }

        else if(keyCode==KeyEvent.KEYCODE_BACK&&persist==false)
        {
            onBackPressed();
        }

        else if(keyCode== KeyEvent.KEYCODE_MENU)
        {
            openOptionsMenu();
        }



        return true;
    }


}


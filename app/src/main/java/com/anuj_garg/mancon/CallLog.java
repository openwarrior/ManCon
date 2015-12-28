package com.anuj_garg.mancon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

class CustomListCallLog extends ArrayAdapter<String> {
    private final Activity context;
    private  final LinkedList<Call> list;
    private  final ArrayList<String> callIDlist;
    public CustomListCallLog(Activity context,
                                 LinkedList<Call> list, ArrayList<String> callIDlist) {
        super(context, R.layout.contact_list_item, callIDlist);
        this.context = context;
        this.callIDlist = callIDlist;
        this.list=list;

    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View rowView= null;
        try {
            final LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.cal_log_item, null, true);
            TextView txtName = (TextView) rowView.findViewById(R.id.nameContact);
            TextView txttime = (TextView) rowView.findViewById(R.id.time);
            TextView txtduration = (TextView) rowView.findViewById(R.id.duration);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.imgContact);

            if(list.get(position).callType.equals(""+android.provider.CallLog.Calls.MISSED_TYPE))
            {
                imageView.setImageResource(R.drawable.missed);
            }
            else if(list.get(position).callType.equals(""+android.provider.CallLog.Calls.OUTGOING_TYPE))
            {

                imageView.setImageResource(R.drawable.outgoing);
            }
            else if(list.get(position).callType.equals(""+android.provider.CallLog.Calls.INCOMING_TYPE))
            {

                imageView.setImageResource(R.drawable.incoming);
            }

            DatabaseHandler db=new DatabaseHandler(getContext());

            Contact contact=db.getContactByPhone(list.get(position).number);
            if(contact!=null) {

                txtName.setTextColor(Color.BLACK);
                txtName.setText(contact.name);
            }
            else if((contact=db.getALLContactByPhone(list.get(position).number))!=null)
            {
                txtName.setTextColor(Color.GRAY);
                txtName.setText(contact.name);

            }
            else
            {

                txtName.setTextColor(Color.GRAY);
                txtName.setText(list.get(position).number);
            }
            db.close();

                txttime.setText(list.get(position).time);
                txtduration.setText(list.get(position).duration);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rowView;
    }
}


public class CallLog extends Activity {
    public ScrollView s;
    MenuItem deleteAll;
    ListView listView;
    int x;
    LinkedList<Call> list=new LinkedList<>();
    LinearLayout l;
    public static String number;
    CustomListCallLog adapterChanger;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppControler.unlock(getApplicationContext());
        AppControler.bCallLog=true;
        setContentView(R.layout.activity_call_log);
        ContentResolver cr = getContentResolver();
        activity=this;

        final DatabaseHandler db=new DatabaseHandler(getApplicationContext());

        list=db.getAllLog();

        db.close();
        showLog(list);


    }

    public void showLog(final LinkedList<Call> list)
    {
        try {


            final ArrayList<String> callIDList=new ArrayList<>();
            for(int i=0;i<list.size();i++)
            {
                callIDList.add(""+list.get(i).id);
            }
            final CustomListCallLog adapter = new
                    CustomListCallLog(this, list,callIDList);
            adapterChanger=adapter;
            listView=(ListView)findViewById(R.id.LogList);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                    Call call=db.getLog(Integer.parseInt(callIDList.get(position)));
                    db.close();

                    String uri="tel:"+call.number;
                    Intent intent=new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(intent);

                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                if(Build.VERSION.SDK_INT > 10) {
                PopupMenu popupMenu=new PopupMenu(getApplicationContext(),view);
                popupMenu.getMenuInflater().inflate(R.menu.popuplogs,popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("Delete this log"))
                        {

                            DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                            db.deleteLog(Integer.parseInt(callIDList.get(position)));
                            db.close();
                            callIDList.remove(position);
                            list.remove(position);
                            //view.setClickable(false);
                            adapter.notifyDataSetChanged();
                        }
                        if(item.getTitle().equals("Delete all log of this contact"))
                        {
                            DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                            db.deleteLogByNumber(list.get(position).number);
                            final LinkedList<Call> newlist=db.getAllLog();
                            db.close();

                            showLog(newlist);
                        }
                        return true;
                    }
                });

                    popupMenu.show();
                }
                else
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(activity);

                    alert.setTitle("Are you sure ");
                    alert.setMessage("this will delete items from call log");



                    alert.setNegativeButton("This", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                            db.deleteLogByNumber(list.get(position).number);
                            final LinkedList<Call> newlist=db.getAllLog();
                            db.close();

                            showLog(newlist);
                        }
                    });

                    alert.setPositiveButton("All related", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                            db.deleteLog(Integer.parseInt(callIDList.get(position)));
                            db.close();
                            callIDList.remove(position);
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



    }

    @Override
    protected void onPause() {
        super.onPause();
        AppControler.bCallLog=false;
        AppControler.setUnsetMinimised();
    }
@Override
protected void onResume()
{
    super.onResume();

    final DatabaseHandler db=new DatabaseHandler(getApplicationContext());

    list=db.getAllLog();
    db.close();

    showLog(list);

    adapterChanger.notifyDataSetChanged();
   //showLog(list);

}



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_call_log, menu);



        return true;
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
            case R.id.selected:
                intent = new Intent(getApplicationContext(), Contacts.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.all:
                intent = new Intent(getApplicationContext(), ALLContacts.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.deleteAll:
                AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle("Are you sure ");
                alert.setMessage("this will delete your whole call log");



                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                        db.deleteLogComplete();

                        LinkedList<Call> list=db.getAllLog();
                        showLog(list);
                        db.close();
                    }
                });

                alert.show();
                return true;
            case R.id.setting:
                intent = new Intent(getApplicationContext(), Setting.class);
                Setting.intent=new Intent(MainActivity.context, CallLog.class);

                startActivity(intent);
                return true;
            default:
                return true;

        }


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
}

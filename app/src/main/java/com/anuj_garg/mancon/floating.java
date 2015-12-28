package com.anuj_garg.mancon;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class floating extends Activity {
    Activity floating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating);

        try {
            floating=this;
            Bundle extras = getIntent().getExtras();
            String value="";
            if (extras != null) {
                   if(extras.getString("state").equals("kill"))
                   {
                       finish();
                       onDestroy();
                       return;
                   }
                value = extras.getString("number");
            }
            DatabaseHandler db=new DatabaseHandler(getApplicationContext());
            Contact contact=db.getContactByPhone(value);
            db.close();
            TextView txt=(TextView)findViewById(R.id.nameOnCall);
            if(txt!=null)
            {txt.setText(contact.getName());}

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_floating, menu);

        ActionBar actionBar = getActionBar();

        return true;
    }





    @Override
    protected void onNewIntent(Intent intent)
    {
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.getString("state").equals("kill")) {
                finish();
                onDestroy();
                return;
            }
            else if (extras.getString("state").equals("out"))
            {
                DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                Contact contact=db.getContactByPhone(extras.getString("number"));
                db.close();
                TextView txt=(TextView)findViewById(R.id.nameOnCall);
                txt.setText(contact.getName());

            }
        }



    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.anuj_garg.mancon;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


public class NotificationPassword extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_password);

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("general", Context.MODE_PRIVATE);
        if(sharedPreferences.getBoolean("passwordSecurity",false)) {
            AppControler.locked = true;
        }

        if(AppControler.locked==false) {

            startActivity(new Intent(getApplicationContext(), CallLog.class));
            finish();

        }

        LinearLayout mainpage=(LinearLayout)findViewById(R.id.mainpage);
        mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new AppControler()).unlock(getApplicationContext());
            }
        });


    }


    @Override
    protected void onResume()
    {
        super.onRestart();


        if(AppControler.locked==false) {

            startActivity(new Intent(getApplicationContext(), CallLog.class));
            finish();

        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("general", Context.MODE_PRIVATE);

        if(sharedPreferences.getBoolean("passwordSecurity",false)) {
            AppControler.locked = true;
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification_password, menu);

        ActionBar actionBar=getActionBar();
        if(Build.VERSION.SDK_INT > 10) {
            actionBar.hide();
        }
        return true;
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

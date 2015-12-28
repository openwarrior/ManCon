package com.anuj_garg.mancon;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends Activity {

    static Context  MainActi;
    public static ContentResolver contentResolver;
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentResolver = this.getContentResolver();
        context=this.getApplicationContext();
        Intent intent=new Intent(this, DBprovider.class);
        //startService(intent);

        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=this.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        CountryZipCode="+"+CountryZipCode;


       SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("general", Context.MODE_PRIVATE);
        int accountExist=sharedPreferences.getInt("exist",0);
        if(accountExist==0) {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("exist",1);
            editor.putBoolean("service", true);
            editor.putBoolean("notificationMissedCall", true);
            editor.putBoolean("passwordSecurity",false);
            editor.putBoolean("hideronoff",false);
            editor.putString("country", CountryZipCode);
            editor.putString("hider", "1234");
            editor.commit();

            DatabaseHandler db=new DatabaseHandler(getApplicationContext());
            db.createContact(new Contact(0,"Hider","1234"));
            db.close();
        }

        try {

            Thread t=new RawContacts(getApplicationContext());
            t.start();
        } catch (Exception e) {

            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        if(sharedPreferences.getBoolean("passwordSecurity",false)) {
            AppControler.locked = true;
        }



            startActivity(new Intent(getApplicationContext(), Contacts.class));
            finish();


        LinearLayout mainpage=(LinearLayout)findViewById(R.id.mainpage);
        mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppControler.unlock(getApplicationContext());
            }
        });


    }


    @Override
    protected void onResume()
    {
        super.onRestart();



                startActivity(new Intent(getApplicationContext(), Contacts.class));
            finish();


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
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

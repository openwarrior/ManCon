package com.anuj_garg.mancon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class Setting extends Activity {
        public static Intent intent=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppControler.bSettings=true;
        setContentView(R.layout.activity_setting);
        final Activity activity=this;

        TableRow appmode=(TableRow)findViewById(R.id.appmode);
        TableRow help=(TableRow)findViewById(R.id.helpSetting);
        TableRow notification=(TableRow)findViewById(R.id.notificationSetting);
//        TableRow changepassword=(TableRow)findViewById(R.id.changepassword);
//        TableRow passwordmode=(TableRow)findViewById(R.id.passwordmode);
        TableRow tellAfriend=(TableRow)findViewById(R.id.tellFriendSetting);
        TableRow about=(TableRow)findViewById(R.id.about);
        TableRow hider=(TableRow)findViewById(R.id.hider);
        TableRow hideronoff=(TableRow)findViewById(R.id.hideronoff);

        final CheckBox boxappmode=(CheckBox)findViewById(R.id.boxappMode);
      final CheckBox boxmisscallnotification=(CheckBox)findViewById(R.id.boxNotification);
//        final CheckBox boxpassword=(CheckBox)findViewById(R.id.boxpasswordMode);
        final CheckBox boxhider=(CheckBox)findViewById(R.id.boxhider);
        final TextView hiderdata=(TextView)findViewById(R.id.hiderdata);




        final SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("general", Context.MODE_PRIVATE);

        boxappmode.setChecked(sharedPreferences.getBoolean("service",false));
        boxmisscallnotification.setChecked(sharedPreferences.getBoolean("notificationMissedCall", true));
//        boxpassword.setChecked(sharedPreferences.getBoolean("passwordSecurity",false));
        boxhider.setChecked(sharedPreferences.getBoolean("hideronoff",false));
        hiderdata.setText(sharedPreferences.getString("hider","1234"));



        boolean x=sharedPreferences.getBoolean("service",false);
        boolean y=sharedPreferences.getBoolean("notificationMissedCall", true);
        boolean z=sharedPreferences.getBoolean("passwordSecurity",false);


        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(getApplicationContext(),Help.class));
            }
        });

        hider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(activity);

                alert.setTitle("Enter new hider key");
                alert.setMessage("Try to keep it simple as it is not recoverable");



// Set an EditText view to get user input
                final EditText input = new EditText(activity);
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                
                alert.setView(input);


                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if(input.getText().toString().length()!=4)
                        {
                            Toast.makeText(getApplicationContext(),"Hider can only be of 4 digit",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        String value = input.getText().toString();
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("hider", value);
                      editor.commit();

                        hiderdata.setText(value);
                        DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                        db.updateContact("Hider",value,1);
                        db.close();

                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();

            }
        });

        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boxmisscallnotification.toggle();
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean("notificationMissedCall",boxmisscallnotification.isChecked());
                editor.commit();
            }
        });

        hideronoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boxhider.toggle();
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean("hideronoff",boxhider.isChecked());
                editor.commit();

                if(boxhider.isChecked()==true)
                {
                    PackageManager p = getPackageManager();
                    ComponentName componentName = new ComponentName(getApplicationContext(), Launcher.class); // activity which is first time open in manifiest file which is declare as <category android:name="android.intent.category.LAUNCHER" />
                    p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                }
                else
                {
                    PackageManager p = getPackageManager();
                    ComponentName componentName = new ComponentName(getApplicationContext(), Launcher.class);
                    p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                }
            }
        });

        appmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boxappmode.toggle();
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putBoolean("service",boxappmode.isChecked());
                editor.commit();
            }
        });
//        changepassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(!boxpassword.isChecked())
//                {
//                    Toast.makeText(getApplicationContext(),"First turn on password security",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                DatabaseHandler db=(new DatabaseHandler(getApplicationContext()));
//                if(db.isthereanyold())
//                {
//                    Intent intent1=new Intent(getApplicationContext(),Lock.class);
//                    intent1.putExtra("text","Enter old password");
//                    intent1.putExtra("type","check");
//                    startActivity(intent1);
//                }
//                else
//                {
//                    Intent intent1=new Intent(getApplicationContext(),Lock.class);
//                    intent1.putExtra("text","Enter new password");
//                    intent1.putExtra("type","set");
//                    startActivity(intent1);
//                }
//                db.close();
//
//            }
//        });

        tellAfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareBody = "Hey, We all have some contacts that we don't want to be there in our main contact list, some for them we don't want a call log is generated. And after that, Will not be it wish come true if we can hide that app all together???  Try ManCon... https://play.google.com/store/apps/details?id=com.anuj_garg.mancon ";
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "MANCON - ");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share Using"));
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),About.class));
            }
        });

//        passwordmode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(boxpassword.isChecked())
//                {
//                    Intent intent1=new Intent(getApplicationContext(),Lock.class);
//                    intent1.putExtra("text","Enter password");
//                    intent1.putExtra("type","enter");
//                    intent1.putExtra("typeInEnter","turnoffsecurity");
//                    startActivity(intent1);
//                }
//                else{
//
//                    DatabaseHandler db=(new DatabaseHandler(getApplicationContext()));
//                    if(db.isthereanyold()){
//                        Intent intent1=new Intent(getApplicationContext(),Lock.class);
//                        intent1.putExtra("text","Enter password");
//                        intent1.putExtra("type","enter");
//                        intent1.putExtra("typeInEnter","turnonsecurity");
//                        startActivity(intent1);
//                    }
//                    else
//                    {
//                        Intent intent1=new Intent(getApplicationContext(),Lock.class);
//                        intent1.putExtra("text","Enter new password");
//                        intent1.putExtra("type","set");
//                        startActivity(intent1);
//                    }
//                    db.close();
//                }
//            }
//        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        final CheckBox boxappmode=(CheckBox)findViewById(R.id.boxappMode);
        final CheckBox boxmisscallnotification=(CheckBox)findViewById(R.id.boxNotification);
//        final CheckBox boxpassword=(CheckBox)findViewById(R.id.boxpasswordMode);


        final SharedPreferences sharedPreferences=getApplicationContext().getApplicationContext().getSharedPreferences("general", Context.MODE_PRIVATE);

        boxappmode.setChecked(sharedPreferences.getBoolean("service",true));
        boxmisscallnotification.setChecked(sharedPreferences.getBoolean("notificationMissedCall", true));
//        boxpassword.setChecked(sharedPreferences.getBoolean("passwordSecurity",false));



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }
    @Override public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
        {

            startActivity(intent);
            finish();
        }
        //onBackPressed();
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppControler.bSettings=false;
        AppControler.setUnsetMinimised();
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

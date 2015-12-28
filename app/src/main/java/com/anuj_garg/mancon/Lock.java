package com.anuj_garg.mancon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Lock extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        final Intent intent=getIntent();
        String text=intent.getStringExtra("text");
        text=text.toUpperCase();
        final String type=intent.getStringExtra("type");


        TextView title=(TextView)findViewById(R.id.text);
        final EditText password=(EditText)findViewById(R.id.password);
        password.setCursorVisible(false);
        title.setText(text);

        password.addTextChangedListener(new TextWatcher() {

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

                if(password.getText().toString().trim().length()==4)
                {
                    if(type.equals("check"))
                    {
                        DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                        boolean b=db.compare(password.getText().toString());
                        db.close();
                        if(b) {
                            Intent intent1 = new Intent(getApplicationContext(), Lock.class);
                            intent1.putExtra("text", "Enter new password");
                            intent1.putExtra("type", "set");
                            startActivity(intent1);
                        }
                        finish();
                    }else if(type.equals("set"))
                    {
                        DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                        db.passwordsetattempt1(password.getText().toString());
                        db.close();
                        Intent intent1=new Intent(getApplicationContext(),Lock.class);
                        intent1.putExtra("text","Re-enter Password");
                        intent1.putExtra("type","confirm");
                        startActivity(intent1);
                        finish();
                    }
                    else if(type.equals("confirm"))
                   {
                       DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                        boolean b=db.passwordsetattempt2(password.getText().toString());
                       db.close();
                       if(b)
                       {
                          /* AlertDialog.Builder builder=new AlertDialog.Builder(getApplicationContext());
                           builder.setMessage("Your password is successfully changed").setTitle("Success");
                           AlertDialog dialog=builder.create();*/
                           Toast.makeText(getApplicationContext(),"new password set",Toast.LENGTH_SHORT).show();
                           SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("general", Context.MODE_PRIVATE);
                           SharedPreferences.Editor editor = sharedPreferences.edit();

                           editor.putBoolean("passwordSecurity",true);
                           editor.commit();
                           finish();

                       }
                       else
                       {
                           Toast.makeText(getApplicationContext(),"both password didn't match",Toast.LENGTH_SHORT).show();
                           finish();
                       }
                   }
                    else if(type.equals("enter"))
                   {
                       String typeInEnter=intent.getStringExtra("typeInEnter");
                       DatabaseHandler db=new DatabaseHandler(getApplicationContext());
                       boolean b=db.compare(password.getText().toString());
                       db.close();
                       if(b)
                       {

                           if(typeInEnter!=null)
                           {
                               if(typeInEnter.equals("turnonsecurity"))
                               {
                                   SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("general", Context.MODE_PRIVATE);
                                   SharedPreferences.Editor editor = sharedPreferences.edit();

                                   editor.putBoolean("passwordSecurity",true);
                                   editor.commit();
                                   finish();
                               }

                              else if(typeInEnter.equals("turnoffsecurity"))
                               {
                                   SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("general", Context.MODE_PRIVATE);
                                   SharedPreferences.Editor editor = sharedPreferences.edit();

                                   editor.putBoolean("passwordSecurity",false);
                                   editor.commit();
                                   finish();
                               }
                               else if(typeInEnter.equals("unlock"))
                               {
                                  // AppControler.locked=false;
                                   finish();
                               }
                           }

                       }
                       else
                       {

                           if(typeInEnter.equals("turnonsecurity"))
                           {

                               Toast.makeText(getApplicationContext(),"Wrong password",Toast.LENGTH_SHORT).show();
                               finish();
                           }

                           else if(typeInEnter.equals("turnoffsecurity"))
                           {

                               Toast.makeText(getApplicationContext(),"Wrong password",Toast.LENGTH_SHORT).show();
                               finish();
                           }
                           else if(typeInEnter.equals("unlock"));
                           {

                               Toast.makeText(getApplicationContext(),"Wrong password",Toast.LENGTH_SHORT).show();

                               Intent intent1=new Intent(getApplicationContext(),Lock.class);
                               intent1.putExtra("text","Enter password");
                               intent1.putExtra("type","enter");
                               intent1.putExtra("typeInEnter","unlock");
                               startActivity(intent1);
                           }
                           finish();
                       }

                   }
                }

            }

        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lock, menu);
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

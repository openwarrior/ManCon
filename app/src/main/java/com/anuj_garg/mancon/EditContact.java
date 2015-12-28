package com.anuj_garg.mancon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class EditContact extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppControler.bEdit=true;
        setContentView(R.layout.activity_edit_contact);

        final EditText name=(EditText)findViewById(R.id.addName);
        final EditText number=(EditText)findViewById(R.id.addNumber);
        final Button submit=(Button)findViewById(R.id.addSubmit);

        Intent intent=getIntent();
        final String id=intent.getStringExtra("id");

        Contact contact=(new DatabaseHandler(getApplicationContext())).getContact(Integer.parseInt(id));
        name.setText(contact.name);
        number.setText(contact.number);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number.getText().toString().trim().length()<10)
                {
                    Toast.makeText(getApplicationContext(), "Phone number should be of atleast 10 digit", Toast.LENGTH_SHORT).show();

                }
                else {
                    String finalNumber=ContactItem.hygenicNumber(number.getText().toString());
                    DatabaseHandler db=new DatabaseHandler(getApplicationContext());

                    Contact contact=db.getContactByPhone(finalNumber);
                    db.close();
                    if(contact==null || contact.contact_id==Integer.parseInt(id))
                    {

                        db=new DatabaseHandler(getApplicationContext());
                        db.updateContact(name.getText().toString(), finalNumber, Integer.parseInt(id));
                        Toast.makeText(getApplicationContext(),"updated "+db.getContactByPhone(finalNumber).name,Toast.LENGTH_SHORT).show();
                        db.close();
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Number already exist with name "+contact.name,Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
        name.addTextChangedListener(new TextWatcher() {

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

                if(name.getText().toString().trim().length()!=0)
                {
                    submit.setEnabled(true);
                }
                else
                {
                    submit.setEnabled(false);
                }

            }

        });


    }


    @Override
    protected void onPause() {
        super.onPause();
        AppControler.bEdit=false;
        AppControler.setUnsetMinimised();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_contact, menu);
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

    @Override public boolean onKeyDown(int keyCode,KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK&&event.getRepeatCount()==0)
        {

            startActivity(new Intent(getApplicationContext(),Contacts.class));
            finish();
        }
        //onBackPressed();
        return true;
    }
}

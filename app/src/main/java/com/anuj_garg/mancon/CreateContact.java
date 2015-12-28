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


public class CreateContact extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppControler.bCreate=true;
        setContentView(R.layout.activity_create_contact);

        final EditText name=(EditText)findViewById(R.id.addName);
        final EditText number=(EditText)findViewById(R.id.addNumber);
        final Button submit=(Button)findViewById(R.id.addSubmit);
        submit.setEnabled(false);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(number.getText().toString().trim().length()<10)
                {
                    Toast.makeText(getApplicationContext(),"Phone number should be of atleast 10 digit",Toast.LENGTH_SHORT).show();

                }
                else {
                   String finalNumber=ContactItem.hygenicNumber(number.getText().toString());
                    DatabaseHandler db=new DatabaseHandler(getApplicationContext());

                    Contact contact=db.getContactByPhone(finalNumber);
                    db.close();
                    if(contact==null)
                    {

                        db=new DatabaseHandler(getApplicationContext());
                        db.createContact(new Contact(0,name.getText().toString(),finalNumber));
                        Toast.makeText(getApplicationContext(),"added "+db.getContactByPhone(finalNumber).name,Toast.LENGTH_SHORT).show();
                        db.close();
                        finish();
                        startActivity(new Intent(getApplicationContext(),Contacts.class));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_contact, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppControler.bCreate=false;
        AppControler.setUnsetMinimised();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId())
        {
            case R.id.all:
                intent = new Intent(getApplicationContext(), ALLContacts.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.selected:
                intent = new Intent(getApplicationContext(), Contacts.class);
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
                Setting.intent=new Intent(MainActivity.context, CreateContact.class);
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

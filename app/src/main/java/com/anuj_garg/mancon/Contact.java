package com.anuj_garg.mancon;

/**
 * Created by Anuj Garg on 2/21/2015.
 */
public class Contact {
    public int contact_id;
    public String name;
    public String number;

    public Contact(int contact_id, String name, String number) {
        this.contact_id = contact_id;
        this.name = name;
        this.number = number;
    }

    public int getContact_id()
    {
        return contact_id;
    }

    public String getName()
    {

        return name;
    }

    public String getNumber()
    {
        return number;

    }


}
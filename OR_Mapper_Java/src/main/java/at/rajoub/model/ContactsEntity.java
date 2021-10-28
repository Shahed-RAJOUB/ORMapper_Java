package at.rajoub.model;

import at.rajoub.meta.annotation.Table;

@Table(tableName = "contacts")
public class ContactsEntity {

    private int contact_id;
    private int customer_id;
    private String contact_name;
    private String phone;
    private String email;

    public int getcontact_id(){
        return contact_id;
    }
    public int getcustomer_id(){
        return customer_id;
    }
    public String getcontact_name(){
        return contact_name;
    }
    public String getphone(){
        return phone;
    }
    public String getemail(){
        return email;
    }
}

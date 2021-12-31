package at.rajoub.model;

import at.rajoub.meta.annotation.Column;
import at.rajoub.meta.annotation.ForiegnKey;
import at.rajoub.meta.annotation.PrimaryKey;
import at.rajoub.meta.annotation.Table;

@Table(tableName = "contacts")
public class ContactsEntity {

    @PrimaryKey
    private int contact_id;
    @ForiegnKey
    private int customer_id;
    @Column
    private String contact_name;
    @Column
    private String phone;
    @Column
    private String email;

    public int getcontact_id() {
        return contact_id;
    }

    public int getcustomer_id() {
        return customer_id;
    }

    public String getcontact_name() {
        return contact_name;
    }

    public String getphone() {
        return phone;
    }

    public String getemail() {
        return email;
    }
}

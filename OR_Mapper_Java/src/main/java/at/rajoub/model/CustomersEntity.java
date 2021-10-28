package at.rajoub.model;

import at.rajoub.meta.annotation.Table;

@Table(tableName = "customers")
public class CustomersEntity {

    private int customer_id;
    private String customer_name;

    public int getcustomer_id() {
        return customer_id;
    }

    public String getcustomer_name() {
        return customer_name;
    }
}

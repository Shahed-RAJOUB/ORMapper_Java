package at.rajoub.model;

import at.rajoub.meta.annotation.Column;
import at.rajoub.meta.annotation.PrimaryKey;
import at.rajoub.meta.annotation.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(tableName = "customers")
@Data
@NoArgsConstructor
/*
 * Example to build the Entity class
 */
public class CustomersEntity {

    @PrimaryKey
    @Column (columnName = "customer_id")
    private int customer_id;
    @Column(columnName = "customer_name")
    private String customer_name;
}

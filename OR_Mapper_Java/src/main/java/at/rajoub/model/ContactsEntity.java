package at.rajoub.model;

import at.rajoub.meta.annotation.Column;
import at.rajoub.meta.annotation.ForeignKey;
import at.rajoub.meta.annotation.PrimaryKey;
import at.rajoub.meta.annotation.Table;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(tableName = "contacts")
@Data
@NoArgsConstructor
@Builder
public class ContactsEntity {

    @PrimaryKey
    @Column (columnName = "contact_id")
    private int contact_id;
    @ForeignKey(joinedTo = CustomersEntity.class)
    @Column (columnName = "customer_id")
    private int customer_id;
    @Column (columnName = "contact_name")
    private String contact_name;
    @Column(columnName = "phone")
    private String phoneNum;
    @Column(columnName = "email")
    private String email;

}

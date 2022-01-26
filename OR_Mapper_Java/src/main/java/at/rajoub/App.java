package at.rajoub;

import at.rajoub.model.ContactsEntity;
import at.rajoub.model.CustomersEntity;
import at.rajoub.model.StudentEntity;
import at.rajoub.model.TestEntity;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


/**
 * This App is an example to show how to use ORM
 */
public class App {

    public static void main(String[] args) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Create a mapper
        Orm orm = new Orm();
        orm.setCash(true);

        // Select all
        List<ContactsEntity> allDataInContanct_1 = orm.SelectAllRows(ContactsEntity.class);
        // to check if cash works
        List<ContactsEntity> allDataInContanct_2 = orm.SelectAllRows(ContactsEntity.class);
        List<CustomersEntity> allDataInCustomer = orm.SelectAllRows(CustomersEntity.class);
        List<TestEntity> allDataInTest = orm.SelectAllRows(TestEntity.class);

        // Select the properties in a table
        List<String> fields = orm.SelectAllColumns(ContactsEntity.class);

        // Select by unique id
        ContactsEntity rowAtIdFromContact = orm.SelectByID(ContactsEntity.class, 1);

        // Select by a property value
        List<ContactsEntity> rowAtColumnFromContact_1 = orm.SelectbyColumn(ContactsEntity.class, "email", "john.doe@bluebird.dev");
        List<ContactsEntity> rowAtColumnFromContact_2 = orm.SelectbyColumn(ContactsEntity.class, "phone", "(408)-111-1234");
        List<ContactsEntity> allRowswithFK = orm.SelectbyColumn(ContactsEntity.class, "customer_id", 1);


        //Join is double Selects: all customers where contact id is 2 // select contacts with foriegn key = id //contacts for customer with id = 1
        Map<CustomersEntity, List<ContactsEntity>> customersjoinedcontacts = orm.JoinbyForiegnKey(CustomersEntity.class , ContactsEntity.class,1);


        // Join All properties between two tables 1-n relationship // order is important
        Map<CustomersEntity, List<ContactsEntity>> contactsForCustumers = orm.JoinAll(CustomersEntity.class , ContactsEntity.class);

        // Create a new object of the entity
        ContactsEntity smith = ContactsEntity.builder()
                .customer_id(2).contact_name("Smith").email("Smith@gmail.com").phoneNum("01254789").build();
        // Insert in a table
        orm.Insert(smith);

        // Change an old object of the entity
        smith.setContact_name("Stefan");
        // update a value in a row in a table
        orm.UpdatebyID(6 , smith);

        // Delete a row in a table
        orm.DeleteRowbyId(12 , ContactsEntity.class);

        orm.CreateTable(StudentEntity.class);

        orm.DropTable(StudentEntity.class);

        System.out.println(allDataInContanct_1);
        System.out.println(allDataInContanct_2);
        System.out.println(allDataInCustomer);
        System.out.println(allDataInTest);
        System.out.println(fields);
        System.out.println(rowAtIdFromContact);
        System.out.println(allRowswithFK);
        System.out.println(rowAtColumnFromContact_1);
        System.out.println(rowAtColumnFromContact_2);
       // System.out.println(customersjoinedcontacts);
        //System.out.println(contactsForCustumers);

    }
}

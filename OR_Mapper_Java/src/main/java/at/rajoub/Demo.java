package at.rajoub;

import at.rajoub.meta.Argument;
import at.rajoub.meta.CompareOperator;
import at.rajoub.meta.WhereOperators;
import at.rajoub.model.ContactsEntity;
import at.rajoub.model.CustomersEntity;
import at.rajoub.model.StudentEntity;
import at.rajoub.model.TestEntity;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * This Demo is an example to show how to use ORM
 * Each Section gives an example of the function used in this Mapper
 * Each result is present in the Console
 */
public class Demo {

    public static void main(String[] args) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        // Create a mapper
        Orm orm = new Orm();
        // Set Cash
        orm.setCash(false);

        // Create Student table
        orm.CreateTable(StudentEntity.class);

        //Drop student table
        orm.DropTable(StudentEntity.class);

        // Select all
        List<ContactsEntity> allDataInContanct_1 = orm.SelectAllRows(ContactsEntity.class);
        System.out.println(allDataInContanct_1);


        // to check if cash works
        List<ContactsEntity> allDataInContanct_2 = orm.SelectAllRows(ContactsEntity.class);
        List<CustomersEntity> allDataInCustomer = orm.SelectAllRows(CustomersEntity.class);
        List<TestEntity> allDataInTest = orm.SelectAllRows(TestEntity.class);
        System.out.println(allDataInContanct_2);
        System.out.println(allDataInCustomer);
        System.out.println(allDataInTest);

        // Select the properties as column names in a table
        List<String> fields = orm.SelectAllColumns(ContactsEntity.class);
        System.out.println(fields);

        // Select by unique id
        ContactsEntity rowAtIdFromContact = orm.SelectByID(ContactsEntity.class, 1);
        System.out.println(rowAtIdFromContact);

        // Select by a property value
        List<ContactsEntity> rowAtColumnFromContact_1 = orm.SelectByColumn(ContactsEntity.class, "email", "john.doe@bluebird.dev");
        List<ContactsEntity> rowAtColumnFromContact_2 = orm.SelectByColumn(ContactsEntity.class, "phone", "(408)-111-1234");
        List<ContactsEntity> allRowswithFK = orm.SelectByColumn(ContactsEntity.class, "customer_id", 1);
        System.out.println(allRowswithFK);
        System.out.println(rowAtColumnFromContact_1);
        System.out.println(rowAtColumnFromContact_2);


        // Using Select where with Operator and multiple arguments
        List<Argument> Arguments = new ArrayList<>();
        Argument arg1 = Argument.builder().column("customer_id").value(2).opr(CompareOperator.Equal).build();
        Argument arg2 = Argument.builder().column("email").value("Smith@gmail.com").opr(CompareOperator.Equal).build();
        Arguments.add(arg1);
        Arguments.add(arg2);
        List<ContactsEntity> ContactsWithMultipleArguments = orm.SelectMultiArgQuery(ContactsEntity.class ,  WhereOperators.AND , Arguments);
        System.out.println(ContactsWithMultipleArguments);


        // using Select where with one argument and operator
        Argument arg = Argument.builder().column("customer_id").value(2).opr(CompareOperator.Less).build();
        List<ContactsEntity> ContactsWithOneArgument = orm.SelectOneArgQuery(ContactsEntity.class ,  arg);
        System.out.println(ContactsWithOneArgument);


        //Join is double Selects: all customers where contact id is 2 // select contacts with foriegn key = id //contacts for customer with id = 1
        Map<CustomersEntity, List<ContactsEntity>> customersjoinedcontacts = orm.JoinbyForiegnKey(CustomersEntity.class , ContactsEntity.class,1);
        System.out.println(customersjoinedcontacts);

        // Join All properties between two tables 1-n relationship // order is important
        Map<CustomersEntity, List<ContactsEntity>> contactsForCustumers = orm.JoinAll(CustomersEntity.class , ContactsEntity.class);
        System.out.println(contactsForCustumers);

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

    }
}

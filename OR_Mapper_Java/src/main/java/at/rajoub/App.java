package at.rajoub;

import at.rajoub.meta.annotation.Table;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final Logger LOGGER = LogManager.getLogger(App.class.getName());
    public static void main( String[] args ) throws SQLException {

        // Create a mapper
        Orm orm = new Orm();

        // Select all
        List<ArrayList<Object>> allDataInContanct = orm.SelectAllRows("ContactsEntity");
        List<ArrayList<Object>> allDataInCustomer = orm.SelectAllRows("CustomersEntity");
        List<ArrayList<Object>> allDataInTest = orm.SelectAllRows("TestEntity");

        // Select the properties ina table
        List<String> fields = orm.SelectAllColumns("ContactsEntity");

        // Select by unique id
        List<Object> rowAtIdFromContact = orm.SelectByID("ContactsEntity",1);

        // Select by a property value
        List<ArrayList<Object>>  rowAtColumnFromContact = orm.SelectbyColumn("ContactsEntity","email","john.doe@bluebird.dev");


        System.out.println(allDataInContanct);
        System.out.println(allDataInCustomer);
        System.out.println(allDataInTest);
        System.out.println(fields);

        LOGGER.info("info");

        //TestEntity.class.getDeclaredFields();
    }
}

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
        Orm orm = new Orm();

       // List<String> fields = orm.SelectAllColumns("ContactsEntity");
        List<ArrayList<Object>> data1 = orm.SelectAllRows("ContactsEntity");
        List<ArrayList<Object>> data2 = orm.SelectAllRows("CustomersEntity");
        List<ArrayList<Object>> data3 = orm.SelectAllRows("TestEntity");

       // System.out.println(fields);
        System.out.println(data1);
        System.out.println(data2);
        System.out.println(data3);

        LOGGER.info("info");

        //TestEntity.class.getDeclaredFields();
    }
}

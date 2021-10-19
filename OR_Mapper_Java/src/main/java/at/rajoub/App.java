package at.rajoub;

import at.rajoub.annotation.Table;
import at.rajoub.model.TestEntity;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        Reflections reflections = new Reflections("");

        Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Table.class);

        classes.stream()
                .flatMap(it -> Arrays.stream(it.getDeclaredFields()))
                .forEach(field -> System.out.println(((Field) field).getName()));

        //TestEntity.class.getDeclaredFields();
    }
}

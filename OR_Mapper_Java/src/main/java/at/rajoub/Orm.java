package at.rajoub;

import at.rajoub.meta.Entity;
import at.rajoub.meta.annotation.Table;
import at.rajoub.persistence.CrudOperations;
import org.reflections.Reflections;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Orm {
    // check all annotations
    Reflections reflections = new Reflections("at.rajoub");
    // scan for table
    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Table.class);

    public List<String> SelectAllColumns(String tableName) {
        List<String> fieldsNames = new ArrayList<>();

        //classes.forEach(it -> fieldsNames.add(it.getSimpleName()));
        List<Class<?>> selected = classes.stream()
                .filter(attr->tableName.equals(attr.getSimpleName()))
                .collect(Collectors.toList());

        Entity entity = new Entity(selected.get(0));

        entity.getFields().stream().forEach(f-> fieldsNames.add((f.getColumnName())));

        return fieldsNames;

    }

    public List<ArrayList<Object>> SelectAllRows(String e) throws SQLException {
        List<String> columns = SelectAllColumns(e);
        List<Class<?>> selected = classes.stream()
                .filter(attr->e.equals(attr.getSimpleName()))
                .collect(Collectors.toList());
        Entity entity = new Entity(selected.get(0));
        CrudOperations op = new CrudOperations();
       return op.SelectAll(entity , columns);
    }
}

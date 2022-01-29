package at.rajoub;

import at.rajoub.meta.Argument;
import at.rajoub.meta.Entity;
import at.rajoub.meta.Field;
import at.rajoub.meta.WhereOperators;
import at.rajoub.meta.annotation.Table;
import at.rajoub.persistence.CrudOperations;
import lombok.Setter;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/*
 * All the Operations build in this ORM
 */
public class Orm {
    @Setter
    private boolean cash;
    /*
     * Cashing the class and objects by their ids in it
     */
    private Map<Class<?>, Map<Object, Object>> cashSet = new HashMap<>();

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Reflection calls                                                                                        //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /*
     * check all annotations using reflections library
     * Save the entities in a map with the name of the class as key and the entity as value
     */
    Reflections reflections = new Reflections("at.rajoub");
    /*
     * Scan the tables
     */
    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Table.class);
    Map<Class<?>, Entity> entities = classes.stream()
            .map(it -> Entity.builder().type(it).build())
            .collect(Collectors.toMap(Entity::getType, it -> it));

    /*
     * Find thename of all columns in a table
     * returns a list of the names as Strings
     */
    public <T> List<String> SelectAllColumns(Class<T> entityClass) {
        List<String> fieldsNames = new ArrayList<>();
        Entity entity = entities.get(entityClass);
        entity.getFields().stream().forEach(f -> fieldsNames.add((f.getColumnName())));
        return fieldsNames;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Crud methods                                                                                            //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * Select all the roas in a table and all the values
     * returns a list of objects of the requested class
     */
    public <T> List<T> SelectAllRows(Class<T> entityClass) throws SQLException {
        if (cash) {
            if (cashSet != null && cashSet.containsKey(entityClass) && cashSet.get(entityClass) != null) {
                List<Object> cashResults = new ArrayList<>();
                cashSet.get(entityClass).forEach((k, v) -> {
                    cashResults.add(v);
                });
                return (List<T>) cashResults;
            } else {
                Entity entity = entities.get(entityClass);
                CrudOperations op = new CrudOperations();
                List<Object> results = op.SelectAll(entity);
                putCash(results, entity);
                return (List<T>) results;
            }
        } else {
            Entity entity = entities.get(entityClass);
            CrudOperations op = new CrudOperations();
            List<Object> results = op.SelectAll(entity);
            putCash(results, entity);
            return (List<T>) results;
        }

    }

    /*
     * Select a row by ID from a table
     * requires PK in a table as a unique value
     * returns an Object of the required class
     */
    public <T> T SelectByID(Class<T> entityClass, int i) {
        if (cash) {
            if (cashSet != null && cashSet.containsKey(entityClass) && cashSet.get(entityClass) != null) {
                if (cashSet.get(entityClass).containsKey(i)) {
                    List<Object> cashResults = new ArrayList<>();
                    cashSet.get(entityClass).forEach((k, v) -> {
                        if (k.equals(i)) {
                            cashResults.add(v);
                        }
                    });
                    return (T) cashResults.get(0);
                } else {
                    Entity entity = entities.get(entityClass);
                    CrudOperations op = new CrudOperations();
                    return (T) op.SelectbyValue(entity, entity.getPrimaryKey(), i).stream().findFirst()
                            .orElseThrow(() -> new RuntimeException("You need to add Primary key annotation to your table!"));
                }
            } else {
                Entity entity = entities.get(entityClass);
                CrudOperations op = new CrudOperations();
                return (T) op.SelectbyValue(entity, entity.getPrimaryKey(), i).stream().findFirst()
                        .orElseThrow(() -> new RuntimeException("You need to add Primary key annotation to your table!"));
            }
        } else {
            Entity entity = entities.get(entityClass);
            CrudOperations op = new CrudOperations();
            return (T) op.SelectbyValue(entity, entity.getPrimaryKey(), i).stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("You need to add Primary key annotation to your table!"));
        }
    }

    /*
     * Select the rows that fulfill the argument where the column equal to the given value
     * returns a list of objects of the required class
     */
    public <T> List<T> SelectByColumn(Class<T> entityClass, String column, Object value) {
        Entity entity = entities.get(entityClass);
        CrudOperations op = new CrudOperations();
        Field f = entity.getFields().stream().filter(attr -> column.equals(attr.getColumnName())).findFirst()
                .orElseThrow(() -> new RuntimeException("You need to add column name annotation to your table!"));
        return (List<T>) op.SelectbyValue(entity, f, value);
    }

    /*
     * Selects all the Objects that are connected to other object with 1-n relation using FK at a specific PK
     * requires PK and FK in both classes
     * returns a Map with the object with the PK key and the other Objects with FK as Value
     */
    public <T1, T2> Map<T1, List<T2>> JoinbyForiegnKey(Class<T1> firstTable, Class<T2> secondTable, int id) throws SQLException {
        Map<T1, List<T2>> SelectedJoin = new HashMap<>();
        Entity entity1 = entities.get(firstTable);
        Entity entity2 = entities.get(secondTable);

        Field field = entity2.getFields().stream()
                .filter(Field::isForeignKey)
                .filter(f -> f.getFKJoinedEntity()
                        .equals(firstTable))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("You need to add Foreign Key annotation to your second table and join it to a class!"));

        T1 SelectedRowsSecondtable = SelectByID(firstTable, id);
        List<T2> SelectedRowsFirsttable = SelectByColumn(secondTable, field.getColumnName(), id);
        SelectedJoin.put(SelectedRowsSecondtable, SelectedRowsFirsttable);
        return SelectedJoin;
    }

    /*
     * Selects all the Objects that are connected to other object with 1-n relation using FK
     * requires PK and FK in both classes
     * returns a Map with the object with the PK key and the other Objects with FK as Value
     */
    public <T1, T2> Map<T1, List<T2>> JoinAll(Class<T1> firstTable, Class<T2> secondTable) throws SQLException {
        Map<T1, List<T2>> SelectedJoin = new HashMap<>();
        CrudOperations op = new CrudOperations();
        Entity entity1 = entities.get(firstTable);
        Entity entity2 = entities.get(secondTable);
        Field field = entity2.getFields().stream()
                .filter(Field::isForeignKey)
                .filter(f -> f.getFKJoinedEntity()
                        .equals(firstTable))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("You need to add Foreign Key annotation to your second table and join it to a class!"));
        List<Object> Ids = op.SelectAllIds(entity1);
        for (Object o : Ids) {
            T1 SelectedRowsSecondtable = SelectByID(firstTable, (Integer) o);
            List<T2> SelectedRowsFirsttable = SelectByColumn(secondTable, field.getColumnName(), o);
            SelectedJoin.put(SelectedRowsSecondtable, SelectedRowsFirsttable);
        }
        return SelectedJoin;
    }

    /*
     * Insert in a table after building an object
     * returns the new ID of the Object and updates the ID of the Object
     */
    public <T> void Insert(Object entity) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Entity e = entities.get(entity.getClass());
        CrudOperations op = new CrudOperations();
        Object id = op.insert(e, entity);
        e.getPrimaryKey().getSetMethod().invoke(entity, id);
        System.out.println(entity);
        if (cash) {
            Map<Object, Object> temp = new HashMap<>();
            temp.put(id, entity);
            cashSet.put(e.getType(), temp);
        }
    }

    /*
     * Update a row in a table of a special Object at its ID
     * requires the new Object and its ID
     */
    public <T> void UpdatebyID(Object id, Object entity) throws InvocationTargetException, IllegalAccessException, SQLException {
        Entity e = entities.get(entity.getClass());
        CrudOperations op = new CrudOperations();
        op.update(id, e, entity);
        if (cash) {
            cashSet.get(e.getType());
            Map<Object, Object> temp = new HashMap<>();
            temp.put(id, entity);
            cashSet.put(e.getType(), temp);
        }
    }

    /*
     * Delete a  row by the ID from the table
     */
    public <T> void DeleteRowbyId(int id, Class<T> entityClass) throws SQLException {
        Entity e = entities.get(entityClass);
        CrudOperations op = new CrudOperations();
        op.deletebyID(id, e);
    }

    /*
     * Creates a table if a classs Entity was created with all the required annotations
     * reterns a table in Postgres
     */
    public <T> void CreateTable(Class<T> entityClass) throws SQLException {
        Entity e = entities.get(entityClass);
        CrudOperations op = new CrudOperations();
        op.createTable(e);
    }

    /*
     * Drops the table that was saved in Postgres Container
     */
    public <T> void DropTable(Class<T> entityClass) throws SQLException {
        Entity e = entities.get(entityClass);
        CrudOperations op = new CrudOperations();
        op.dropTable(e);
    }

    /*
     * Specific selects where a column and . or operator need to return a list of objects of the required class
     * Requires more than one Argument to do AND and OR
     * returns list of Objects
     */
    public <T> List<T> SelectMultiArgQuery(Class<T> entityClass, WhereOperators opr, List<Argument> arguments) {
        String WhereOpr;
        if (opr.equals(WhereOperators.OR)) {
            WhereOpr = " OR ";
        } else if (opr.equals(WhereOperators.AND)) {
            WhereOpr = " AND ";
        } else {
            throw new RuntimeException("No operator provided");
        }
        Entity entity = entities.get(entityClass);
        CrudOperations op = new CrudOperations();
        List<Field> fields = new ArrayList<>();
        for (Argument ar : arguments) {
            Field f = entity.getFields().stream().filter(attr -> ar.getColumn().equals(attr.getColumnName())).findFirst()
                    .orElseThrow(() -> new RuntimeException("You need to add column name annotation to your table!"));
            fields.add(f);
        }
        return (List<T>) op.SelectLogicQuery(entity, fields, WhereOpr, arguments);
    }

    /*
     * Specific selects where for one Condition
     * Requires  one Argument to do Equal , NotEqual , LIKE , Greater ,Less
     * returns list of Objects
     */
    public <T> List<T> SelectOneArgQuery(Class<T> entityClass, Argument arg) {
        Entity entity = entities.get(entityClass);
        CrudOperations op = new CrudOperations();
        Field f = entity.getFields().stream().filter(attr -> arg.getColumn().equals(attr.getColumnName())).findFirst()
                .orElseThrow(() -> new RuntimeException("You need to add column name annotation to your table!"));
        return (List<T>) op.SelectWithOneArg(entity, f, arg);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Cash methods                                                                                            //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
     * Update the Cash after using it after Selects functions
     */
    public void putCash(List<Object> results, Entity entity) {
        Object pk = null;
        Map<Object, Object> temp = new HashMap<>();
        for (Object o : results) {
            try {
                pk = entity.getPrimaryKey().getGetMethod().invoke(o);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
            temp.put(pk, o);
        }
        cashSet.put(entity.getType(), temp);
    }

}

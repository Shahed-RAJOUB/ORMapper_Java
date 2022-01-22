package at.rajoub;

import at.rajoub.meta.Entity;
import at.rajoub.meta.Field;
import at.rajoub.meta.annotation.Table;
import at.rajoub.persistence.CrudOperations;
import lombok.Setter;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Orm {
    @Setter
    private boolean cash;
    // Cashing the class and objects by their ids in it
    private Map<Class<?>, Map<Object, Object>> cashSet = new HashMap<>();

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Reflection calls                                                                                        //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // check all annotations
    Reflections reflections = new Reflections("at.rajoub");
    // scan for table
    Set<Class<?>> classes = reflections.getTypesAnnotatedWith(Table.class);
    Map<Class<?>, Entity> entities = classes.stream()
            .map(it -> Entity.builder().type(it).build())
            .collect(Collectors.toMap(Entity::getType, it -> it));

    public <T> List<String> SelectAllColumns(Class<T> entityClass) {
        List<String> fieldsNames = new ArrayList<>();
        Entity entity = entities.get(entityClass);
        entity.getFields().stream().forEach(f -> fieldsNames.add((f.getColumnName())));
        return fieldsNames;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Crud methods                                                                                            //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
                }else{
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

    public <T> List<T> SelectbyColumn(Class<T> entityClass, String column, Object value) {
        Entity entity = entities.get(entityClass);
        CrudOperations op = new CrudOperations();
        Field f = entity.getFields().stream().filter(attr -> column.equals(attr.getFieldName())).findFirst()
                .orElseThrow(() -> new RuntimeException("You need to add column name annotation to your table!"));
        return (List<T>) op.SelectbyValue(entity, f, value);
    }

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
        List<T2> SelectedRowsFirsttable = SelectbyColumn(secondTable, field.getColumnName(), id);
        SelectedJoin.put(SelectedRowsSecondtable, SelectedRowsFirsttable);
        return SelectedJoin;
    }

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
            List<T2> SelectedRowsFirsttable = SelectbyColumn(secondTable, field.getColumnName(), o);
            SelectedJoin.put(SelectedRowsSecondtable, SelectedRowsFirsttable);
        }
        return SelectedJoin;
    }

    public <T> void Insert(Object entity) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Entity e = entities.get(entity.getClass());
        CrudOperations op = new CrudOperations();
        op.insert(e, entity);
    }

    public <T> void UpdatebyID(Object id, Object entity) throws InvocationTargetException, IllegalAccessException, SQLException {
        Entity e = entities.get(entity.getClass());
        CrudOperations op = new CrudOperations();
        op.update(id, e, entity);
    }

    public <T> void DeleteRowbyId(int id, Class<T> entityClass) throws SQLException {
        Entity e = entities.get(entityClass);
        CrudOperations op = new CrudOperations();
        op.deletebyID(id, e);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Cash methods                                                                                            //
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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

package at.rajoub.persistence;

import at.rajoub.meta.Argument;
import at.rajoub.meta.CompareOperator;
import at.rajoub.meta.Entity;
import at.rajoub.meta.Field;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/*
 * Functions used to establish the queries committed in SQL
 */
public class CrudOperations {
    /*
     * Select * from a table
     */
    public List<Object> SelectAll(Entity entity) throws SQLException {
        DbContent dbContent = new DbContent();
        List<Object> dataList = new ArrayList<>();
        String query = "SELECT * FROM " + entity.getTableName();
        PreparedStatement statement;
        ResultSet rs;
        try {
            statement = dbContent.getC().prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                Object o = entity.getType().getConstructor().newInstance();
                entity.getFields().forEach(f -> {
                    try {
                        Object columnValue = rs.getObject(f.getColumnName());
                        f.getSetMethod().invoke(o, columnValue);
                    } catch (SQLException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
                dataList.add(o);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return dataList;

    }

    /*
     * Select * from a table where a column name equal a value
     */
    public List<Object> SelectbyValue(Entity entity, Field field, Object value) {
        DbContent dbContent = new DbContent();
        List<Object> dataList = new ArrayList<>();
        String query;
        if (value instanceof String) {
            query = "SELECT * FROM " + entity.getTableName() + " Where \"" + field.getColumnName() + "\" = '" + value + "'";
        } else {
            query = "SELECT * FROM " + entity.getTableName() + " Where " + field.getColumnName() + " = " + value;
        }
        PreparedStatement statement;
        ResultSet rs;
        try {
            statement = dbContent.getC().prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                Object o = entity.getType().getConstructor().newInstance();
                entity.getFields().forEach(f -> {
                    try {
                        Object columnValue = rs.getObject(f.getColumnName());
                        f.getSetMethod().invoke(o, columnValue);
                    } catch (SQLException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
                dataList.add(o);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return dataList;
    }

    /*
     * Select * from a table where PK_Id equals something
     */
    public List<Object> SelectAllIds(Entity entity) throws SQLException {
        DbContent dbContent = new DbContent();
        List<Object> IdsList = new ArrayList<>();
        String query = "SELECT " + entity.getPrimaryKey().getColumnName() + " FROM " + entity.getTableName();
        PreparedStatement statement;
        ResultSet rs;
        try {
            statement = dbContent.getC().prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                IdsList.add(rs.getObject(entity.getPrimaryKey().getColumnName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return IdsList;
    }

    /*
     * Insert into a table values
     * returns the new ID of the entity and updates the entity
     */
    public Object insert(Entity e, Object entity) throws SQLException, InvocationTargetException, IllegalAccessException {
        DbContent dbContent = new DbContent();
        String columns = e.allFieldsNoPK().stream()
                .map(Field::getColumnName)
                .collect(Collectors.joining("\",\""));
        String strq = " ?, ";
        String str = " ? ";
        String query = "INSERT INTO " + e.getTableName() + " (\"" + columns + "\") VALUES (" + strq.repeat(e.getFields().size() - 2) + str + ")";
        PreparedStatement statement = dbContent.getC().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        int i = 1;
        for (Field f : e.getFields()) {
            if (!f.isPrimaryKey()) {
                statement.setObject(i, f.getGetMethod().invoke(entity));
                i++;
            }
        }
        statement.execute();
        ResultSet rs = statement.getGeneratedKeys();
        Object generatedKey = null;
        while (rs.next()) {
            generatedKey = rs.getObject(1);
        }
        System.out.println("Inserted record's ID: " + generatedKey);

        dbContent.getC().commit();
        statement.close();
        return generatedKey;
    }

    /*
     * update  table Set values where other values
     */
    public void update(Object pKID, Entity e, Object entity) throws SQLException, InvocationTargetException, IllegalAccessException {
        DbContent dbContent = new DbContent();
        String query;
        if (e.allFieldsNoPK().size() > 1) {
            String columns = e.allFieldsNoPK().stream()
                    .map(Field::getColumnName)
                    .collect(Collectors.joining("\",\""));
            String strq = " ?, ";
            String str = " ? ";
            query = "UPDATE " + e.getTableName() + " SET (\"" + columns + "\") = (" + strq.repeat(e.getFields().size() - 2) + str + ") where \"" + e.getPrimaryKey().getColumnName() + "\" = " + pKID.toString();
        } else {
            String columns = e.allFieldsNoPK().stream()
                    .map(Field::getColumnName)
                    .collect(Collectors.joining("\",\""));
            String strq = " ?, ";
            String str = " ? ";
            query = "UPDATE " + e.getTableName() + " SET \"" + columns + "\" = (" + strq.repeat(e.getFields().size() - 2) + str + ") where \"" + e.getPrimaryKey().getColumnName() + "\" = " + pKID.toString();
        }

        PreparedStatement statement = dbContent.getC().prepareStatement(query);
        int i = 1;
        for (Field f : e.getFields()) {
            if (!f.isPrimaryKey()) {
                statement.setObject(i, f.getGetMethod().invoke(entity));
                i++;
            }
        }
        statement.executeUpdate();
        dbContent.getC().commit();
        statement.close();
    }

    /*
     * Delete from a table a row
     */
    public void deletebyID(Object id, Entity e) throws SQLException {
        DbContent dbContent = new DbContent();
        String query = "DELETE FROM " + e.getTableName() + " WHERE " + e.getPrimaryKey().getColumnName() + " = ?";
        PreparedStatement statement = dbContent.getC().prepareStatement(query);
        statement.setObject(1, id);
        statement.executeUpdate();
        dbContent.getC().commit();
        statement.close();
    }

    /*
     * Create table if not exist
     */
    public void createTable(Entity e) throws SQLException {
        DbContent dbContent = new DbContent();
        String pKStr = e.getPrimaryKey().getColumnName() + " integer generated always as identity constraint " + e.getTableName() + "_pkey primary key,";
        List<Field> fields = e.allFieldsNoPK();
        List<String> fieldsStr = new ArrayList<>();
        for (Field f : fields) {
            if (f.getType().equals(String.class)) {
                String temp = f.getColumnName() + " varchar(255)";
                fieldsStr.add(temp);
            } else if (f.getType().equals(Integer.class)) {
                String temp = f.getColumnName() + " integer";
                fieldsStr.add(temp);
            } else if (f.getType().equals(Long.class)) {
                String temp = f.getColumnName() + " bigserial";
                fieldsStr.add(temp);
            } else {
                String temp = f.getColumnName() + " text";
                fieldsStr.add(temp);
            }
        }
        String buildStr = "";
        for (int i = 0; i < fields.size(); i++) {
            int index = i + 1;
            if (index != fields.size()) {
                buildStr = buildStr + fieldsStr.get(i) + ",";
            } else {
                buildStr = buildStr + fieldsStr.get(i);
            }
        }
        String query = "create table if not exists " + e.getTableName() + " ( " + pKStr + buildStr + " ); alter table " + e.getTableName() + " owner to postgres;";
        PreparedStatement statement = dbContent.getC().prepareStatement(query);
        statement.execute();
        dbContent.getC().commit();
        statement.close();
    }

    /*
     * Drop table if exist
     */
    public void dropTable(Entity e) throws SQLException {
        DbContent dbContent = new DbContent();
        String query = "DROP TABLE if exists " + e.getTableName() + " cascade;";
        PreparedStatement statement = dbContent.getC().prepareStatement(query);
        statement.execute();
        dbContent.getC().commit();
        statement.close();
    }

    /*
     * Select * where multiple arguments with AND and OR
     */
    public List<Object> SelectLogicQuery(Entity entity, List<Field> fields, String whereOpr, List<Argument> arguments) {
        DbContent dbContent = new DbContent();
        List<Object> dataList = new ArrayList<>();
        String query = "SELECT * FROM " + entity.getTableName() + " Where ";
        List<String> Condition = new ArrayList<>();
        List<String> argOprs = StrArguments(arguments);
        for (int i = 0; i < arguments.size(); i++) {
            String temp = "";
            if (arguments.get(i).getValue() instanceof String) {
                temp = "\"" + fields.get(i).getColumnName() + "\" " + argOprs.get(i) + " '" + arguments.get(i).getValue() + "' ";
                Condition.add(temp);
            } else {
                temp = fields.get(i).getColumnName() + argOprs.get(i) + arguments.get(i).getValue();
                Condition.add(temp);
            }
        }
        for (int i = 0; i < arguments.size(); i++) {
            int index = i + 1;
            if (index != arguments.size()) {
                query = query + Condition.get(i) + whereOpr;
            } else {
                query = query + Condition.get(i);
            }
        }
        PreparedStatement statement;
        ResultSet rs;
        try {
            statement = dbContent.getC().prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                Object o = entity.getType().getConstructor().newInstance();
                entity.getFields().forEach(f -> {
                    try {
                        Object columnValue = rs.getObject(f.getColumnName());
                        f.getSetMethod().invoke(o, columnValue);
                    } catch (SQLException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
                dataList.add(o);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        return dataList;
    }

    /*
     * returns the correct expression of the operator
     */
    private List<String> StrArguments(List<Argument> arguments) {
        List<String> str = new ArrayList<>();
        for (Argument r : arguments) {
            String temp = "";
            if (r.getOpr().equals(CompareOperator.Equal)) {
                temp = " = ";
                str.add(temp);
            } else if (r.getOpr().equals(CompareOperator.NotEqual)) {
                temp = " <> ";
                str.add(temp);
            } else if (r.getOpr().equals(CompareOperator.LIKE)) {
                temp = " LIKE ";
                str.add(temp);
            } else if (r.getOpr().equals(CompareOperator.Greater)) {
                temp = " > ";
                str.add(temp);
            } else if (r.getOpr().equals(CompareOperator.Less)) {
                temp = " < ";
                str.add(temp);
            } else {
                throw new RuntimeException("No operator provided");
            }
        }
        return str;
    }

    /*
     *  Select * where oe argument logical comparison required
     */
    public Object SelectWithOneArg(Entity entity, Field field, Argument arg) {
        DbContent dbContent = new DbContent();
        List<Object> dataList = new ArrayList<>();
        String query = "SELECT * FROM " + entity.getTableName() + " Where ";
        List<Argument> arguments = new ArrayList<>();
        arguments.add(arg);
        List<String> argOprs = StrArguments(arguments);
        String Condition = "";
        if (arg.getValue() instanceof String) {
            Condition = "\"" + field.getColumnName() + "\" " + argOprs.get(0) + " '" + arg.getValue() + "' ";
        } else {
            Condition = field.getColumnName() + argOprs.get(0) + arg.getValue();
        }
        query = query + Condition;
        PreparedStatement statement;
        ResultSet rs;
        try {
            statement = dbContent.getC().prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                Object o = entity.getType().getConstructor().newInstance();
                entity.getFields().forEach(f -> {
                    try {
                        Object columnValue = rs.getObject(f.getColumnName());
                        f.getSetMethod().invoke(o, columnValue);
                    } catch (SQLException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                });
                dataList.add(o);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        return dataList;
    }
}

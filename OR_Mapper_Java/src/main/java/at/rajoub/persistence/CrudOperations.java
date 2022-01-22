package at.rajoub.persistence;

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


public class CrudOperations {

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

    public List<Object> SelectbyValue(Entity entity, Field field, Object value) {
        DbContent dbContent = new DbContent();
        List<Object> dataList = new ArrayList<>();
        String query;
        if (value instanceof String) {
            query = "SELECT * FROM " + entity.getTableName() + " Where " + field.getColumnName() + " = '" + value + "'";
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

    public void insert(Entity e, Object entity) throws SQLException, InvocationTargetException, IllegalAccessException {
        DbContent dbContent = new DbContent();
        String columns = e.allFieldsNoPK().stream()
                .map(Field::getColumnName)
                .collect(Collectors.joining(","));
        String strq = " ?, ";
        String str = " ? ";
        String query = "INSERT INTO " + e.getTableName() + " (" + columns + ") VALUES (" + strq.repeat(e.getFields().size() - 2) + str + ")";
        PreparedStatement statement = dbContent.getC().prepareStatement(query , Statement.RETURN_GENERATED_KEYS);
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
    }

    public void update(Object pKID, Entity e, Object entity) throws SQLException, InvocationTargetException, IllegalAccessException {
        DbContent dbContent = new DbContent();
        String columns = e.allFieldsNoPK().stream()
                .map(Field::getColumnName)
                .collect(Collectors.joining(","));
        String strq = " ?, ";
        String str = " ? ";
        String query = "UPDATE " + e.getTableName() + " SET (" + columns + ") = (" + strq.repeat(e.getFields().size() - 2) + str + ") where "+ e.getPrimaryKey().getColumnName()+" = "+pKID.toString();
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

    public void deletebyID(Object id , Entity e) throws SQLException {
        DbContent dbContent = new DbContent();
        String query = "DELETE FROM "+e.getTableName()+" WHERE "+e.getPrimaryKey().getColumnName()+" = ?";
        PreparedStatement statement = dbContent.getC().prepareStatement(query);
        statement.setObject(1, id);
        statement.executeUpdate();
        dbContent.getC().commit();
        statement.close();
    }
}

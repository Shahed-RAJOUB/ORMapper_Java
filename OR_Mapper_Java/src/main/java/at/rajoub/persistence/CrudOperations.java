package at.rajoub.persistence;

import at.rajoub.meta.Entity;
import at.rajoub.meta.Field;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CrudOperations {

    public  List<Object> SelectAll(Entity entity) throws SQLException {
        DbContent dbContent = new DbContent();
        List<Object> dataList= new ArrayList<>();
        String query = "SELECT * FROM "+entity.getTableName();
        PreparedStatement statement ;
        ResultSet rs;
        try {
            statement = dbContent.getC().prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                Object o = entity.getType().getConstructor().newInstance();
                entity.getFields().forEach(f->{
                    try {
                        Object columnValue = rs.getObject(f.getColumnName());
                        f.getSetMethod().invoke(o,columnValue);
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
        List<Object> dataList= new ArrayList<>();
        String query;
        if(value instanceof String) {
             query = "SELECT * FROM " + entity.getTableName() + " Where " + field.getColumnName() + " = '" + value + "'";
        }else{
             query = "SELECT * FROM " + entity.getTableName() + " Where " + field.getColumnName() + " = " + value ;
        }
        PreparedStatement statement ;
        ResultSet rs;
        try {
            statement = dbContent.getC().prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                Object o = entity.getType().getConstructor().newInstance();
                entity.getFields().forEach(f->{
                    try {
                        Object columnValue = rs.getObject(f.getColumnName());
                        f.getSetMethod().invoke(o,columnValue);
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
        List<Object> IdsList= new ArrayList<>();
        String query = "SELECT "+ entity.getPrimaryKey().getColumnName() + " FROM "+entity.getTableName();
        PreparedStatement statement ;
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
}

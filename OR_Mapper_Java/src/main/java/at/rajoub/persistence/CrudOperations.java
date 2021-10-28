package at.rajoub.persistence;

import at.rajoub.meta.Entity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class CrudOperations {

    public List<ArrayList<Object>> SelectAll(Entity entity, List<String> columns) throws SQLException {
        DbContent dbContent = new DbContent();
        List<ArrayList<Object>> dataList= new ArrayList<>();
        String query = "SELECT * FROM "+entity.getTableName();
        PreparedStatement statement ;
        ResultSet rs;
        try {
            statement = dbContent.getC().prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                ArrayList<Object> list = new ArrayList<Object>();
                for (int i = 0 ; i < columns.size() ; i++){
                    list.add(rs.getObject(columns.get(i)));
                }
               // entities = new Entity(rs.getInt("id"), rs.getString("date"), rs.getFloat("duration"), rs.getFloat("destination"), rs.getFloat("calories"), rs.getString("url"), rs.getString("tour_name"), rs.getString("ratings"));
                dataList.add(list);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return dataList;

    }
}

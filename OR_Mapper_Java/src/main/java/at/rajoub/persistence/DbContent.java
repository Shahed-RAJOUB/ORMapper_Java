package at.rajoub.persistence;

import lombok.Data;
import java.sql.Connection;
import java.sql.DriverManager;

@Data
public class DbContent {

    private Connection c;

    public DbContent() {
        try {
            DataBaseConfig data = DataBaseConfig.getInstance();
            c = DriverManager.getConnection(data.getUrl(), data.getUserName(), data.getPassword());
            c.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}

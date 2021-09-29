package at.rajoub.persistence;

import lombok.Data;
import lombok.extern.log4j.Log4j;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.log4j.Logger;

@Data
@Log4j
public class DbContent {
    private Connection c;

    public DbContent() {
        try {
            DataBaseConfig data = DataBaseConfig.getInstance();
            c = DriverManager.getConnection(data.getUrl(), data.getUserName(), data.getPassword());
            c.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
            Logger logger = Logger.getLogger(DbContent.class);
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}

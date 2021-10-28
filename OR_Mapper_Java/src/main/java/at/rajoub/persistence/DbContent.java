package at.rajoub.persistence;

import lombok.Data;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.Connection;
import java.sql.DriverManager;

@Data
public class DbContent {
    private static final Logger logger = LogManager.getLogger("Data Content connected");
    private Connection c;

    public DbContent() {
        try {
            DataBaseConfig data = DataBaseConfig.getInstance();
            c = DriverManager.getConnection(data.getUrl(), data.getUserName(), data.getPassword());
            c.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
            logger.info(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }
}

package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
//    private final String url = System.getenv("JDBC_URL");
//    private final String user = System.getenv("USERNAME");
//    private final String password = System.getenv("PASSWORD");

    public Connection getConnection() {
        try {
            String url = System.getenv("DB_URL");
            String user = System.getenv("USERNAME");
            String password = System.getenv("PASSWORD");
            return DriverManager.getConnection(url, user, password);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

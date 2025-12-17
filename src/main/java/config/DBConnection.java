package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private final String url = System.getenv("JDBC_URL");
    private final String user = System.getenv("USERNAME");
    private final String password = System.getenv("PASSWORD");

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("connexion OK!");
        }
        catch (SQLException e){
            System.out.println("Error" + e.getMessage());
        }
        return connection;
    }
}

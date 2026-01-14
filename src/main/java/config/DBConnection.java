package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
        public Connection getConnection() throws SQLException {

            System.out.println("JDBC_URL = " + System.getenv("JDBC_URL"));
            System.out.println("USERNAME = " + System.getenv("USERNAME"));
            System.out.println("PASSWORD = " + System.getenv("PASSWORD"));
            
            String url = System.getenv("JDBC_URL");
            String user = System.getenv("USERNAME");
            String password = System.getenv("PASSWORD");

            if (url == null || user == null || password == null) {
                throw new SQLException("Variables d'environnement manquantes");
            }
            return DriverManager.getConnection(url, user, password);
        }
}

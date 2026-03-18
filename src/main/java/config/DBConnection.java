package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class DBConnection {
    static Connection connection = null;
    public static Connection getConnection() throws SQLException {
        ResourceBundle bundle =  ResourceBundle.getBundle("db");
        String url = bundle.getString("DB_URL");
        String user = bundle.getString("DB_USER");
        String pass = bundle.getString("DB_PASSWORD");
        if(connection ==null || connection.isClosed()){
            try {
                connection = DriverManager.getConnection(url, user, pass);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return connection;
    }
}

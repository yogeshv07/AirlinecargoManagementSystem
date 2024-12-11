package airline;

import java.sql.*;

public class DatabaseConnection {

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/login_system";
        String user = "root";
        String password = "root";
        return DriverManager.getConnection(url, user, password);
    }
}

package admin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class AdminDB {
    private final String dbUrl;

    public AdminDB(String dbFilePath) {
        this.dbUrl = "jdbc:sqlite:" + dbFilePath;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbUrl);
    }
}
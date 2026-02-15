package service.persistence;

import admin.AdminDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqliteFinePolicyRepository {
    private final AdminDB db;

    public SqliteFinePolicyRepository(AdminDB db) {
        this.db = db;
    }

    public String getFinePolicyOption() {
        String sql = "SELECT value FROM admin_settings WHERE key='fine_policy'";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            String value = rs.next() ? rs.getString(1) : "A";
            if (value == null) {
                return "A";
            }
            String x = value.trim().toUpperCase();
            return (x.equals("A") || x.equals("B") || x.equals("C")) ? x : "A";
        } catch (SQLException e) {
            return "A";
        }
    }
}

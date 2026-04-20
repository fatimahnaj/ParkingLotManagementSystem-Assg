package service.persistence;

import admin.AdminDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class SqliteUnpaidFineRepository implements UnpaidFineRepository {
    private final AdminDB db;

    public SqliteUnpaidFineRepository(AdminDB db) {
        this.db = db;
    }

    @Override
    public double getUnpaidFineTotal(String plateNum) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM fines WHERE plate = ? AND is_paid = 0";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, plateNum);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        } catch (SQLException e) {
            System.out.println("[SqliteUnpaidFineRepository] getUnpaidFineTotal error: " + e.getMessage());
            return 0.0;
        }
    }

    @Override
    public void addUnpaidFine(String plateNum, String fineType, double amount) {
        if (amount <= 0) {
            return;
        }
        String sql = """
            INSERT INTO fines(plate, fine_type, amount, is_paid, created_at)
            VALUES(?, ?, ?, 0, ?)
        """;
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, plateNum);
            ps.setString(2, fineType == null ? "UNPAID_FINE" : fineType);
            ps.setDouble(3, amount);
            ps.setString(4, LocalDateTime.now().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[SqliteUnpaidFineRepository] addUnpaidFine error: " + e.getMessage());
        }
    }

    @Override
    public void clearUnpaidFines(String plateNum) {
        String sql = "UPDATE fines SET is_paid = 1 WHERE plate = ? AND is_paid = 0";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, plateNum);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[SqliteUnpaidFineRepository] clearUnpaidFines error: " + e.getMessage());
        }
    }
}

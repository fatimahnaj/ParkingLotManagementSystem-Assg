package service.persistence;

import admin.AdminDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import models.Ticket;
import models.parking.ParkingSpot;

public class SqliteParkingSessionRepository {
    public static class ActiveSessionRecord {
        private final int sessionId;
        private final String spotId;
        private final LocalDateTime entryTime;
        private final String finePolicyOption;

        public ActiveSessionRecord(int sessionId, String spotId, LocalDateTime entryTime, String finePolicyOption) {
            this.sessionId = sessionId;
            this.spotId = spotId;
            this.entryTime = entryTime;
            this.finePolicyOption = finePolicyOption;
        }

        public int getSessionId() {
            return sessionId;
        }

        public String getSpotId() {
            return spotId;
        }

        public LocalDateTime getEntryTime() {
            return entryTime;
        }

        public String getFinePolicyOption() {
            return finePolicyOption;
        }
    }

    private final AdminDB db;

    public SqliteParkingSessionRepository(AdminDB db) {
        this.db = db;
        ensureSchema();
    }

    public int startSession(Ticket ticket, LocalDateTime entryTime) {
        String plate = ticket.getVehicle().getPlateNum();
        ActiveSessionRecord existing = findActiveSessionByPlate(plate);
        if (existing != null) {
            return existing.getSessionId();
        }

        upsertVehicle(plate, ticket.getVehicle().getType());
        upsertParkingSpot(ticket.getParkingSpot(), "occupied");

        String sql = "INSERT INTO parking_sessions(plate, spot_id, entry_time, fine_policy, is_paid) VALUES(?, ?, ?, ?, 0)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, plate);
            ps.setString(2, ticket.getParkingSpot().getSpotID());
            ps.setString(3, entryTime.toString());
            ps.setString(4, getCurrentFinePolicyOption());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("[SqliteParkingSessionRepository] startSession error: " + e.getMessage());
        }
        return -1;
    }

    public ActiveSessionRecord findActiveSessionByPlate(String plate) {
        String sql = """
            SELECT session_id, spot_id, entry_time, fine_policy
            FROM parking_sessions
            WHERE plate = ? AND exit_time IS NULL
            ORDER BY session_id DESC
            LIMIT 1
        """;
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, plate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String entryText = rs.getString("entry_time");
                    return new ActiveSessionRecord(
                        rs.getInt("session_id"),
                        rs.getString("spot_id"),
                        LocalDateTime.parse(entryText),
                        normalizeFinePolicy(rs.getString("fine_policy"))
                    );
                }
            }
        } catch (Exception e) {
            System.out.println("[SqliteParkingSessionRepository] findActiveSessionByPlate error: " + e.getMessage());
        }
        return null;
    }

    public void completeSession(int sessionId, LocalDateTime exitTime, double feeAmount, double fineAmount, boolean paid) {
        completeSession(sessionId, exitTime, feeAmount, fineAmount, paid, "UNKNOWN", feeAmount + fineAmount, 0.0);
    }

    public void completeSession(
        int sessionId,
        LocalDateTime exitTime,
        double feeAmount,
        double fineAmount,
        boolean paid,
        String paymentMethod,
        double amountPaid,
        double remainingBalance
    ) {
        String sql = """
            UPDATE parking_sessions
            SET exit_time = ?, fee_amount = ?, fine_amount = ?, is_paid = ?, payment_method = ?, amount_paid = ?, remaining_balance = ?
            WHERE session_id = ?
        """;
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, exitTime.toString());
            ps.setDouble(2, feeAmount);
            ps.setDouble(3, fineAmount);
            ps.setInt(4, paid ? 1 : 0);
            ps.setString(5, paymentMethod == null ? "UNKNOWN" : paymentMethod);
            ps.setDouble(6, amountPaid);
            ps.setDouble(7, remainingBalance);
            ps.setInt(8, sessionId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[SqliteParkingSessionRepository] completeSession error: " + e.getMessage());
        }
    }

    public void updateSpotStatus(String spotId, String status) {
        String sql = "UPDATE parking_spots SET status = ? WHERE spot_id = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, spotId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[SqliteParkingSessionRepository] updateSpotStatus error: " + e.getMessage());
        }
    }

    public void insertUnpaidFine(String plate, String fineType, double amount) {
        if (amount <= 0) {
            return;
        }
        String sql = """
            INSERT INTO fines(plate, fine_type, amount, is_paid, created_at)
            VALUES(?, ?, ?, 0, ?)
        """;
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, plate);
            ps.setString(2, fineType);
            ps.setDouble(3, amount);
            ps.setString(4, LocalDateTime.now().toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[SqliteParkingSessionRepository] insertUnpaidFine error: " + e.getMessage());
        }
    }

    public void markAllFinesPaid(String plate) {
        String sql = "UPDATE fines SET is_paid = 1 WHERE plate = ? AND is_paid = 0";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, plate);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[SqliteParkingSessionRepository] markAllFinesPaid error: " + e.getMessage());
        }
    }

    public boolean backdateActiveSessionEntry(String plate, long hoursAgo) {
        String sql = """
            UPDATE parking_sessions
            SET entry_time = ?
            WHERE session_id = (
                SELECT session_id
                FROM parking_sessions
                WHERE plate = ? AND exit_time IS NULL
                ORDER BY session_id DESC
                LIMIT 1
            )
        """;
        LocalDateTime forcedEntry = LocalDateTime.now().minusHours(Math.max(1, hoursAgo));
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, forcedEntry.toString());
            ps.setString(2, plate);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[SqliteParkingSessionRepository] backdateActiveSessionEntry error: " + e.getMessage());
            return false;
        }
    }

    private void upsertVehicle(String plate, String vehicleType) {
        String sql = """
            INSERT INTO vehicles(plate, vehicle_type, is_vip)
            VALUES(?, ?, 0)
            ON CONFLICT(plate) DO UPDATE SET vehicle_type = excluded.vehicle_type
        """;
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, plate);
            ps.setString(2, vehicleType == null ? "Unknown" : vehicleType);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[SqliteParkingSessionRepository] upsertVehicle error: " + e.getMessage());
        }
    }

    private void upsertParkingSpot(ParkingSpot spot, String status) {
        String[] parsed = parseFloorAndRow(spot.getSpotID());
        String sql = """
            INSERT INTO parking_spots(spot_id, floor_id, row_id, spot_type, status)
            VALUES(?, ?, ?, ?, ?)
            ON CONFLICT(spot_id) DO UPDATE SET
                floor_id = excluded.floor_id,
                row_id = excluded.row_id,
                spot_type = excluded.spot_type,
                status = excluded.status
        """;
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, spot.getSpotID());
            ps.setString(2, parsed[0]);
            ps.setString(3, parsed[1]);
            ps.setString(4, spot.getType().name());
            ps.setString(5, status);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("[SqliteParkingSessionRepository] upsertParkingSpot error: " + e.getMessage());
        }
    }

    private String[] parseFloorAndRow(String spotId) {
        String floor = "UNKNOWN";
        String row = "UNKNOWN";
        if (spotId != null) {
            String[] parts = spotId.split("-");
            if (parts.length >= 2) {
                floor = parts[0];
                row = parts[1];
            }
        }
        return new String[]{floor, row};
    }

    private void ensureSchema() {
        String vehiclesSql = """
            CREATE TABLE IF NOT EXISTS vehicles (
                plate        TEXT PRIMARY KEY,
                vehicle_type TEXT NOT NULL,
                is_vip       INTEGER NOT NULL DEFAULT 0
            )
        """;
        String spotsSql = """
            CREATE TABLE IF NOT EXISTS parking_spots (
                spot_id     TEXT PRIMARY KEY,
                floor_id    TEXT NOT NULL,
                row_id      TEXT NOT NULL,
                spot_type   TEXT NOT NULL,
                status      TEXT NOT NULL
            )
        """;
        String sessionsSql = """
            CREATE TABLE IF NOT EXISTS parking_sessions (
                session_id  INTEGER PRIMARY KEY AUTOINCREMENT,
                plate       TEXT NOT NULL,
                spot_id     TEXT NOT NULL,
                entry_time  TEXT NOT NULL,
                exit_time   TEXT,
                fine_policy TEXT NOT NULL DEFAULT 'A',
                fee_amount  REAL NOT NULL DEFAULT 0,
                fine_amount REAL NOT NULL DEFAULT 0,
                is_paid     INTEGER NOT NULL DEFAULT 0,
                payment_method TEXT NOT NULL DEFAULT 'UNKNOWN',
                amount_paid REAL NOT NULL DEFAULT 0,
                remaining_balance REAL NOT NULL DEFAULT 0
            )
        """;
        String finesSql = """
            CREATE TABLE IF NOT EXISTS fines (
                fine_id    INTEGER PRIMARY KEY AUTOINCREMENT,
                plate      TEXT NOT NULL,
                fine_type  TEXT NOT NULL,
                amount     REAL NOT NULL,
                is_paid    INTEGER NOT NULL DEFAULT 0,
                created_at TEXT NOT NULL
            )
        """;
        String settingsSql = """
            CREATE TABLE IF NOT EXISTS admin_settings (
                key   TEXT PRIMARY KEY,
                value TEXT NOT NULL
            )
        """;
        String defaultPolicySql = """
            INSERT OR IGNORE INTO admin_settings(key, value)
            VALUES ('fine_policy', 'A')
        """;
        try (Connection c = db.getConnection();
             Statement st = c.createStatement()) {
            st.execute(vehiclesSql);
            st.execute(spotsSql);
            st.execute(sessionsSql);
            st.execute(finesSql);
            st.execute(settingsSql);
            st.execute(defaultPolicySql);
            ensureSessionColumns(c);
        } catch (SQLException e) {
            System.out.println("[SqliteParkingSessionRepository] ensureSchema error: " + e.getMessage());
        }
    }

    private void ensureSessionColumns(Connection c) throws SQLException {
        ensureColumn(c, "parking_sessions", "fine_policy", "TEXT NOT NULL DEFAULT 'A'");
        ensureColumn(c, "parking_sessions", "payment_method", "TEXT NOT NULL DEFAULT 'UNKNOWN'");
        ensureColumn(c, "parking_sessions", "amount_paid", "REAL NOT NULL DEFAULT 0");
        ensureColumn(c, "parking_sessions", "remaining_balance", "REAL NOT NULL DEFAULT 0");
    }

    private void ensureColumn(Connection c, String table, String column, String definition) throws SQLException {
        String pragma = "PRAGMA table_info(" + table + ")";
        boolean exists = false;
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(pragma)) {
            while (rs.next()) {
                if (column.equalsIgnoreCase(rs.getString("name"))) {
                    exists = true;
                    break;
                }
            }
        }
        if (!exists) {
            try (Statement st = c.createStatement()) {
                st.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
            }
        }
    }

    private String getCurrentFinePolicyOption() {
        String sql = "SELECT value FROM admin_settings WHERE key='fine_policy'";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? normalizeFinePolicy(rs.getString(1)) : "A";
        } catch (SQLException e) {
            return "A";
        }
    }

    private String normalizeFinePolicy(String option) {
        if (option == null) {
            return "A";
        }
        String x = option.trim().toUpperCase();
        return (x.equals("A") || x.equals("B") || x.equals("C")) ? x : "A";
    }
}

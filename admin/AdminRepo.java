package admin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * READ-only reporting repository.
 * Other members will INSERT/UPDATE into tables.
 */
public class AdminRepo {
    private final AdminDB db;

    public AdminRepo(AdminDB db) {
        this.db = db;
    }

    // ---------- DTOs ----------
    public static class ParkedRow {
        public final String plate, vehicleType, spotId, spotType, entryTime;
        public ParkedRow(String plate, String vehicleType, String spotId, String spotType, String entryTime) {
            this.plate = plate;
            this.vehicleType = vehicleType;
            this.spotId = spotId;
            this.spotType = spotType;
            this.entryTime = entryTime;
        }
    }

    public static class FloorOccRow {
        public final String floorId;
        public final int total, occupied;
        public FloorOccRow(String floorId, int total, int occupied) {
            this.floorId = floorId;
            this.total = total;
            this.occupied = occupied;
        }
        public double rate() { return total == 0 ? 0 : (occupied * 100.0 / total); }
    }

    public static class TypeOccRow {
        public final String spotType;
        public final int total, occupied;
        public TypeOccRow(String spotType, int total, int occupied) {
            this.spotType = spotType;
            this.total = total;
            this.occupied = occupied;
        }
    }

    public static class FineRow {
        public final String plate, fineType, status, createdAt;
        public final double amount;
        public FineRow(String plate, String fineType, double amount, String status, String createdAt) {
            this.plate = plate;
            this.fineType = fineType;
            this.amount = amount;
            this.status = status;
            this.createdAt = createdAt;
        }
    }

    public static class Revenue {
        public final double totalFees, totalFines;
        public Revenue(double totalFees, double totalFines) {
            this.totalFees = totalFees;
            this.totalFines = totalFines;
        }
        public double total() { return totalFees + totalFines; }
    }

    // ---------- Reports ----------

    /** Vehicles currently parked (exit_time IS NULL). */
    public List<ParkedRow> getCurrentlyParked() {
        String sql = """
            SELECT s.plate, v.vehicle_type, s.spot_id, p.spot_type, s.entry_time
            FROM parking_sessions s
            JOIN vehicles v ON v.plate = s.plate
            JOIN parking_spots p ON p.spot_id = s.spot_id
            WHERE s.exit_time IS NULL
            ORDER BY s.entry_time ASC;
        """;

        List<ParkedRow> out = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new ParkedRow(
                        rs.getString("plate"),
                        rs.getString("vehicle_type"),
                        rs.getString("spot_id"),
                        rs.getString("spot_type"),
                        rs.getString("entry_time")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[AdminRepo] getCurrentlyParked error: " + e.getMessage());
        }
        return out;
    }

    /** Occupancy by floor: counts spots + occupied spots. */
    public List<FloorOccRow> getOccupancyByFloor() {
        String sql = """
            SELECT floor_id,
                   COUNT(*) AS total_spots,
                   SUM(CASE WHEN LOWER(status)='occupied' THEN 1 ELSE 0 END) AS occupied_spots
            FROM parking_spots
            GROUP BY floor_id
            ORDER BY floor_id;
        """;

        List<FloorOccRow> out = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new FloorOccRow(
                        rs.getString("floor_id"),
                        rs.getInt("total_spots"),
                        rs.getInt("occupied_spots")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[AdminRepo] getOccupancyByFloor error: " + e.getMessage());
        }
        return out;
    }

    /** Occupancy by spot type. */
    public List<TypeOccRow> getOccupancyByType() {
        String sql = """
            SELECT spot_type,
                   COUNT(*) AS total_spots,
                   SUM(CASE WHEN LOWER(status)='occupied' THEN 1 ELSE 0 END) AS occupied_spots
            FROM parking_spots
            GROUP BY spot_type
            ORDER BY spot_type;
        """;

        List<TypeOccRow> out = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new TypeOccRow(
                        rs.getString("spot_type"),
                        rs.getInt("total_spots"),
                        rs.getInt("occupied_spots")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[AdminRepo] getOccupancyByType error: " + e.getMessage());
        }
        return out;
    }

    /** Full fine history (paid + unpaid) from fines table. */
    public List<FineRow> getUnpaidFines() {
        String sql = """
            SELECT plate, fine_type, amount, is_paid, created_at
            FROM fines
            ORDER BY created_at DESC;
        """;

        List<FineRow> out = new ArrayList<>();
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(new FineRow(
                        rs.getString("plate"),
                        rs.getString("fine_type"),
                        rs.getDouble("amount"),
                        rs.getInt("is_paid") == 1 ? "PAID" : "UNPAID",
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            System.out.println("[AdminRepo] getUnpaidFines error: " + e.getMessage());
        }
        return out;
    }

    /** Revenue: sums paid sessions (fees+fines) OR can sum payments table later if Member 3 adds it. */
    public Revenue getRevenue() {
        String sql = """
            SELECT
              SUM(CASE WHEN is_paid=1 THEN fee_amount ELSE 0 END) AS total_fees,
              SUM(CASE WHEN is_paid=1 THEN fine_amount ELSE 0 END) AS total_fines
            FROM parking_sessions;
        """;

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            double fees = rs.getDouble("total_fees");
            double fines = rs.getDouble("total_fines");
            return new Revenue(fees, fines);

        } catch (SQLException e) {
            System.out.println("[AdminRepo] getRevenue error: " + e.getMessage());
            return new Revenue(0, 0);
        }
    }

    // ---------- Admin settings ----------
    public String getFinePolicyOption() {
        String sql = "SELECT value FROM admin_settings WHERE key='fine_policy';";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getString(1) : "A";
        } catch (SQLException e) {
            return "A";
        }
    }

    public void setFinePolicyOption(String option) {
        if (option == null) return;
        String x = option.trim().toUpperCase();
        if (!(x.equals("A") || x.equals("B") || x.equals("C"))) x = "A";
        String previous = getFinePolicyOption();

        String sql = """
            INSERT INTO admin_settings(key, value) VALUES('fine_policy', ?)
            ON CONFLICT(key) DO UPDATE SET value=excluded.value;
        """;

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, x);
            ps.executeUpdate();
            System.out.println("[AdminRepo] Fine policy changed: " + previous + " -> " + x);
        } catch (SQLException e) {
            System.out.println("[AdminRepo] setFinePolicyOption error: " + e.getMessage());
        }
    }
}
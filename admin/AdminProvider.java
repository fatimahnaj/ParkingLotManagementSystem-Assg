package admin;

import java.util.List;

/**
 * Interface used by Admin module to read system data.
 * Other members will later connect their ParkingLot,
 * BillingService, and FineService to this.
 *
 * Admin does NOT calculate billing or fines.
 * Admin only displays reports.
 */
public interface AdminProvider {

    List<ParkedRow> getParkedVehicles();

    List<FloorRow> getFloorOccupancy();

    List<TypeRow> getTypeOccupancy();

    List<FineRow> getUnpaidFines();

    Revenue getRevenue();

    // ==============================
    // Simple Data Classes (DTOs)
    // ==============================

    class ParkedRow {
        public String plate;
        public String vehicleType;
        public String spotId;
        public String spotType;
        public String entryTime;

        public ParkedRow(String plate, String vehicleType,
                         String spotId, String spotType, String entryTime) {
            this.plate = plate;
            this.vehicleType = vehicleType;
            this.spotId = spotId;
            this.spotType = spotType;
            this.entryTime = entryTime;
        }
    }

    class FloorRow {
        public String floorId;
        public int total;
        public int occupied;

        public FloorRow(String floorId, int total, int occupied) {
            this.floorId = floorId;
            this.total = total;
            this.occupied = occupied;
        }

        public double getRate() {
            if (total == 0) return 0;
            return (occupied * 100.0) / total;
        }
    }

    class TypeRow {
        public String type;
        public int total;
        public int occupied;

        public TypeRow(String type, int total, int occupied) {
            this.type = type;
            this.total = total;
            this.occupied = occupied;
        }
    }

    class FineRow {
        public String plate;
        public String fineType;
        public double amount;

        public FineRow(String plate, String fineType, double amount) {
            this.plate = plate;
            this.fineType = fineType;
            this.amount = amount;
        }
    }

    class Revenue {
        public double totalFees;
        public double totalFines;

        public Revenue(double totalFees, double totalFines) {
            this.totalFees = totalFees;
            this.totalFines = totalFines;
        }

        public double getTotal() {
            return totalFees + totalFines;
        }
    }
}
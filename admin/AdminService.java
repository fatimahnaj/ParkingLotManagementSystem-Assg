package admin;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles report retrieval.
 * Does NOT calculate any billing logic.
 */
public class AdminService {

    private AdminProvider provider;

    public AdminService(AdminProvider provider) {
        this.provider = provider;
    }

    public List<AdminProvider.ParkedRow> getParkedVehicles() {
        return safe(provider.getParkedVehicles());
    }

    public List<AdminProvider.FloorRow> getFloorOccupancy() {
        return safe(provider.getFloorOccupancy());
    }

    public List<AdminProvider.TypeRow> getTypeOccupancy() {
        return safe(provider.getTypeOccupancy());
    }

    public List<AdminProvider.FineRow> getUnpaidFines() {
        return safe(provider.getUnpaidFines());
    }

    public AdminProvider.Revenue getRevenue() {
        AdminProvider.Revenue r = provider.getRevenue();
        if (r == null) return new AdminProvider.Revenue(0, 0);
        return r;
    }

    private <T> List<T> safe(List<T> list) {
        if (list == null) return new ArrayList<>();
        return list;
    }
}
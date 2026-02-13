package admin;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Admin UI Panel
 * Displays:
 * - Current parked vehicles
 * - Occupancy reports
 * - Revenue
 * - Unpaid fines
 */
public class AdminDashboard extends JPanel {

    private AdminService service;

    private DefaultTableModel parkedModel =
            new DefaultTableModel(new Object[]{"Plate", "Vehicle", "Spot", "Type", "Entry"}, 0);

    private DefaultTableModel floorModel =
            new DefaultTableModel(new Object[]{"Floor", "Total", "Occupied", "Rate %"}, 0);

    private DefaultTableModel typeModel =
            new DefaultTableModel(new Object[]{"Type", "Total", "Occupied"}, 0);

    private DefaultTableModel fineModel =
            new DefaultTableModel(new Object[]{"Plate", "Fine", "Amount (RM)"}, 0);

    private JLabel revenueLabel = new JLabel();

    public AdminDashboard(AdminProvider provider) {

        this.service = new AdminService(provider);

        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Parked", new JScrollPane(new JTable(parkedModel)));
        tabs.addTab("Occupancy", buildOccupancyPanel());
        tabs.addTab("Revenue", buildRevenuePanel());
        tabs.addTab("Fines", new JScrollPane(new JTable(fineModel)));

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());

        add(tabs, BorderLayout.CENTER);
        add(refreshBtn, BorderLayout.SOUTH);

        refresh();
    }

    private JPanel buildOccupancyPanel() {
        JPanel p = new JPanel(new GridLayout(2, 1));
        p.add(new JScrollPane(new JTable(floorModel)));
        p.add(new JScrollPane(new JTable(typeModel)));
        return p;
    }

    private JPanel buildRevenuePanel() {
        JPanel p = new JPanel(new BorderLayout());
        revenueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        p.add(revenueLabel, BorderLayout.NORTH);
        return p;
    }

    private void refresh() {

        // Parked Vehicles
        parkedModel.setRowCount(0);
        for (AdminProvider.ParkedRow r : service.getParkedVehicles()) {
            parkedModel.addRow(new Object[]{
                    r.plate, r.vehicleType, r.spotId, r.spotType, r.entryTime
            });
        }

        // Floor Occupancy
        floorModel.setRowCount(0);
        for (AdminProvider.FloorRow r : service.getFloorOccupancy()) {
            floorModel.addRow(new Object[]{
                    r.floorId, r.total, r.occupied,
                    String.format("%.2f", r.getRate())
            });
        }

        // Type Occupancy
        typeModel.setRowCount(0);
        for (AdminProvider.TypeRow r : service.getTypeOccupancy()) {
            typeModel.addRow(new Object[]{
                    r.type, r.total, r.occupied
            });
        }

        // Fines
        fineModel.setRowCount(0);
        for (AdminProvider.FineRow r : service.getUnpaidFines()) {
            fineModel.addRow(new Object[]{
                    r.plate, r.fineType, String.format("%.2f", r.amount)
            });
        }

        // Revenue
        AdminProvider.Revenue rev = service.getRevenue();
        revenueLabel.setText(String.format(
                "Fees: RM %.2f | Fines: RM %.2f | Total: RM %.2f",
                rev.totalFees, rev.totalFines, rev.getTotal()
        ));
    }
}
package admin;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import ui.MainFrame;

public class AdminDashboard extends JPanel {

    private final MainFrame frame;
    private final AdminRepo repo;

    private final DefaultTableModel parkedModel =
            new DefaultTableModel(new Object[]{"Plate", "Vehicle", "Spot", "Spot Type", "Entry"}, 0);

    private final DefaultTableModel floorModel =
            new DefaultTableModel(new Object[]{"Floor", "Total", "Occupied", "Rate %"}, 0);

    private final DefaultTableModel typeModel =
            new DefaultTableModel(new Object[]{"Type", "Total", "Occupied"}, 0);

    private final DefaultTableModel fineModel =
            new DefaultTableModel(new Object[]{"Plate", "Fine Type", "Amount (RM)"}, 0);

    private final JLabel revenueLabel = new JLabel();
    private final JComboBox<String> policyBox = new JComboBox<>(new String[]{"A", "B", "C"});

    public AdminDashboard(MainFrame frame,AdminRepo repo) {
        this.frame = frame;
        this.repo = repo;

        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Parked", new JScrollPane(new JTable(parkedModel)));
        tabs.addTab("Occupancy", buildOccupancy());
        tabs.addTab("Revenue", buildRevenue());
        tabs.addTab("Fines", new JScrollPane(new JTable(fineModel)));
        tabs.addTab("Fine Policy", buildPolicyPanel());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshAll());

        add(tabs, BorderLayout.CENTER);
        add(refreshBtn, BorderLayout.SOUTH);

        refreshAll();
    }

    private JPanel buildOccupancy() {
        JPanel p = new JPanel(new GridLayout(2, 1, 10, 10));
        p.add(new JScrollPane(new JTable(floorModel)));
        p.add(new JScrollPane(new JTable(typeModel)));
        return p;
    }

    private JPanel buildRevenue() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        revenueLabel.setFont(revenueLabel.getFont().deriveFont(Font.BOLD, 16f));
        p.add(revenueLabel, BorderLayout.NORTH);
        p.add(new JLabel("<html>Revenue is based on paid sessions stored in database.<br/>" +
                "Billing module will update fee_amount/fine_amount and is_paid.</html>"), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildPolicyPanel() {
        JPanel p = new JPanel(new GridLayout(3, 1, 8, 8));
        p.add(new JLabel("Select fine policy option (A/B/C):"));

        policyBox.setSelectedItem(repo.getFinePolicyOption());
        p.add(policyBox);

        JButton saveBtn = new JButton("Save Policy (for future billing)");
        saveBtn.addActionListener(e -> {
            repo.setFinePolicyOption((String) policyBox.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Saved fine policy: " + repo.getFinePolicyOption());
        });
        p.add(saveBtn);

        return p;
    }

    public void refreshAll() {
        // parked
        parkedModel.setRowCount(0);
        for (AdminRepo.ParkedRow r : repo.getCurrentlyParked()) {
            parkedModel.addRow(new Object[]{r.plate, r.vehicleType, r.spotId, r.spotType, r.entryTime});
        }

        // occupancy floor
        floorModel.setRowCount(0);
        for (AdminRepo.FloorOccRow r : repo.getOccupancyByFloor()) {
            floorModel.addRow(new Object[]{r.floorId, r.total, r.occupied, String.format("%.2f", r.rate())});
        }

        // occupancy type
        typeModel.setRowCount(0);
        for (AdminRepo.TypeOccRow r : repo.getOccupancyByType()) {
            typeModel.addRow(new Object[]{r.spotType, r.total, r.occupied});
        }

        // fines
        fineModel.setRowCount(0);
        for (AdminRepo.FineRow r : repo.getUnpaidFines()) {
            fineModel.addRow(new Object[]{r.plate, r.fineType, String.format("%.2f", r.amount)});
        }

        // revenue
        AdminRepo.Revenue rev = repo.getRevenue();
        revenueLabel.setText(String.format("Fees: RM %.2f | Fines: RM %.2f | Total: RM %.2f",
                rev.totalFees, rev.totalFines, rev.total()));
    }

    // quick test run
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminDB db = new AdminDB("parking.db");

            JFrame f = new JFrame("Admin Dashboard");
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(900, 600);
            f.setLocationRelativeTo(null);
            MainFrame frame = new MainFrame(); 
            f.setContentPane(new AdminDashboard(frame, new AdminRepo(db)));
            f.setVisible(true);
        });
    }
}
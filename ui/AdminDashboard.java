package ui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import admin.AdminRepo;

public class AdminDashboard extends JPanel {

    private final MainFrame frame;
    private final AdminRepo repo;
    private JTabbedPane tabs;

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

    public AdminDashboard(MainFrame frame, AdminRepo repo) {
        this.frame = frame;
        this.repo = repo;

        setLayout(new BorderLayout(10, 10));

        tabs = new JTabbedPane();
        tabs.addTab("Parked", new JScrollPane(new JTable(parkedModel)));
        tabs.addTab("Occupancy", buildOccupancy());
        tabs.addTab("Revenue", buildRevenue());
        tabs.addTab("Fines", new JScrollPane(new JTable(fineModel)));
        tabs.addTab("Fine Policy", buildPolicyPanel());

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshAll());

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            // go back to main dashboard screen
            if (frame != null) {
                resetToParkedTab(); // reset tab
                frame.resetAdminLogin(); // clear login form for next time
                frame.showScreen("SCREEN1");
            }
        });

        // same size buttons looks nicer
        Dimension btnSize = new Dimension(140, 35);
        refreshBtn.setPreferredSize(btnSize);
        logoutBtn.setPreferredSize(btnSize);

        // put logout under refresh (stacked)
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 0, 6));
        bottomPanel.add(refreshBtn);
        bottomPanel.add(logoutBtn);

        add(tabs, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

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
        
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        // label + combobox in one small row
        JPanel selectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        selectPanel.add(new JLabel("Select fine policy option (A/B/C):"));

        policyBox.setSelectedItem(repo.getFinePolicyOption());
        selectPanel.add(policyBox);

        // save button in center row
        JButton saveBtn = new JButton("Save Policy (for future billing)");
        saveBtn.addActionListener(e -> {
            repo.setFinePolicyOption((String) policyBox.getSelectedItem());
            JOptionPane.showMessageDialog(this, "Saved fine policy: " + repo.getFinePolicyOption());
        });

        JPanel savePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        savePanel.add(saveBtn);

        p.add(Box.createVerticalStrut(20));
        p.add(selectPanel);
        p.add(Box.createVerticalStrut(10));
        p.add(savePanel);
        p.add(Box.createVerticalGlue());

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

        // update dropdown too (so other admin changes can be seen after refresh)
        policyBox.setSelectedItem(repo.getFinePolicyOption());
    }

    public void resetToParkedTab() {
        if (tabs != null) tabs.setSelectedIndex(0); // Reset to first tab
    }

    // quick test run
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
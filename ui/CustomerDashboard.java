package ui;
import java.awt.*;
import javax.swing.*;
import models.vehicle.Vehicle;

class CustomerDashboard extends JPanel {

    private final MainFrame frame;
    private JPanel labelsPanel;
    private JLabel plateLabel;
    private JLabel vehicleTypeLabel;
    private Vehicle currentVehicle;

    public CustomerDashboard(MainFrame frame) {

        this.frame = frame;
        setLayout(new BorderLayout());

        plateLabel = new JLabel();
        vehicleTypeLabel = new JLabel();

        JButton parkBtn = new JButton("Park");
        parkBtn.setPreferredSize(new Dimension(150, 40));
        JButton exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(150, 40));
        //Action listeners
        parkBtn.addActionListener(e -> handlePark());
        exitBtn.addActionListener(e -> handleExit());

        labelsPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        labelsPanel.add(plateLabel);
        labelsPanel.add(vehicleTypeLabel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttonsPanel.add(parkBtn);
        buttonsPanel.add(exitBtn);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        labelsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(labelsPanel);
        centerPanel.add(Box.createVerticalStrut(16));
        centerPanel.add(buttonsPanel);
        add(centerPanel, BorderLayout.CENTER);

        refresh();
    }

    public void refresh(){
        currentVehicle = frame.getLatestVehicle();
        String plateText = currentVehicle == null ? "(none)" : currentVehicle.getPlateNum();
        String vehicleTypeText = currentVehicle == null ? "(none)" : currentVehicle.getType();
        plateLabel.setText("Plate number : " + plateText);
        vehicleTypeLabel.setText("Vehicle type : " + vehicleTypeText);
        revalidate();
        repaint();
    }

    private void handlePark(){
        currentVehicle = frame.getLatestVehicle();
        JOptionPane.showMessageDialog(this, 
            "Parking flow started for " + currentVehicle.getType() + " with plate " + currentVehicle.getPlateNum() + 
            "\n (Backend integration will assign spot)");
    }

    private void handleExit() {
        currentVehicle = frame.getLatestVehicle();
        JOptionPane.showMessageDialog(this,
            "Exit flow started for vehicle " + currentVehicle.getPlateNum() + 
            "\n(Backedn integration will release spot and add revenue)");
    }

    
}


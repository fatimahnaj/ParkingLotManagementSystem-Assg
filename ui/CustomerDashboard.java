package ui;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.swing.*;

import models.vehicle.Car;
import models.vehicle.Handicapped;
import models.vehicle.Motorcycle;
import models.vehicle.SUV;
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
        labelsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        labelsPanel.add(plateLabel);
        labelsPanel.add(vehicleTypeLabel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttonsPanel.add(parkBtn);
        buttonsPanel.add(exitBtn);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        labelsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(labelsPanel, BorderLayout.NORTH);
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

        JPanel panel = new JPanel(new GridLayout(2,2,1,1));

        //setup combo box vehicle type
        //TO BUSYRA :  setup available parking spot list kat sini. buat if else guna currentVehicle.getType
        ArrayList<String> parkingSpotLists = new ArrayList<>();
        parkingSpotLists.add("Contoh1");
        parkingSpotLists.add("Contoh2");
        parkingSpotLists.add("Contoh3");
        String[] parkingSpotArray = parkingSpotLists.toArray(new String[0]);
        JComboBox<String> parkingSpotListsCB = new JComboBox<>(parkingSpotArray);

        //setup the popup panel
        panel.add(new JLabel("Vehicle type :"));
        panel.add(parkingSpotListsCB);

        //handling the result from the popup
        int result = JOptionPane.showConfirmDialog(
                this,panel,"Parking spot selection",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            //comboBox
            String selectedChoice = (String) parkingSpotListsCB.getSelectedItem();
            for (String choice : parkingSpotLists) {
                if (selectedChoice.equals(choice)) {
                    System.out.println(choice + " selected");
                    break;
                }
            }
                //print data of the vehicle if entry is succeed
                //Entry time is counted only once customer selected parking spot
                currentVehicle.setEntryTime(LocalDateTime.now());
                frame.addVehicle(currentVehicle);
                System.out.println(currentVehicle);
                JOptionPane.showMessageDialog(panel, "Spot selection is successful. \n Please head to your parking spot : \n Nanti tims generate ticket kat sini.");
                refresh();
            }
    }


    private void handleExit() {
        currentVehicle = frame.getLatestVehicle();
        JOptionPane.showMessageDialog(this,
            "Exit flow started for vehicle " + currentVehicle.getPlateNum() + 
            "\n(Backedn integration will release spot and add revenue)");
    }

    
}


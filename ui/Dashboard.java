package ui;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.vehicle.Car;
import models.vehicle.Handicapped;
import models.vehicle.Motorcycle;
import models.vehicle.SUV;
import models.vehicle.Vehicle;

class Dashboard extends JPanel {

    private final MainFrame frame;
    private List<Vehicle> vehicles;

    public Dashboard(MainFrame frame) {

        this.frame = frame;

        setLayout(new BorderLayout());

        JButton registerBtn = new JButton("Register");
        Dimension registerBtnSize = new Dimension(150, 40);
        registerBtn.setPreferredSize(registerBtnSize);
        registerBtn.addActionListener(e-> registerPopup());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton adminBtn = new JButton("admin");
        adminBtn.addActionListener(e -> frame.showScreen("ADMINLOGIN"));
        bottomPanel.add(adminBtn, BorderLayout.EAST);

        JButton customerBtn = new JButton("Customer");
        customerBtn.addActionListener(e-> frame.showScreen("CUSTOMERSCREEN"));
        bottomPanel.add(customerBtn, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(registerBtn, new GridBagConstraints());
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    //popup utk registration vehicle
    private void registerPopup() {
        JPanel panel = new JPanel(new GridLayout(2,2,1,1));
        JTextField plateField = new JTextField(15);

        //setup combo box vehicle type
        ArrayList<String> vehicleTypes = new ArrayList<>();
        vehicleTypes.add("Motorcycle");
        vehicleTypes.add("Car");
        vehicleTypes.add("SUV");
        vehicleTypes.add("Handicapped");
        String[] vehicleTypeList = vehicleTypes.toArray(new String[0]);
        JComboBox<String> vehicleTypesCB = new JComboBox<>(vehicleTypeList);

        //setup the popup panel
        panel.add(new JLabel("Plate number :"));
        panel.add(plateField);
        panel.add(new JLabel("Vehicle type :"));
        panel.add(vehicleTypesCB);

        //handling the result from the popup
        int result = JOptionPane.showConfirmDialog(
                this,panel,"Vehicle registration",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String plate = plateField.getText().trim();
            //comboBox
            String selectedChoice = (String) vehicleTypesCB.getSelectedItem();
            for (String choice : vehicleTypes) {
                if (selectedChoice.equals(choice)) {
                    System.out.println(choice + " selected");
                    break;
                }
            }
            if (plate.isEmpty()) {
                //not successful:
                JOptionPane.showMessageDialog(panel, "Error: Please fill in plate number.");
            } else { 
                //successful :
                //check if vehicle already exist in database
                Vehicle v = frame.getStoredVehicle(plate,selectedChoice);
                v.setEntryTime(LocalDateTime.now());

                
                if (v == null) {
                    // Vehicle doesn't exist, create a new one
                    switch(selectedChoice) {
                        case "Motorcycle" -> v = new Motorcycle(plate, "Motorcycle");
                        case "Car" -> v = new Car(plate, "Car");
                        case "SUV" -> v = new SUV(plate, "SUV");
                        case "Handicapped" -> v = new Handicapped(plate, "Handicapped");
                        default -> {
                            System.err.println("Invalid vehicle type selected.");
                            v = null;
                        }
                    }
                    //Entry time is counted only once customer selected parking spot
                    v.setEntryTime(LocalDateTime.now());
                    frame.addVehicle(v);
                    frame.saveNewVehicle(v);
                }
                //print data of the vehicle if entry is succeed
                if (v != null) {
                    System.out.println(v);
                    JOptionPane.showMessageDialog(panel, "Registration successful.");
                    v.setEntryTime(LocalDateTime.now()); //set Entry time
                    frame.addVehicle(v);
                    frame.updateVehicleInDb(v); //update vehicle data (we got new entry time now)
                    frame.showScreen("SCREEN2");
                } else {
                    JOptionPane.showMessageDialog(panel, "Error: Failed to create vehicle.");
                }
                }
                
            }

            
        
    }
}

package ui;
import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

class Dashboard extends JPanel {

    private final MainFrame frame;

    public Dashboard(MainFrame frame) {

        this.frame = frame;

        setLayout(new BorderLayout());
        

        JButton registerBtn = new JButton("Register");
        Dimension registerBtnSize = new Dimension(150, 40);
        registerBtn.setPreferredSize(registerBtnSize);
        registerBtn.addActionListener(e-> registerPopup());
        //registerBtn.addActionListener(e -> frame.showScreen("SCREEN2")); //switch screen


        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(registerBtn, new GridBagConstraints());
        add(centerPanel, BorderLayout.CENTER);
    }

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
                this,panel,"Create new Seminar",
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
                JOptionPane.showMessageDialog(panel, "Registration successful.");
                frame.showScreen("SCREEN2");
            }

            
        }
    }
}


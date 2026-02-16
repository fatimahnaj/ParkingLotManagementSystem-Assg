// package ui;

// import javax.swing.*;
// import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;

// import models.models.parking.ParkingLot;
// import models.models.vehicle.Vehicle; //fatim's class gotta recheck again later

// public class CustomerScreen extends JPanel {
//     private MainFrame mainFrame;
//     private ParkingLot lot;

//     private JTextField plateField;
//     private JComboBox<String> vehicleTypeBox;
//     private JButton parkButton;
//     private JButton exitButton;
//     private JButton backButton;

//     public CustomerScreen(MainFrame mainFrame, ParkingLot lot){
//         this.mainFrame = mainFrame;
//         this.lot = lot;

//         setLayout(new GridLayout(5,2));

//         //Plate number input
//         plateField = new JTextField();
//         add(new JLabel("Plate Number: "));
//         add(plateField);

//         //Vehicle type dropdown
//         add(new JLabel("Vehicle Type: "));
//         vehicleTypeBox = new JComboBox<>(new String[] {"Car", "Motorcycle", "Bicycle"});
//         add(vehicleTypeBox);

//         //Park button
//         parkButton = new JButton("Park");
//         add(parkButton);

//         //Exit button
//         exitButton = new JButton("Exit");
//         add(exitButton);

//         //Back button to return to Dashboard
//         backButton = new JButton("Back to Dashboard");
//         add(backButton);

//         //Action listeners
//         parkButton.addActionListener(new ActionListener(){
//             public void actionPerformed(ActionEvent e){
//                 handlePark();
//             }
//         });

//         exitButton.addActionListener(new ActionListener(){
//             public void actionPerformed(ActionEvent e){
//                 handleExit();
//             }
//         });

//         backButton.addActionListener(new ActionListener() {
//             public void actionPerformed(ActionEvent e){
//                 mainFrame.showScreen("SCREEN1"); //go back to dashboard
//             }
//         });
//     }

//     private void handlePark(){
//         String plate = plateField.getText();
//         String type = (String) vehicleTypeBox.getSelectedItem();

//         Vehicle v = new Vehicle(plate, type);
//         JOptionPane.showMessageDialog(this, 
//             "Parking flow started for " + type + " with plate " + plate + 
//             "\n (Backend integration will assign spot)");
//     }

//     private void handleExit() {
//         String plate = plateField.getText();
//         JOptionPane.showMessageDialog(this,
//             "Exit flow started for vehicle " + plate + 
//             "\n(Backedn integration will release spot and add revenue)");
//     }
// }

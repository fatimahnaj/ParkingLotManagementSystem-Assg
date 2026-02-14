package ui;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.vehicle.Vehicle;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;
    private final List<Vehicle> vehicles = new ArrayList<>();

    public MainFrame() {

        setTitle("Multi Screen App");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CardLayout setup
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Create screens
        Dashboard screen1 = new Dashboard(this);
        Screen2 screen2 = new Screen2(this);
        Screen3 screen3 = new Screen3(this);

        // Add screens
        container.add(screen1, "SCREEN1");
        container.add(screen2, "SCREEN2");
        container.add(screen3, "SCREEN3");

        add(container);

        setVisible(true);
    }

    //unload data from database (if ada)
    //objects manually created by developer
    private void initData(){
        
    }

    //get the vehicle objects
    public List<Vehicle> getVehicles(){
        return vehicles;
    }

    //add a new vehicle obj into the list
    public void addVehicle(Vehicle v){
        vehicles.add(v);
    }

    // Navigation method
    public void showScreen(String name) {
        cardLayout.show(container, name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}

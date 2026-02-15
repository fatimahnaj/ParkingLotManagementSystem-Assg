package ui;
import admin.AdminDB;
import admin.AdminDashboard;
import admin.AdminRepo;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.parking.ParkingLot;
import models.vehicle.Car;
import models.vehicle.Vehicle;


//this is where we place all the objects, screens
//methods are introduced to retrieve these objects/screens

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private CustomerDashboard customerDashboard;
    private AdminDB db;

    public MainFrame() {

        setTitle("Multi Screen App");
        setSize(1000, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CardLayout setup
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        //initialise data for current run
        initData();
        //retrieve database for admin
        db = new AdminDB("parking.db");

        // Create screens
        Dashboard screen1 = new Dashboard(this);
        customerDashboard = new CustomerDashboard(this);
        Screen3 screen3 = new Screen3(this);
        CustomerScreen customerscreen = new CustomerScreen(this, new ParkingLot("MyLot"));
        AdminDashboard adminDashboard = new AdminDashboard(this, new AdminRepo(db));

        // Add screens
        container.add(screen1, "SCREEN1");
        container.add(customerDashboard, "SCREEN2");
        container.add(screen3, "SCREEN3");
        container.add(customerscreen, "CUSTOMERSCREEN");
        container.add(adminDashboard, "ADMINDASHBOARD");
        

        add(container);

        setVisible(true);
    }

    //unload data from database (if ada)
    //objects manually created by developer
    private void initData(){
        Vehicle dummyVehicle = new Car("BMX9800","Car");
        vehicles.add(dummyVehicle);
    }

    //get the vehicle objects
    public List<Vehicle> getVehicles(){
        return vehicles;
    }

    //add a new vehicle obj into the list
    public void addVehicle(Vehicle v){
        vehicles.add(v);
        if (customerDashboard != null) {
            customerDashboard.refresh();
        }
    }

    //save new vehicle to database
    public void saveNewVehicle(Vehicle v) {
        String sql = "INSERT OR IGNORE INTO vehicles VALUES (?, ?, 0)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, v.getPlateNum());
            ps.setString(2, v.getType());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //get latest created vehicle obj
    public Vehicle getLatestVehicle(){
        if (vehicles.isEmpty()) {
            return null;
        }
        int lastIndex = vehicles.size() - 1;
        return vehicles.get(lastIndex);
    }

    // Navigation method
    public void showScreen(String name) {
        cardLayout.show(container, name);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}

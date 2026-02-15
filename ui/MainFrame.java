package ui;
import admin.AdminDB;
import admin.AdminRepo;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.parking.ParkingLot;
import models.vehicle.Car;
import models.vehicle.Handicapped;
import models.vehicle.Motorcycle;
import models.vehicle.SUV;
import models.vehicle.Vehicle;


//this is where we place all the objects, screens
//methods are introduced to retrieve these objects/screens

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private CustomerDashboard customerDashboard;
    private AdminDB db;
    private AdminLogin adminLogin;

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
        adminLogin = new AdminLogin(this, new AdminRepo(db));

        // Add screens
        container.add(screen1, "SCREEN1");
        container.add(customerDashboard, "SCREEN2");
        container.add(screen3, "SCREEN3");
        container.add(customerscreen, "CUSTOMERSCREEN");
        container.add(adminDashboard, "ADMINDASHBOARD");
        container.add(adminLogin, "ADMINLOGIN");
        
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

    public Vehicle getStoredVehicle(String plate) {
        if (plate == null) {
            return null;
        }

        String trimmedPlate = plate.trim();
        if (trimmedPlate.isEmpty()) {
            return null;
        }

        String sql = "SELECT plate, vehicle_type FROM vehicles WHERE plate = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, trimmedPlate);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String plateNum = rs.getString("plate");
                    String type = rs.getString("vehicle_type");
                    return createVehicleFromType(plateNum, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Vehicle createVehicleFromType(String plateNum, String type) {
        if (type == null) {
            return new Vehicle(plateNum, "Unknown");
        }

        switch (type.trim().toLowerCase()) {
            case "motorcycle":
                return new Motorcycle(plateNum, "Motorcycle");
            case "car":
                return new Car(plateNum, "Car");
            case "suv":
                return new SUV(plateNum, "SUV");
            case "handicapped":
                return new Handicapped(plateNum, "Handicapped");
            default:
                return new Vehicle(plateNum, type);
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

    public void resetAdminLogin() {
        if (adminLogin != null) {
            adminLogin.resetForm();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}

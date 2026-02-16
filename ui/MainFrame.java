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
import models.Ticket;
import models.parking.ParkingLot;
import models.vehicle.Car;
import models.vehicle.Handicapped;
import models.vehicle.Motorcycle;
import models.vehicle.SUV;
import models.vehicle.Vehicle;
import service.billing.BillingService;
import service.billing.DefaultBillingPolicy;
import service.billing.DefaultDiscountPolicy;
import service.billing.PaymentService;
import service.fine.DefaultFinePolicy;
import service.fine.FineService;
import service.persistence.SqliteFinePolicyRepository;
import service.persistence.SqliteParkingSessionRepository;
import service.persistence.SqliteUnpaidFineRepository;
import service.persistence.UnpaidFineRepository;
import util.ParkingLotInitializer;


//this is where we place all the objects, screens
//methods are introduced to retrieve these objects/screens

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;
    private final List<Vehicle> vehicles = new ArrayList<>();
    private CustomerDashboard customerDashboard;
    private AdminDB db;
    private AdminLogin adminLogin;
    private final ParkingLot parkingLot;
    private final UnpaidFineRepository unpaidFineRepository;
    private final BillingService billingService;
    private final PaymentService paymentService;
    // added, uh delete la kalo nak nnti
    private final SqliteParkingSessionRepository parkingSessionRepository;
    private Ticket activeTicket;
    // also added
    private int activeSessionId = -1;

    public MainFrame() {

        setTitle("Parking Lot Management");
        setSize(1000, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // CardLayout setup
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        //initialise data for current run
        initData();
        parkingLot = ParkingLotInitializer.createLot();
        //retrieve database for admin
        db = new AdminDB("parking.db");
        parkingSessionRepository = new SqliteParkingSessionRepository(db);
        unpaidFineRepository = new SqliteUnpaidFineRepository(db);
        SqliteFinePolicyRepository finePolicyRepository = new SqliteFinePolicyRepository(db);
        FineService fineService = new FineService(new DefaultFinePolicy(finePolicyRepository::getFinePolicyOption));
        billingService = new BillingService(
            new DefaultBillingPolicy(),
            new DefaultDiscountPolicy(),
            fineService,
            unpaidFineRepository
        );
        paymentService = new PaymentService(unpaidFineRepository);

        // Create screens
        Dashboard dashboard = new Dashboard(this);
        customerDashboard = new CustomerDashboard(this);
        AdminDashboard adminDashboard = new AdminDashboard(this, new AdminRepo(db));
        adminLogin = new AdminLogin(this, new AdminRepo(db));

        // Add screens
        container.add(dashboard, "DASHBOARD");
        container.add(customerDashboard, "CUSTOMERDASHBOARD");
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
        loadVehiclesFromDb(); // load all vehicle from DB into vehicles arraylist
    }

    
    //======VEHICLE'S DATA HANDLING
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
        String sql = "INSERT OR IGNORE INTO vehicles(plate, vehicle_type, is_vip) VALUES (?, ?, 0)";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, v.getPlateNum());
            ps.setString(2, v.getType());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //update existing vehicle data in database
    public void updateVehicleInDb(Vehicle v) {
        if (v == null || v.getPlateNum() == null) {
            return;
        }

        String sql = "UPDATE vehicles SET vehicle_type = ?, entry_time = ?, exit_time = ? WHERE plate = ?";
        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, v.getType());

            if (v.getEntryTime() != null) {
                ps.setString(2, v.getFormattedEntryTime());
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }

            if (v.getExitTime() != null) {
                ps.setString(3, v.getFormattedExitTime());
            } else {
                ps.setNull(3, java.sql.Types.VARCHAR);
            }

            ps.setString(4, v.getPlateNum());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //retrieve vehicle from database
    public Vehicle getStoredVehicle(String plate) {
        return getStoredVehicle(plate, null);
    }

    public Vehicle getStoredVehicle(String plate, String vehicleType) {
        if (plate == null) {
            return null;
        }

        String trimmedPlate = plate.trim();
        if (trimmedPlate.isEmpty()) {
            return null;
        }

        String sql;
        if (vehicleType != null && !vehicleType.trim().isEmpty()) {
            sql = "SELECT plate, vehicle_type FROM vehicles WHERE plate = ? AND vehicle_type = ?";
        } else {
            sql = "SELECT plate, vehicle_type FROM vehicles WHERE plate = ?";
        }

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, trimmedPlate);
            if (vehicleType != null && !vehicleType.trim().isEmpty()) {
                ps.setString(2, vehicleType.trim());
            }

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

    //load all vehicle from database
    public void loadVehiclesFromDb() {
        if (db == null) {
            return;
        }

        String sql = "SELECT plate, vehicle_type FROM vehicles ORDER BY plate";
        List<Vehicle> loaded = new ArrayList<>();

        try (Connection c = db.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String plateNum = rs.getString("plate");
                String type = rs.getString("vehicle_type");
                loaded.add(createVehicleFromType(plateNum, type));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        vehicles.clear();
        vehicles.addAll(loaded);
        if (customerDashboard != null) {
            customerDashboard.refresh();
        }
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

    
    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public BillingService getBillingService() {
        return billingService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public Ticket getActiveTicket() {
        return activeTicket;
    }

    public void setActiveTicket(Ticket activeTicket) {
        this.activeTicket = activeTicket;
    }

    public void clearActiveTicket() {
        this.activeTicket = null;
    }

    public SqliteParkingSessionRepository getParkingSessionRepository() {
        return parkingSessionRepository;
    }

    public int getActiveSessionId() {
        return activeSessionId;
    }

    public void setActiveSessionId(int activeSessionId) {
        this.activeSessionId = activeSessionId;
    }

    public void clearActiveSessionId() {
        this.activeSessionId = -1;
    }
 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}

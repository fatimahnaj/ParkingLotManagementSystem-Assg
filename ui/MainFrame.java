package ui;
import admin.AdminDB;
import admin.AdminDashboard;
import admin.AdminRepo;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import models.Ticket;
import models.parking.ParkingLot;
import models.vehicle.Car;
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

        setTitle("Multi Screen App");
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
        AdminDB db = new AdminDB("parking.db");
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
        Dashboard screen1 = new Dashboard(this);
        customerDashboard = new CustomerDashboard(this);
        Screen3 screen3 = new Screen3(this);
        CustomerScreen customerscreen = new CustomerScreen(this, parkingLot);
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
 
    //dupliated 
    
    // public void addVehicle(Vehicle vehicle) {
    //     System.out.println("Vehicle added: " + vehicle);
    // }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }
}

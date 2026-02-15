package ui;
import java.awt.*;
import java.time.LocalDateTime;

import javax.swing.*;

import dto.billing.BillingBreakdownDto;
import models.Ticket;
import models.parking.ParkingSpot;
import models.parking.SpotType;
import models.vehicle.Vehicle;
import service.persistence.SqliteParkingSessionRepository;

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


        // To test the skip-fines flow
        JButton simulateBtn = new JButton("Simulate Fine");
        simulateBtn.setPreferredSize(new Dimension(150, 40));

        //Action listeners
        parkBtn.addActionListener(e -> handlePark());
        exitBtn.addActionListener(e -> handleExit());
        simulateBtn.addActionListener(e -> handleSimulateFine());

        labelsPanel = new JPanel(new GridLayout(2, 1, 0, 8));
        labelsPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        labelsPanel.add(plateLabel);
        labelsPanel.add(vehicleTypeLabel);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        buttonsPanel.add(parkBtn);
        buttonsPanel.add(exitBtn);
        buttonsPanel.add(simulateBtn);

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
        if (currentVehicle == null) {
            JOptionPane.showMessageDialog(this,
                "No vehicle is registered yet. Please register first from the main dashboard.");
            return;
        }

        SpotType preferredType = resolveSpotTypeForVehicle(currentVehicle);
        java.util.List<ParkingSpot> availableSpots = frame.getParkingLot().getAvailableSpotsByType(preferredType);
        if (availableSpots.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "No available " + preferredType + " spot for this vehicle."
            );
            return;
        }

        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        JComboBox<String> parkingSpotListsCB = new JComboBox<>();
        for (ParkingSpot spot : availableSpots) {
            parkingSpotListsCB.addItem(spot.getSpotID());
        }
        panel.add(new JLabel("Suggested spot type :"));
        panel.add(new JLabel(preferredType.toString()));
        panel.add(new JLabel("Choose parking spot :"));
        panel.add(parkingSpotListsCB);

        int result = JOptionPane.showConfirmDialog(
            this, panel, "Parking spot selection",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        int selectedIndex = parkingSpotListsCB.getSelectedIndex();
        if (selectedIndex < 0 || selectedIndex >= availableSpots.size()) {
            JOptionPane.showMessageDialog(this, "Invalid parking spot selected.");
            return;
        }

        ParkingSpot selectedSpot = availableSpots.get(selectedIndex);
        if (!selectedSpot.assignVehicle(currentVehicle)) {
            JOptionPane.showMessageDialog(this, "Failed to assign parking spot. Please try again.");
            return;
        }

        LocalDateTime entryTime = LocalDateTime.now();
        currentVehicle.setEntryTime(entryTime);
        Ticket ticket = new Ticket(currentVehicle, selectedSpot);
        frame.setActiveTicket(ticket);
        int sessionId = frame.getParkingSessionRepository().startSession(ticket, entryTime);
        frame.setActiveSessionId(sessionId);

        JOptionPane.showMessageDialog(
            this,
            "Parking successful at " + selectedSpot.getSpotID() + "."
        );
        refresh();
    }


    private void handleExit() {
        currentVehicle = frame.getLatestVehicle();
        if (currentVehicle == null) {
            JOptionPane.showMessageDialog(this,
                "No active vehicle found. Please register and park a vehicle first.");
            return;
        }

        Ticket activeTicket = resolveActiveTicketForVehicle(currentVehicle);
        if (activeTicket == null) {
            JOptionPane.showMessageDialog(this,
                "No active parking session found for vehicle " + currentVehicle.getPlateNum() + ".");
            return;
        }

        syncEntryTimeFromDb(currentVehicle);

        LocalDateTime checkoutTime = LocalDateTime.now();
        currentVehicle.setExitTime(checkoutTime);
        BillingBreakdownDto bill = frame.getBillingService().calculatePayable(activeTicket, checkoutTime);

        boolean hasOutstandingFine = bill.getOutstandingFineTotal() > 0.0;
        boolean payFinesNow = true;

        if (hasOutstandingFine) {
            Object[] options = {"Pay now", "Skip fines", "Cancel"};
            int choice = JOptionPane.showOptionDialog(
                this,
                "Outstanding fines detected for " + currentVehicle.getPlateNum() + ".\n"
                    + "Pay fines now or carry them forward to next visit?",
                "Outstanding Fine",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
            );
            if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) {
                return;
            }
            payFinesNow = choice == 0;
        }

        double payableNow = payFinesNow ? bill.getPayableTotal() : bill.getNetParkingCharge();
        String receiptText = buildReceiptText(currentVehicle, activeTicket, bill, payFinesNow, payableNow);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            receiptText + "\n\nClick YES to make payment now.",
            "Payment Receipt",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE
        );
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (payFinesNow) {
            frame.getPaymentService().confirmPayment(activeTicket, bill, frame.getParkingLot());
        } else {
            frame.getPaymentService().confirmBasePaymentCarryFines(activeTicket, bill, frame.getParkingLot());
        }

        int sessionId = frame.getActiveSessionId();
        if (sessionId <= 0) {
            SqliteParkingSessionRepository.ActiveSessionRecord fromDb =
                frame.getParkingSessionRepository().findActiveSessionByPlate(currentVehicle.getPlateNum());
            if (fromDb != null) {
                sessionId = fromDb.getSessionId();
            }
        }
        if (sessionId > 0) {
            double finePaid = payFinesNow ? bill.getOutstandingFineTotal() : 0.0;
            frame.getParkingSessionRepository().completeSession(
                sessionId,
                checkoutTime,
                bill.getNetParkingCharge(),
                finePaid,
                true
            );
        }
        frame.getParkingSessionRepository().updateSpotStatus(activeTicket.getParkingSpot().getSpotID(), "free");

        activeTicket.getParkingSpot().releaseSpot();
        frame.clearActiveTicket();
        frame.clearActiveSessionId();
        JOptionPane.showMessageDialog(
            this,
            "Payment successful.\nVehicle " + currentVehicle.getPlateNum() + " has exited."
        );
        refresh();
        frame.showScreen("SCREEN1");
    }

    // To test the skip-fines flow (Simulator)

    private void handleSimulateFine() {
        currentVehicle = frame.getLatestVehicle();
        if (currentVehicle == null) {
            JOptionPane.showMessageDialog(this,
                "No vehicle found. Register and park a vehicle first.");
            return;
        }

        String plate = currentVehicle.getPlateNum();
        Object[] options = {"Add unpaid fine RM80", "Backdate entry 26h", "Both", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Simulation helper for testing skip-fines flow.\nChoose one action for plate " + plate + ".",
            "Simulate Fine/Overstay",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == 3 || choice == JOptionPane.CLOSED_OPTION) {
            return;
        }

        boolean addUnpaid = choice == 0 || choice == 2;
        boolean backdate = choice == 1 || choice == 2;
        StringBuilder result = new StringBuilder("Simulation applied:\n");

        if (addUnpaid) {
            frame.getPaymentService().addManualUnpaidFine(plate, "SIM_TEST_FINE", 80.0);
            result.append("- Added unpaid fine RM80\n");
        }

        if (backdate) {
            boolean updated = frame.getParkingSessionRepository().backdateActiveSessionEntry(plate, 26);
            if (updated) {
                currentVehicle.setEntryTime(LocalDateTime.now().minusHours(26));
                result.append("- Backdated active entry by 26 hours\n");
            } else {
                result.append("- Could not backdate entry (no active DB session)\n");
            }
        }

        JOptionPane.showMessageDialog(this, result.toString());
    }

    // end of simulation helper

    private SpotType resolveSpotTypeForVehicle(Vehicle vehicle) {
        String type = vehicle.getType() == null ? "" : vehicle.getType().trim().toLowerCase();
        switch (type) {
            case "motorcycle":
                return SpotType.COMPACT;
            case "handicapped":
                return SpotType.HANDICAPPED;
            case "car":
            case "suv":
            default:
                return SpotType.REGULAR;
        }
    }

    private Ticket resolveActiveTicketForVehicle(Vehicle vehicle) {
        Ticket activeTicket = frame.getActiveTicket();
        if (activeTicket != null) {
            String ticketPlate = activeTicket.getVehicle().getPlateNum();
            if (ticketPlate != null && ticketPlate.equals(vehicle.getPlateNum())) {
                return activeTicket;
            }
        }

        SqliteParkingSessionRepository.ActiveSessionRecord fromDb =
            frame.getParkingSessionRepository().findActiveSessionByPlate(vehicle.getPlateNum());
        if (fromDb != null) {
            frame.setActiveSessionId(fromDb.getSessionId());
            vehicle.setEntryTime(fromDb.getEntryTime());
        }

        ParkingSpot occupiedSpot = frame.getParkingLot().findSpotByPlate(vehicle.getPlateNum());
        if (occupiedSpot == null && fromDb != null) {
            occupiedSpot = findSpotById(fromDb.getSpotId());
            if (occupiedSpot != null && !occupiedSpot.isOccupied()) {
                occupiedSpot.assignVehicle(vehicle);
            }
        }
        if (occupiedSpot == null) {
            return null;
        }

        Ticket recoveredTicket = new Ticket(vehicle, occupiedSpot);
        frame.setActiveTicket(recoveredTicket);
        return recoveredTicket;
    }

    private void syncEntryTimeFromDb(Vehicle vehicle) {
        SqliteParkingSessionRepository.ActiveSessionRecord fromDb =
            frame.getParkingSessionRepository().findActiveSessionByPlate(vehicle.getPlateNum());
        if (fromDb != null) {
            vehicle.setEntryTime(fromDb.getEntryTime());
            frame.setActiveSessionId(fromDb.getSessionId());
        }
    }

    private ParkingSpot findSpotById(String spotId) {
        for (models.parking.Floor floor : frame.getParkingLot().getFloors()) {
            for (models.parking.Row row : floor.getRows()) {
                for (ParkingSpot spot : row.getSpots()) {
                    if (spot.getSpotID().equals(spotId)) {
                        return spot;
                    }
                }
            }
        }
        return null;
    }

    private String buildReceiptText(
        Vehicle vehicle,
        Ticket ticket,
        BillingBreakdownDto bill,
        boolean payFinesNow,
        double payableNow
    ) {
        StringBuilder builder = new StringBuilder();
        builder.append("===== EXIT RECEIPT =====\n");
        builder.append("Plate Number        : ").append(vehicle.getPlateNum()).append("\n");
        builder.append("Spot ID             : ").append(ticket.getParkingSpot().getSpotID()).append("\n");
        builder.append("Parked Minutes      : ").append(bill.getParkedMinutes()).append(" min\n");
        builder.append("Billable Hours      : ").append(bill.getBillableHours()).append(" hour(s)\n");
        builder.append("Parking Fee         : RM ")
            .append(String.format("%.2f", bill.getNetParkingCharge())).append("\n");
        builder.append("Current Fine        : RM ")
            .append(String.format("%.2f", bill.getCurrentSessionFineTotal())).append("\n");
        builder.append("Previous Unpaid     : RM ")
            .append(String.format("%.2f", bill.getUnpaidPreviousFines())).append("\n");
        if (!payFinesNow) {
            builder.append("Fine Action         : Skip fines (carry forward)\n");
            builder.append("Fines Carried       : RM ")
                .append(String.format("%.2f", bill.getOutstandingFineTotal())).append("\n");
        } else {
            builder.append("Fine Action         : Pay now\n");
        }
        builder.append("------------------------------\n");
        builder.append("TOTAL PAY NOW       : RM ")
            .append(String.format("%.2f", payableNow)).append("\n");
        return builder.toString();
    }
 
    
}


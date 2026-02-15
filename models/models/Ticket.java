package models;

import java.time.LocalDateTime;
import java.util.UUID;
import models.parking.ParkingSpot;
import models.vehicle.Vehicle;

public class Ticket {
    private String ticketID; //ni guna bile eh
    private Vehicle vehicle;
    private ParkingSpot parkingSpot;
    private boolean isPaid;
    private boolean reservationPermit;
    private final LocalDateTime createdAt;

    //ticket will be generated once vehicle have selected parking spot
    public Ticket(Vehicle vehicle, ParkingSpot parkingSpot){
        this.ticketID = UUID.randomUUID().toString();
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
        this.isPaid = false;
        this.reservationPermit = false;
        this.createdAt = LocalDateTime.now();
    }

    //ticket will be mark as paid after customer has paid
    public void markPaid(){
        isPaid = true;
    }

    public void generateTicket(){
        System.out.println("Ticket generated! : " + parkingSpot+"-"+vehicle.getPlateNum()+"-"+vehicle.getEntryTime());
    }

    public String getTicketID() {
        return ticketID;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public boolean hasReservationPermit() {
        return reservationPermit;
    }

    public void setReservationPermit(boolean reservationPermit) {
        this.reservationPermit = reservationPermit;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
        
}

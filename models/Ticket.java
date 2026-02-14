package models;

import models.parking.ParkingSpot;
import models.vehicle.Vehicle;

public class Ticket {
    private String ticketID; //ni guna bile eh
    private Vehicle vehicle;
    private ParkingSpot parkingSpot;
    private boolean isPaid; 

    //ticket will be generated once vehicle have selected parking spot
    public Ticket(Vehicle vehicle, ParkingSpot parkingSpot){
        this.vehicle = vehicle;
        this.parkingSpot = parkingSpot;
    }

    //ticket will be mark as paid after customer has paid
    public void markPaid(){
        isPaid = true;
    }

    public void generateTicket(){
        System.out.println("Ticket generated! : " + parkingSpot+"-"+vehicle.getPlateNum()+"-"+vehicle.getEntryTime());
    }
        
}

package models.parking;
import models.vehicle.Vehicle;


public class ParkingSpot {
    private String spotID;
    private SpotType type;
    private boolean occupied;
    private Vehicle currentVehicle; //refer to fatim's part for vehicle

    public ParkingSpot(String spotID, SpotType type){
        this.spotID = spotID;
        this.type = type;
        this.occupied = false;
        this.currentVehicle = null;
    }

    // public boolean canFitVehicle(Vehicle vehicle){
    //     return vehicle.canParkIn(type);
    // }

    public boolean assignVehicle(Vehicle vehicle){
        if(!occupied){
            this.currentVehicle = vehicle;
            this.occupied = true;
            return true;
        } return false;
    }

    public void releaseSpot(){
        this.currentVehicle = null;
        this.occupied = false;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public SpotType getType() { 
        return type;
    }

    public String getSpotID() {
        return spotID;
    }
    
}

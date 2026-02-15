package ui;


public class ParkingSpot {
    private String spotID;
    private SpotType type;
    private boolean occupied;
    private String plateNum;
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
            this.plateNum = vehicle.getPlateNum();
            this.occupied = true;
            return true;
        } return false;
    }

    public void releaseSpot(){
        this.currentVehicle = null;
        this.plateNum = null;
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

    public Vehicle getCurrentVehicle(){
        return currentVehicle;
    }
    
    public String getPlateNum() {
        return plateNum;
    }

    public boolean isReservedSpot() {
        return type == SpotType.RESERVED;
    }

    public boolean isHandicappedSpot() {
        return type == SpotType.HANDICAPPED;
    }

    @Override
    public String toString() {
        return spotID + " [" + type + "] - " +
                (occupied ? "Occupied by " + getPlateNum() : "Free");
    }
}

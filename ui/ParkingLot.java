package ui;


import java.util.ArrayList;
import java.util.List;

public class ParkingLot {
    private String lotName;
    private List<Floor> floors;
    private double totalRevenue;

    public ParkingLot(String lotName){
        this.lotName = lotName;
        this.floors = new ArrayList<>();
        this.totalRevenue = 0.0;
    }

    public void addFloor(Floor floor){
        floors.add(floor);
    }

    public void addRevenue(double amount){
        totalRevenue += amount;
    }

    public double calculateTotalRevenue(){
        return totalRevenue;
    }

    public List<Floor> getFloors(){
        return floors;
    }

    public String getLotName(){
        return lotName;
    }

    //find the first free spot of a given type
    public ParkingSpot findAvailableSpotVehicle(SpotType type){
        for (Floor floor : floors) {
            for (Row row : floor.getRows()){
                for (ParkingSpot spot : row.getSpots()){
                    if (!spot.isOccupied() && spot.getType() == type){
                        return spot; //first free spot of this type
                    }
                } 
            }
        }
        return null; //none found
    }

    //get all free spots of a given type
    public List<ParkingSpot> getAvailableSpotsByType(SpotType type){
        List<ParkingSpot> available = new ArrayList<>();
        for (Floor floor : floors){
            for (Row row : floor.getRows()){
                for (ParkingSpot spot : row.getSpots()){
                    if (!spot.isOccupied() && spot.getType() == type){
                        available.add(spot);
                    }
                }
            }
        }
        return available;
    }

    //print occupancy report for each floor
    public void printOccupancyReport() {
        for (Floor floor : floors){
            System.out.println("Floor " + floor.getFloorID() +
                "Occupancy: " + floor.calculateOccupancyRate() + "%");
        }
    }

    //find a spot by a plate number (untuk exit)
    public ParkingSpot findSpotByPlate(String plateNum){
        for (Floor floor: floors) {
            for (Row row : floor.getRows()) {
                for (ParkingSpot spot : row.getSpots()) {
                    if (spot.isOccupied() && plateNum.equals(spot.getPlateNum())){
                        return spot;
                    }
                }
            }
        }
        return null;
    }  
}






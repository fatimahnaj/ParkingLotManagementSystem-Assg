package models.parking;

import java.util.List;
import java.util.ArrayList;

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
    

   
}

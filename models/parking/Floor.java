package models.parking;
import java.util.List;

import java.util.ArrayList;

public class Floor {
    private String floorID;
    private List<Row> rows;

    public Floor(String floorID){
        this.floorID = floorID;
        this.rows = new ArrayList<>();
    }

    public void addRow(Row row){
        rows.add(row);
    }

    
    public double calculateOccupancyRate() {
        int totalSpots = 0;
        int occupiedSpots = 0;
        for (Row r : rows) {
            totalSpots += r.getSpots().size();
            occupiedSpots += r.countOccupiedSpots();
        }
        return totalSpots == 0?0 : (double) occupiedSpots / totalSpots * 100;
    }

    public List<Row> getRows(){
        return rows;
    }

    public String getFloorID() {
        return floorID;
    }
}

package ui;

import java.util.List;
import java.util.ArrayList;

public class Row {
    private String rowID;
    private List<ParkingSpot> spots;

    public Row(String rowID) {
        this.rowID = rowID;
        this.spots = new ArrayList<>();
    }

    public void addSpot(ParkingSpot spot) {
        spots.add(spot);
    }

    public int countOccupiedSpots() {
        int count = 0;
        for (ParkingSpot s : spots){
            if (s.isOccupied()) count++;
        }
        return count;
    }

    public List<ParkingSpot> getSpots() {
        return spots;
    }

    public String getRowID(){
        return rowID;
    }
}
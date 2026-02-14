package util;

import models.parking.ParkingLot;
import models.parking.Floor;
import models.parking.Row;
import models.parking.ParkingSpot;
import models.parking.SpotType;


public class ParkingLotInitializer {
    public static ParkingLot createLot() {
        ParkingLot lot = new ParkingLot("University Lot");

        for (int f = 1; f <= 3; f++)  { //3 floors
            Floor floor = new Floor("F" + f);
            
            //Row 1:reserved, regular, compact
            Row row1 = new Row ("R1");
            row1.addSpot(new ParkingSpot("F" + f + "-R1-S1", SpotType.RESERVED));
            row1.addSpot(new ParkingSpot("F" + f + "-R1-S2", SpotType.REGULAR));
            row1.addSpot(new ParkingSpot("F" + f + "-R1-S3", SpotType.COMPACT));

            //Row 2: handicapped, regular, compact
            Row row2 = new Row("R2");
            row2.addSpot(new ParkingSpot("F" + f + "-R2-S1", SpotType.HANDICAPPED));
            row2.addSpot(new ParkingSpot("F" + f + "-R2-S2", SpotType.REGULAR));
            row2.addSpot(new ParkingSpot("F" + f + "-R2-S3", SpotType.COMPACT));

            //Row 3: reserved, regular, compact
            Row row3 = new Row("R3");
            row3.addSpot(new ParkingSpot("F" + f + "-R3-S1", SpotType.RESERVED));
            row3.addSpot(new ParkingSpot("F" + f + "-R3-S2", SpotType.REGULAR));
            row3.addSpot(new ParkingSpot("F" + f + "-R3-S3", SpotType.COMPACT));

            //Add rows to floor
            floor.addRow(row1);
            floor.addRow(row2);
            floor.addRow(row3);

            //Add floor to lot
            lot.addFloor(floor);
        }
        return lot;
    }
}

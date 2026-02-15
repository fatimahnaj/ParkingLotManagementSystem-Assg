// package test;

// // Test the parking lot (Untuk tunjuk during interview)

// import models.parking.ParkingLot;
// import models.parking.Floor;
// import models.parking.Row;
// import models.vehicle.Vehicle;
// import models.parking.ParkingSpot;
// import util.ParkingLotInitializer;

// public class TestParking {
//     public static void main(String[] args) {
//         //Create the parking lot with 3 floors, each with fixed 3x3 layout
//         ParkingLot lot = ParkingLotInitializer.createLot();

//         //Show initial occupancy for Floor 1
//         System.out.println("Initial Occupancy Floor 1: " +
//             lot.getFloors().get(0).calculateOccupancyRate()+ "%");

//         //Pick a spot (Floor 1, Row 1, Spot 1) and assign a dummy vehicle
//         ParkingSpot spot = lot.getFloors().get(0).getRows().get(0).getSpots().get(0);
//         boolean assigned = spot.assignVehicle(new Vehicle(null, null));

//         if (assigned){
//             System.out.println("Vehicle parked at " + spot.getSpotID());
//         } else {
//             System.out.println("Failed to park at " + spot.getSpotID());
//         }

//         //Show occupancy after parking
//         System.out.println("After Parking Occupancy Floor 1: " +
//             lot.getFloors().get(0).calculateOccupancyRate() + "%");

//         //Release the spot
//         spot.releaseSpot();
//         System.out.println("After Release Occupancy Floor 1: " +
//             lot.getFloors().get(0).calculateOccupancyRate() + "%");

//         //Add revenue manually (simulate payment)
//         lot.addRevenue(10.0);
//         System.out.println("Total Revenue: RM " + lot.calculateTotalRevenue());
//     }
// }

// package admin;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;

// /**
//  * Connects Admin module to actual system data.
//  * This class only reads data.
//  */
// public class AdminData implements AdminProvider {

//     private model.parking.ParkingLot parkingLot;          // busyra (Parking structure)
//     private service.billing.BillingService billingService; // umu (Billing / revenue)
//     private service.fine.FineService fineService;          // umu (Fines)

//     public AdminData(model.parking.ParkingLot parkingLot,
//                      service.billing.BillingService billingService,
//                      service.fine.FineService fineService) {

//         this.parkingLot = parkingLot;
//         this.billingService = billingService;
//         this.fineService = fineService;
//     }

//     @Override
//     public List<ParkedRow> getParkedVehicles() {

//         List<ParkedRow> list = new ArrayList<>();

//         if (parkingLot == null) return list; // busyra

//         for (model.parking.Floor f : parkingLot.getFloors()) {      // busyra
//             for (model.parking.Row r : f.getRows()) {              // busyra
//                 for (model.parking.ParkingSpot s : r.getSpots()) { // busyra

//                     if (!"occupied".equalsIgnoreCase(s.getStatus())) // busyra
//                         continue;

//                     model.vehicle.Vehicle v = s.getCurrentVehicle(); // busyra
//                     if (v == null) continue;

//                     list.add(new ParkedRow(
//                             v.getPlateNum(),              // fatim (Vehicle)
//                             v.getType(),                  // fatim
//                             s.getSpotID(),                // busyra
//                             s.getType(),                  // busyra
//                             v.getEntryTime().toString()   // fatim
//                     ));
//                 }
//             }
//         }

//         return list;
//     }

//     @Override
//     public List<FloorRow> getFloorOccupancy() {

//         List<FloorRow> list = new ArrayList<>();

//         if (parkingLot == null) return list; // busyra

//         for (model.parking.Floor f : parkingLot.getFloors()) { // busyra

//             int total = 0;
//             int occupied = 0;

//             for (model.parking.Row r : f.getRows()) {          // busyra
//                 for (model.parking.ParkingSpot s : r.getSpots()) { // busyra
//                     total++;

//                     if ("occupied".equalsIgnoreCase(s.getStatus())) // busyra
//                         occupied++;
//                 }
//             }

//             list.add(new FloorRow(
//                     f.getFloorID(), // busyra
//                     total,
//                     occupied
//             ));
//         }

//         return list;
//     }

//     @Override
//     public List<TypeRow> getTypeOccupancy() {

//         List<TypeRow> list = new ArrayList<>();

//         if (parkingLot == null) return list; // busyra

//         Map<String, int[]> map = new java.util.HashMap<>();

//         for (model.parking.Floor f : parkingLot.getFloors()) { // busyra
//             for (model.parking.Row r : f.getRows()) {          // busyra
//                 for (model.parking.ParkingSpot s : r.getSpots()) { // busyra

//                     String type = s.getType(); // busyra

//                     int[] data = map.getOrDefault(type, new int[]{0, 0});
//                     data[0]++;

//                     if ("occupied".equalsIgnoreCase(s.getStatus())) // busyra
//                         data[1]++;

//                     map.put(type, data);
//                 }
//             }
//         }

//         for (String type : map.keySet()) {
//             int[] d = map.get(type);
//             list.add(new TypeRow(type, d[0], d[1]));
//         }

//         return list;
//     }

//     @Override
//     public List<FineRow> getUnpaidFines() {

//         List<FineRow> list = new ArrayList<>();

//         if (fineService == null) return list; // umu

//         Map<String, List<model.fine.Fine>> fines =
//                 fineService.getUnpaidFinesByPlate(); // umu

//         if (fines == null) return list;

//         for (String plate : fines.keySet()) {
//             for (model.fine.Fine fine : fines.get(plate)) {

//                 if (!fine.isPaid()) { // umu
//                     list.add(new FineRow(
//                             plate,
//                             fine.getFineType(), // umu
//                             fine.getAmount()    // umu
//                     ));
//                 }
//             }
//         }

//         return list;
//     }

//     @Override
//     public Revenue getRevenue() {

//         if (billingService == null)
//             return new Revenue(0, 0); // umu

//         double fees = billingService.getTotalParkingFees();   // umu
//         double fines = billingService.getTotalFinesCollected(); // umu

//         return new Revenue(fees, fines);
//     }
// }
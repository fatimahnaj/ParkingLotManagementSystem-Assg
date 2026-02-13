// package admin;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Map;

/**
 * Connects Admin module to actual system data.
 * Other members will help complete TODO parts.
 *
 * This class only reads data.
 */
// public class AdminData implements AdminProvider {

    // private model.parking.ParkingLot parkingLot;
    // private service.billing.BillingService billingService;
    // private service.fine.FineService fineService;

    // public AdminData(model.parking.ParkingLot parkingLot,
    //                  service.billing.BillingService billingService,
    //                  service.fine.FineService fineService) {

    //     this.parkingLot = parkingLot;
    //     this.billingService = billingService;
    //     this.fineService = fineService;
    // }

    // @Override
    // public List<ParkedRow> getParkedVehicles() {

    //     List<ParkedRow> list = new ArrayList<>();

    //     if (parkingLot == null) return list;

    //     for (model.parking.Floor f : parkingLot.getFloors()) {
    //         for (model.parking.Row r : f.getRows()) {
    //             for (model.parking.ParkingSpot s : r.getSpots()) {

    //                 if (!"occupied".equalsIgnoreCase(s.getStatus()))
    //                     continue;

    //                 model.vehicle.Vehicle v = s.getCurrentVehicle();
    //                 if (v == null) continue;

    //                 list.add(new ParkedRow(
    //                         v.getPlateNum(),
    //                         v.getType(),
    //                         s.getSpotID(),
    //                         s.getType(),
    //                         v.getEntryTime().toString()
    //                 ));
    //             }
    //         }
    //     }

    //     return list;
    // }

    // @Override
    // public List<FloorRow> getFloorOccupancy() {

    //     List<FloorRow> list = new ArrayList<>();

    //     if (parkingLot == null) return list;

    //     for (model.parking.Floor f : parkingLot.getFloors()) {

    //         int total = 0;
    //         int occupied = 0;

    //         for (model.parking.Row r : f.getRows()) {
    //             for (model.parking.ParkingSpot s : r.getSpots()) {
    //                 total++;
    //                 if ("occupied".equalsIgnoreCase(s.getStatus()))
    //                     occupied++;
    //             }
    //         }

    //         list.add(new FloorRow(f.getFloorID(), total, occupied));
    //     }

    //     return list;
    // }

    // @Override
    // public List<TypeRow> getTypeOccupancy() {

    //     List<TypeRow> list = new ArrayList<>();

    //     if (parkingLot == null) return list;

    //     Map<String, int[]> map = new java.util.HashMap<>();

    //     for (model.parking.Floor f : parkingLot.getFloors()) {
    //         for (model.parking.Row r : f.getRows()) {
    //             for (model.parking.ParkingSpot s : r.getSpots()) {

    //                 String type = s.getType();
    //                 int[] data = map.getOrDefault(type, new int[]{0, 0});
    //                 data[0]++;
    //                 if ("occupied".equalsIgnoreCase(s.getStatus()))
    //                     data[1]++;
    //                 map.put(type, data);
    //             }
    //         }
    //     }

    //     for (String type : map.keySet()) {
    //         int[] d = map.get(type);
    //         list.add(new TypeRow(type, d[0], d[1]));
    //     }

    //     return list;
    // }

    // @Override
    // public List<FineRow> getUnpaidFines() {

    //     List<FineRow> list = new ArrayList<>();

    //     if (fineService == null) return list;

    //     Map<String, List<model.fine.Fine>> fines =
    //             fineService.getUnpaidFinesByPlate();

    //     if (fines == null) return list;

    //     for (String plate : fines.keySet()) {
    //         for (model.fine.Fine fine : fines.get(plate)) {
    //             if (!fine.isPaid()) {
    //                 list.add(new FineRow(
    //                         plate,
    //                         fine.getFineType(),
    //                         fine.getAmount()
    //                 ));
    //             }
    //         }
    //     }

    //     return list;
    // }

    // @Override
    // public Revenue getRevenue() {

    //     if (billingService == null)
    //         return new Revenue(0, 0);

    //     double fees = billingService.getTotalParkingFees();
    //     double fines = billingService.getTotalFinesCollected();

    //     return new Revenue(fees, fines);
    // }
// }
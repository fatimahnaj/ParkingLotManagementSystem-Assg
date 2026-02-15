package test;

import java.time.LocalDateTime;

import dto.billing.BillingBreakdownDto;
import models.Ticket;
import models.parking.ParkingLot;
import models.parking.ParkingSpot;
import models.parking.SpotType;
import models.vehicle.Vehicle;
import service.billing.BillingService;
import service.billing.DefaultBillingPolicy;
import service.billing.DefaultDiscountPolicy;
import service.billing.PaymentService;
import service.fine.DefaultFinePolicy;
import service.fine.FineService;
import service.persistence.InMemoryUnpaidFineRepository;
import service.persistence.UnpaidFineRepository;
import util.ParkingLotInitializer;

public class BillingEngineDemo {
    public static void main(String[] args) {
        ParkingLot lot = ParkingLotInitializer.createLot();

        UnpaidFineRepository unpaidRepo = new InMemoryUnpaidFineRepository();
        FineService fineService = new FineService(new DefaultFinePolicy());
        BillingService billingService = new BillingService(
            new DefaultBillingPolicy(),
            new DefaultDiscountPolicy(),
            fineService,
            unpaidRepo
        );
        PaymentService paymentService = new PaymentService(unpaidRepo);

        runScenario(lot, billingService, paymentService, "A101", SpotType.REGULAR, 30, false, false, true);
        runScenario(lot, billingService, paymentService, "H111", SpotType.HANDICAPPED, 90, true, false, true);
        runScenario(lot, billingService, paymentService, "H222", SpotType.REGULAR, 90, true, false, true);

        runScenario(lot, billingService, paymentService, "R999", SpotType.RESERVED, 45, false, false, false);
        runScenario(lot, billingService, paymentService, "R999", SpotType.REGULAR, 30, false, false, true);

        runScenario(lot, billingService, paymentService, "O777", SpotType.REGULAR, (24 * 60) + 20, false, false, true);

        System.out.println("Total Revenue Collected: RM " + String.format("%.2f", lot.calculateTotalRevenue()));
    }

    private static void runScenario(
        ParkingLot lot,
        BillingService billingService,
        PaymentService paymentService,
        String plateNum,
        SpotType spotType,
        long parkedMinutes,
        boolean handicappedCardHolder,
        boolean reservationPermit,
        boolean payNow
    ) {
        ParkingSpot spot = lot.getAvailableSpotsByType(spotType).get(0);
        Vehicle vehicle = new Vehicle(plateNum, "DemoVehicle");
        vehicle.setHandicappedCardHolder(handicappedCardHolder);

        LocalDateTime checkoutTime = LocalDateTime.now();
        vehicle.setEntryTime(checkoutTime.minusMinutes(parkedMinutes));
        vehicle.setExitTime(checkoutTime);

        spot.assignVehicle(vehicle);

        Ticket ticket = new Ticket(vehicle, spot);
        ticket.setReservationPermit(reservationPermit);

        BillingBreakdownDto bill = billingService.calculatePayable(ticket, checkoutTime);

        System.out.println("\nScenario: " + plateNum + " | Spot=" + spotType + " | Minutes=" + parkedMinutes);
        System.out.println(bill.toDetailedBill());

        if (payNow) {
            paymentService.confirmPayment(ticket, bill, lot);
            System.out.println("Payment status: PAID");
        } else {
            paymentService.markAsUnpaid(ticket, bill);
            System.out.println("Payment status: UNPAID (fine carried forward)");
        }

        spot.releaseSpot();
    }
}

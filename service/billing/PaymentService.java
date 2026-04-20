package service.billing;

import dto.billing.BillingBreakdownDto;
import models.Ticket;
import models.parking.ParkingLot;
import service.persistence.UnpaidFineRepository;

public class PaymentService {
    private final UnpaidFineRepository unpaidFineRepository;

    public PaymentService(UnpaidFineRepository unpaidFineRepository) {
        this.unpaidFineRepository = unpaidFineRepository;
    }

    public void confirmPayment(Ticket ticket, BillingBreakdownDto bill, ParkingLot parkingLot) {
        recordCurrentSessionFineEntries(ticket, bill);
        ticket.markPaid();
        parkingLot.addRevenue(bill.getPayableTotal());
        unpaidFineRepository.clearUnpaidFines(ticket.getVehicle().getPlateNum());
    }

    public void markAsUnpaid(Ticket ticket, BillingBreakdownDto bill) {
        recordCurrentSessionFineEntries(ticket, bill);
    }

    private void recordCurrentSessionFineEntries(Ticket ticket, BillingBreakdownDto bill) {
        String plate = ticket.getVehicle().getPlateNum();
        double reservationFine = bill.getCurrentFineSummary().getReservationMisuseFine();
        double overstayFine = bill.getCurrentFineSummary().getOverstayFine();
        if (reservationFine > 0) {
            unpaidFineRepository.addUnpaidFine(plate, "RESERVATION_MISUSE", reservationFine);
        }
        if (overstayFine > 0) {
            unpaidFineRepository.addUnpaidFine(plate, "OVERSTAY", overstayFine);
        }
    }

    public void confirmBasePaymentCarryFines(Ticket ticket, BillingBreakdownDto bill, ParkingLot parkingLot) {
        ticket.markPaid();
        parkingLot.addRevenue(bill.getNetParkingCharge());
        markAsUnpaid(ticket, bill);
    }

    public void addManualUnpaidFine(String plateNum, String fineType, double amount) {
        unpaidFineRepository.addUnpaidFine(plateNum, fineType, amount);
    }
}

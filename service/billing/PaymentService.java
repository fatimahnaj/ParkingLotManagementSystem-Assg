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
        ticket.markPaid();
        parkingLot.addRevenue(bill.getPayableTotal());
        unpaidFineRepository.clearUnpaidFines(ticket.getVehicle().getPlateNum());
    }

    public void markAsUnpaid(Ticket ticket, BillingBreakdownDto bill) {
        unpaidFineRepository.addUnpaidFine(ticket.getVehicle().getPlateNum(), bill.getCurrentFineSummary().getTotalFine());
    }
}

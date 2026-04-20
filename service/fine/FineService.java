package service.fine;

import dto.fine.FineSummaryDto;
import models.Ticket;

public class FineService {
    private final FinePolicy finePolicy;

    public FineService(FinePolicy finePolicy) {
        this.finePolicy = finePolicy;
    }

    public FineSummaryDto evaluateCurrentSessionFines(Ticket ticket, long parkedMinutes) {
        double reservationFine = finePolicy.computeReservationMisuseFine(ticket);
        double overstayFine = finePolicy.computeOverstayFine(parkedMinutes);
        return new FineSummaryDto(reservationFine, overstayFine);
    }

    public FineSummaryDto evaluateCurrentSessionFines(Ticket ticket, long parkedMinutes, String policyOption) {
        double reservationFine = finePolicy.computeReservationMisuseFine(ticket);
        double overstayFine = finePolicy.computeOverstayFine(parkedMinutes, policyOption);
        return new FineSummaryDto(reservationFine, overstayFine);
    }
}

package service.fine;

import models.Ticket;

public interface FinePolicy {
    double computeReservationMisuseFine(Ticket ticket);
    double computeOverstayFine(long parkedMinutes);
}

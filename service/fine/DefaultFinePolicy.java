package service.fine;

import models.Ticket;
import models.parking.SpotType;

public class DefaultFinePolicy implements FinePolicy {
    private static final int OVERSTAY_THRESHOLD_MINUTES = 24 * 60;
    private static final int OVERSTAY_GRACE_MINUTES = 15;
    private static final double OVERSTAY_FINE_PER_HOUR = 10.0;
    private static final double RESERVATION_MISUSE_FINE = 50.0;

    @Override
    public double computeReservationMisuseFine(Ticket ticket) {
        SpotType spotType = ticket.getParkingSpot().getType();
        boolean misuse = spotType == SpotType.RESERVED && !ticket.hasReservationPermit();
        return misuse ? RESERVATION_MISUSE_FINE : 0.0;
    }

    @Override
    public double computeOverstayFine(long parkedMinutes) {
        long overstayMinutes = parkedMinutes - OVERSTAY_THRESHOLD_MINUTES - OVERSTAY_GRACE_MINUTES;
        if (overstayMinutes <= 0) {
            return 0.0;
        }

        long overstayHours = (long) Math.ceil(overstayMinutes / 60.0);
        return overstayHours * OVERSTAY_FINE_PER_HOUR;
    }
}

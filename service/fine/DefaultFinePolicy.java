package service.fine;

import models.Ticket;
import models.parking.SpotType;
import java.util.function.Supplier;

public class DefaultFinePolicy implements FinePolicy {
    private static final int OVERSTAY_THRESHOLD_MINUTES = 24 * 60;
    private static final double RESERVATION_MISUSE_FINE = 50.0;
    private static final double OPTION_A_FINE = 50.0;
    private static final double OPTION_C_FINE_PER_HOUR = 20.0;
    private final Supplier<String> finePolicyOptionSupplier;

    public DefaultFinePolicy() {
        this(() -> "A");
    }

    public DefaultFinePolicy(Supplier<String> finePolicyOptionSupplier) {
        this.finePolicyOptionSupplier = finePolicyOptionSupplier == null ? () -> "A" : finePolicyOptionSupplier;
    }

    @Override
    public double computeReservationMisuseFine(Ticket ticket) {
        SpotType spotType = ticket.getParkingSpot().getType();
        boolean misuse = spotType == SpotType.RESERVED && !ticket.hasReservationPermit();
        return misuse ? RESERVATION_MISUSE_FINE : 0.0;
    }

    @Override
    public double computeOverstayFine(long parkedMinutes) {
        return computeOverstayFine(parkedMinutes, finePolicyOptionSupplier.get());
    }

    @Override
    public double computeOverstayFine(long parkedMinutes, String policyOption) {
        long overstayMinutes = parkedMinutes - OVERSTAY_THRESHOLD_MINUTES;
        if (overstayMinutes <= 0) {
            return 0.0;
        }

        String normalized = policyOption == null ? "A" : policyOption.trim().toUpperCase();
        switch (normalized) {
            case "B":
                return computeProgressiveFine(parkedMinutes);
            case "C":
                long overstayHours = (long) Math.ceil(overstayMinutes / 60.0);
                return overstayHours * OPTION_C_FINE_PER_HOUR;
            case "A":
            default:
                return OPTION_A_FINE;
        }
    }

    private double computeProgressiveFine(long parkedMinutes) {
        double fine = 0.0;
        if (parkedMinutes > 24 * 60) {
            fine += 50.0;
        }
        if (parkedMinutes > 48 * 60) {
            fine += 100.0;
        }
        if (parkedMinutes > 72 * 60) {
            fine += 150.0;
            fine += 200.0;
        }
        return fine;
    }
}

package dto.fine;

public class FineSummaryDto {
    private final double reservationMisuseFine;
    private final double overstayFine;
    private final double totalFine;

    public FineSummaryDto(double reservationMisuseFine, double overstayFine) {
        this.reservationMisuseFine = reservationMisuseFine;
        this.overstayFine = overstayFine;
        this.totalFine = reservationMisuseFine + overstayFine;
    }

    public double getReservationMisuseFine() {
        return reservationMisuseFine;
    }

    public double getOverstayFine() {
        return overstayFine;
    }

    public double getTotalFine() {
        return totalFine;
    }
}

package dto.billing;

import dto.fine.FineSummaryDto;

public class BillingBreakdownDto {
    private final long parkedMinutes;
    private final long billableHours;
    private final double baseFee;
    private final double discountAmount;
    private final FineSummaryDto currentFineSummary;
    private final double unpaidPreviousFines;
    private final double payableTotal;

    public BillingBreakdownDto(
        long parkedMinutes,
        long billableHours,
        double baseFee,
        double discountAmount,
        FineSummaryDto currentFineSummary,
        double unpaidPreviousFines
    ) {
        this.parkedMinutes = parkedMinutes;
        this.billableHours = billableHours;
        this.baseFee = baseFee;
        this.discountAmount = discountAmount;
        this.currentFineSummary = currentFineSummary;
        this.unpaidPreviousFines = unpaidPreviousFines;
        this.payableTotal = (baseFee - discountAmount) + currentFineSummary.getTotalFine() + unpaidPreviousFines;
    }

    public long getParkedMinutes() {
        return parkedMinutes;
    }

    public long getBillableHours() {
        return billableHours;
    }

    public double getBaseFee() {
        return baseFee;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public FineSummaryDto getCurrentFineSummary() {
        return currentFineSummary;
    }

    public double getUnpaidPreviousFines() {
        return unpaidPreviousFines;
    }

    public double getPayableTotal() {
        return payableTotal;
    }

    public String toDetailedBill() {
        StringBuilder builder = new StringBuilder();
        builder.append("===== BILL BREAKDOWN =====\n");
        builder.append("Parked Minutes      : ").append(parkedMinutes).append(" min\n");
        builder.append("Billable Hours      : ").append(billableHours).append(" hour(s)\n");
        builder.append("Base Fee            : RM ").append(String.format("%.2f", baseFee)).append("\n");
        builder.append("Discount            : RM ").append(String.format("%.2f", discountAmount)).append("\n");
        builder.append("Reservation Fine    : RM ").append(String.format("%.2f", currentFineSummary.getReservationMisuseFine())).append("\n");
        builder.append("Overstay Fine       : RM ").append(String.format("%.2f", currentFineSummary.getOverstayFine())).append("\n");
        builder.append("Unpaid Prev. Fines  : RM ").append(String.format("%.2f", unpaidPreviousFines)).append("\n");
        builder.append("------------------------------\n");
        builder.append("TOTAL PAYABLE       : RM ").append(String.format("%.2f", payableTotal)).append("\n");
        return builder.toString();
    }
}

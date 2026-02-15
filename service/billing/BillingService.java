package service.billing;

import java.time.Duration;
import java.time.LocalDateTime;

import dto.billing.BillingBreakdownDto;
import dto.fine.FineSummaryDto;
import models.Ticket;
import service.fine.FineService;
import service.persistence.UnpaidFineRepository;

public class BillingService {
    private final BillingPolicy billingPolicy;
    private final DiscountPolicy discountPolicy;
    private final FineService fineService;
    private final UnpaidFineRepository unpaidFineRepository;

    public BillingService(
        BillingPolicy billingPolicy,
        DiscountPolicy discountPolicy,
        FineService fineService,
        UnpaidFineRepository unpaidFineRepository
    ) {
        this.billingPolicy = billingPolicy;
        this.discountPolicy = discountPolicy;
        this.fineService = fineService;
        this.unpaidFineRepository = unpaidFineRepository;
    }

    public BillingBreakdownDto calculatePayable(Ticket ticket, LocalDateTime checkoutTime) {
        LocalDateTime entryTime = ticket.getVehicle().getEntryTime();
        long parkedMinutes =
            (entryTime == null || checkoutTime == null)
                ? 0
                : Math.max(0, Duration.between(entryTime, checkoutTime).toMinutes());
        long billableHours = billingPolicy.computeBillableHours(parkedMinutes);

        double hourlyRate = ticket.getParkingSpot().getType().getHourlyRate();
        double baseFee = hourlyRate * billableHours;
        double discountAmount = discountPolicy.computeDiscount(ticket, billableHours, baseFee);

        FineSummaryDto currentFineSummary = fineService.evaluateCurrentSessionFines(ticket, parkedMinutes);
        double unpaidPrevious = unpaidFineRepository.getUnpaidFineTotal(ticket.getVehicle().getPlateNum());

        return new BillingBreakdownDto(
            parkedMinutes,
            billableHours,
            baseFee,
            discountAmount,
            currentFineSummary,
            unpaidPrevious
        );
    }
}

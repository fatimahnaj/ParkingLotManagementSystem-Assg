package service.billing;

import models.Ticket;

public interface DiscountPolicy {
    double computeDiscount(Ticket ticket, long billableHours, double baseFee);
}

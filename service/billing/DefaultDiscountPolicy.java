package service.billing;

import models.Ticket;
import models.parking.SpotType;
import models.vehicle.Vehicle;

public class DefaultDiscountPolicy implements DiscountPolicy {
    @Override
    public double computeDiscount(Ticket ticket, long billableHours, double baseFee) {
        Vehicle vehicle = ticket.getVehicle();
        SpotType spotType = ticket.getParkingSpot().getType();

        if (!vehicle.isHandicappedCardHolder()) {
            return 0.0;
        }

        if (spotType == SpotType.HANDICAPPED) {
            return baseFee;
        }

        double handicappedRateTotal = 2.0 * billableHours;
        return Math.max(0.0, baseFee - handicappedRateTotal);
    }
}

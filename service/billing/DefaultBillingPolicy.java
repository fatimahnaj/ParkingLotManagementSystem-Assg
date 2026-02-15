package service.billing;

public class DefaultBillingPolicy implements BillingPolicy {
    @Override
    public long computeBillableHours(long parkedMinutes) {
        long safeMinutes = Math.max(0, parkedMinutes);
        return Math.max(1, (long) Math.ceil(safeMinutes / 60.0));
    }
}

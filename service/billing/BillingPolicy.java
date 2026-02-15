package service.billing;

public interface BillingPolicy {
    long computeBillableHours(long parkedMinutes);
}

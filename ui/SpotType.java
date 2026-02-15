package ui;

public enum SpotType {
    COMPACT(2.0),
    REGULAR(5.0),
    HANDICAPPED(2.0),
    RESERVED(10.0);

    private final double hourlyRate;

    SpotType(double rate) {
        this.hourlyRate = rate;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }
}

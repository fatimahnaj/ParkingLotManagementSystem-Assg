package service.persistence;

public interface UnpaidFineRepository {
    double getUnpaidFineTotal(String plateNum);
    void addUnpaidFine(String plateNum, String fineType, double amount);

    default void addUnpaidFine(String plateNum, double amount) {
        addUnpaidFine(plateNum, "UNPAID_FINE", amount);
    }

    void clearUnpaidFines(String plateNum);
}

package service.persistence;

public interface UnpaidFineRepository {
    double getUnpaidFineTotal(String plateNum);
    void addUnpaidFine(String plateNum, double amount);
    void clearUnpaidFines(String plateNum);
}

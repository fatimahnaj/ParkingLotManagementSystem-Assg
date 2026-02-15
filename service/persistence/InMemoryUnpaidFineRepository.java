package service.persistence;

import java.util.HashMap;
import java.util.Map;

public class InMemoryUnpaidFineRepository implements UnpaidFineRepository {
    private final Map<String, Double> unpaidByPlate = new HashMap<>();

    @Override
    public double getUnpaidFineTotal(String plateNum) {
        return unpaidByPlate.getOrDefault(plateNum, 0.0);
    }

    @Override
    public void addUnpaidFine(String plateNum, double amount) {
        double current = unpaidByPlate.getOrDefault(plateNum, 0.0);
        unpaidByPlate.put(plateNum, current + amount);
    }

    @Override
    public void clearUnpaidFines(String plateNum) {
        unpaidByPlate.remove(plateNum);
    }
}

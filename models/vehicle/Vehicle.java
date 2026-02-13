package vehicle;
import java.time.Duration;
import java.time.LocalDateTime;

public class Vehicle {
    //protected so that child can use it as well
    protected String plateNum;
    protected String type;
    protected LocalDateTime entryTime;
    protected LocalDateTime exitTime;
    //private String fine;

    //must pass in these data waktu nk create vehicle (WAJIBB)
    public Vehicle(String plateNum, String type) {
        this.plateNum = plateNum;
        this.type = type;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public long getDurationHours() {
        if (exitTime == null) return 0;
        return Duration.between(entryTime, exitTime).toMinutes();
    }

}

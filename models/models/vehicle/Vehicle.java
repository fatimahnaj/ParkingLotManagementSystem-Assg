package models.vehicle;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Vehicle {
    //protected so that child can use it as well
    protected String plateNum;
    protected String type;
    protected LocalDateTime entryTime;
    protected LocalDateTime exitTime;
    protected boolean handicappedCardHolder;
    //private String fine;

    //must pass in these data waktu nk create vehicle (WAJIBB)
    public Vehicle(String plateNum, String type) {
        this.plateNum = plateNum;
        this.type = type;
        this.handicappedCardHolder = false;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
    //getters
    public long getDurationHours() {
        if (exitTime == null) return 0;
        return Duration.between(entryTime, exitTime).toMinutes();
    }

    public String getType(){
        return type;
    }

    public String getPlateNum(){
        return plateNum;
    }

    public LocalDateTime getEntryTime(){
        return entryTime;
    }

    public LocalDateTime getExitTime(){
        return exitTime;
    }

    public String getType() {
        return type;
    }

    public boolean isHandicappedCardHolder() {
        return handicappedCardHolder;
    }

    public void setHandicappedCardHolder(boolean handicappedCardHolder) {
        this.handicappedCardHolder = handicappedCardHolder;
    }

    //get entry/exit time with better format
    public String getFormattedEntryTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedEntryTime = entryTime.format(formatter);
        return formattedEntryTime;
    }

    public String getFormattedExitTime(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedExitTime = exitTime.format(formatter);
        return formattedExitTime;
    }

    public String toString() {
        return "PlateNum = " + plateNum + " | Type = " + type + " | Entry time = " + getFormattedEntryTime();
}

}

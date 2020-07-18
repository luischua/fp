package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CrossCheckStatus {
    private LocalDateTime crossCheckStart;
    private LocalDateTime crossCheckEnd;
    private String status;
    private String hitId;

    public String getHitId() {
        return hitId;
    }

    public void setHitId(String hitId) {
        this.hitId = hitId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void startCrossCheck(){
        crossCheckStart = LocalDateTime.now();
    }

    public void endCrossCheck(){
        crossCheckEnd = LocalDateTime.now();
    }

}

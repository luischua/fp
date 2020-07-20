package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CrossCheckStatus {
    private LocalDateTime crossCheckStart;
    private LocalDateTime crossCheckEnd;
    private VerificationStatus status;
    private List<String> hitId;

    public List<String> getHitId() {
        return hitId;
    }

    public void setHitId(List<String> hitId) {
        this.hitId = hitId;
    }

    public VerificationStatus getStatus() {
        return status;
    }

    public void setStatus(VerificationStatus status) {
        this.status = status;
    }

    public void startCrossCheck(){
        crossCheckStart = LocalDateTime.now();
    }

    public void endCrossCheck(){
        crossCheckEnd = LocalDateTime.now();
    }

}

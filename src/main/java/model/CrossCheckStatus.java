package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CrossCheckStatus {
    private LocalDateTime crossCheckStart;
    private LocalDateTime crossCheckEnd;
    private VerificationStatus status;
    private List<VerificationResult> hitList;

    public List<VerificationResult> getHitList() {
        return hitList;
    }

    public void setHitList(List<VerificationResult> hitList) {
        this.hitList = hitList;
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

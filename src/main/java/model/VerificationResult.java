package model;

import org.lightcouch.Document;
import util.CouchDBUtil;

public class VerificationResult extends Document {
    private String base64Image;
    private String userId;
    private double score;

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public void save(){
        CouchDBUtil.getDbClient("verification").save(this);
    }
}

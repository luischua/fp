package model;

import org.apache.commons.codec.binary.Base64;
import org.lightcouch.*;
import util.CouchDBUtil;
import util.FingerprintAnalyzer;

public class Fingerprint extends Document {
    private String base64Image;
    private String verifiedNode;
    private int duration;
    private String status;

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    public void save(){
        Response r = CouchDBUtil.getDbClient("fingerprint").save(this);
        this.setId(r.getId());
    }

    public static Fingerprint find(String id){
        return CouchDBUtil.getDbClient("fingerprint").find(Fingerprint.class, id);
    }

    public boolean match(byte[] fingerprintBytes, boolean saveVerification){
        double score = FingerprintAnalyzer.getScore(fingerprintBytes, Base64.decodeBase64(base64Image));
        double threshold = 40;
        if(saveVerification){
            Verification v = new Verification();
            v.setUserId(this.getId());
            v.setBase64Image(Base64.encodeBase64String(fingerprintBytes));
            v.setScore(score);
            v.save();
        }
        return score > threshold;
    }
}

package model;

import org.apache.commons.codec.binary.Base64;
import org.lightcouch.*;
import util.CouchDBUtil;
import util.FingerprintAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class Fingerprint extends Document {
    private String base64Image;

    private List<CrossCheckStatus> crossCheckStatusList = new ArrayList<CrossCheckStatus>();

    public void newCrossCheck(){
        CrossCheckStatus status = new CrossCheckStatus();
        status.startCrossCheck();
        Person hitPerson = Person.crosscheck(getImage());
        status.endCrossCheck();
        Person currentPerson = Person.find(getId());
        if(hitPerson != null){
            status.setStatus("success");
            currentPerson.setVerifiedStatus("success");
        }else{
            status.setStatus("fail");
            status.setHitId(hitPerson.getId());
            currentPerson.setVerifiedStatus("fail");
        }
        currentPerson.save();
        crossCheckStatusList.add(status);
    }

    public String getBase64Image() {
        return base64Image;
    }

    public byte[] getImage(){
        return Base64.decodeBase64(base64Image);
    }

    public void setImage(byte[] image){
        base64Image = Base64.encodeBase64String(image);
    }

    public void save(){
        if(this.getRevision() != null){
            Response r = CouchDBUtil.getDbClient("fingerprint").update(this);
        }else {
            Response r = CouchDBUtil.getDbClient("fingerprint").save(this);
            this.setId(r.getId());
        }

    }

    public static Fingerprint find(String id){
        return CouchDBUtil.getDbClient("fingerprint").find(Fingerprint.class, id);
    }

    public boolean match(byte[] fingerprintBytes, boolean saveVerification){
        double score = FingerprintAnalyzer.getScore(fingerprintBytes, Base64.decodeBase64(base64Image));
        double threshold = 40;
        if(saveVerification){
            VerificationResult v = new VerificationResult();
            v.setUserId(this.getId());
            v.setBase64Image(Base64.encodeBase64String(fingerprintBytes));
            v.setScore(score);
            v.save();
        }
        return score > threshold;
    }
}

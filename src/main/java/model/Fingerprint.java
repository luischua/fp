package model;

import com.machinezoo.sourceafis.FingerprintMatcher;
import org.apache.commons.codec.binary.Base64;
import org.lightcouch.*;
import util.CouchDBUtil;
import util.FingerprintAnalyzer;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class Fingerprint extends Document {

    private static final CouchDbClient DB_CLIENT = CouchDBUtil.getDbClient(Fingerprint.class);

    private String base64Image;

    private String cachedTemplate;

    private long dateTimeInLong;

    private List<CrossCheckStatus> crossCheckStatusList = new ArrayList<CrossCheckStatus>();

    public Fingerprint(byte[] image){
        base64Image = Base64.encodeBase64String(image);
        cachedTemplate = Base64.encodeBase64String(FingerprintAnalyzer.getTemplateByte(image));
    }

    public String getImage() {
        return base64Image;
    }

    public byte[] getImageByte(){
        return Base64.decodeBase64(base64Image);
    }

    public String getCachedTemplate() {
        return cachedTemplate;
    }

    public byte[] getCachedTemplateByte(){
        return Base64.decodeBase64(cachedTemplate);
    }

    public long getDateTimeInLong() {
        return dateTimeInLong;
    }

    public void setDateTimeInLong(long dateTimeInLong) {
        this.dateTimeInLong = dateTimeInLong;
    }

    public void save(){
        if(this.getRevision() != null){
            Response r = DB_CLIENT.update(this);
        }else {
            this.setDateTimeInLong(getCurrentTimeInMillis());
            Response r = DB_CLIENT.save(this);
            this.setId(r.getId());
        }

    }

    private long getCurrentTimeInMillis()
    {
        return LocalDateTime.now().atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli();
    }

    public static Fingerprint find(String id){
        return CouchDBUtil.getDbClient(Fingerprint.class).find(Fingerprint.class, id);
    }

    public void newCrossCheck(){
        CrossCheckStatus status = new CrossCheckStatus();
        status.startCrossCheck();
        List<VerificationResult> hitList = crosscheckTemplate(this);
        status.endCrossCheck();
        Person currentPerson = Person.find(getId());
        if(hitList.size() == 0){
            status.setStatus(VerificationStatus.SUCCESS);
            currentPerson.setVerifiedStatus(VerificationStatus.SUCCESS);
        }else{
            status.setStatus(VerificationStatus.FAIL);
            currentPerson.setVerifiedStatus(VerificationStatus.FAIL);
            status.setHitList(hitList);
        }
        currentPerson.save();
        crossCheckStatusList.add(status);
    }

    public boolean match(byte[] fingerprintBytes){
        FingerprintAnalyzer analyzer = new FingerprintAnalyzer(this.getCachedTemplateByte());
        byte[] templateBytes = FingerprintAnalyzer.getTemplateByte(fingerprintBytes);
        double score = analyzer.getScore(templateBytes);
        VerificationResult v = new VerificationResult(this.getId(), score);
        v.setBase64Image(Base64.encodeBase64String(fingerprintBytes));
        v.save();
        return score > FingerprintAnalyzer.THRESHOLD;
    }

    public static List<VerificationResult> crosscheckTemplate(Fingerprint probe){
        View allDocs = DB_CLIENT.view("_all_docs");
        String nextParam = "";
        Page<Fingerprint> page;
        List<VerificationResult > hitList = new ArrayList<VerificationResult>();
        FingerprintAnalyzer analyzer = new FingerprintAnalyzer(probe.getCachedTemplateByte());
        do {
            page = allDocs.queryPage(10, null, Fingerprint.class);
            List<Fingerprint> list = page.getResultList();
            for(Fingerprint f : list){
                if(!f.getId().equals(probe.getId())) {
                    System.out.println("Matching" + f.getId());
                    double score = analyzer.getScore(f.getCachedTemplateByte());
                    if (score > FingerprintAnalyzer.THRESHOLD) {
                        System.out.println("Hit" + f.getId());
                        VerificationResult result = new VerificationResult(f.getId(), score);
                        hitList.add(result);
                    }
                }
            }
            nextParam = page.getNextParam();
        }while(page.isHasNext());
        return hitList;
    }
}

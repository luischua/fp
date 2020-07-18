package model;

import org.lightcouch.Document;
import org.lightcouch.Page;
import org.lightcouch.Response;
import org.lightcouch.View;
import util.CouchDBUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Person extends Document {
    private String name;
    private String base64IdImage;
    private LocalDate birthDate;

    public String getVerifiedStatus() {
        return verifiedStatus;
    }

    public void setVerifiedStatus(String verifiedStatus) {
        this.verifiedStatus = verifiedStatus;
        lastVerified = LocalDate.now();
    }

    private String verifiedStatus;
    private LocalDate lastVerified;
    private List<EditHistory> saveHistory = new ArrayList<EditHistory>();

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public long getAge(){
        if(birthDate != null) {
            return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
        }
        return -1;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getBase64IdImage() {
        return base64IdImage;
    }

    public void setBase64IdImage(String base64IdImage, String extension) {
        this.base64IdImage = "data:image/"+extension+";base64, "+base64IdImage;
    }

    public void save(String userId){
        EditHistory h = new EditHistory();
        h.setUserId(userId);
        saveHistory.add(h);
        if(this.getRevision() != null){
            Response r = CouchDBUtil.getDbClient("person").update(this);
        }else{
            Response r = CouchDBUtil.getDbClient("person").save(this);
            this.setId(r.getId());
        }
    }

    public void save(){
        save("system");
    }

    public static Person find(String id){
        return CouchDBUtil.getDbClient("person").find(Person.class, id);
    }

    public static Person crosscheck(byte[] fingerprintBytes){
        View allDocs = CouchDBUtil.getDbClient("fingerprint").view("_all_docs");
        String nextParam;
        Page<Fingerprint> page;
        do {
            page = allDocs.queryPage(10, null, Fingerprint.class);
            List<Fingerprint> list = page.getResultList();
            for(Fingerprint f : list){
                if(f.match(fingerprintBytes, false)){
                    return Person.find(f.getId());
                }
            }
            nextParam = page.getNextParam();
        }while(page.isHasNext());
        return null;
    }
}

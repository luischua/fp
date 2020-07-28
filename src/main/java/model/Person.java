package model;

import org.apache.commons.codec.binary.Base64;
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
    private String idImage;
    private String idImageExtension;
    private LocalDate birthDate;
    private String qrImage;
    private VerificationStatus verifiedStatus;
    private LocalDate lastVerified;

    public byte[] getIdImage() {
        return Base64.decodeBase64(idImage);
    }

    public void setIdImage(byte[] idImage, String extension) {
        this.idImage = Base64.encodeBase64String(idImage);
        this.idImageExtension = extension;
    }

    public byte[] getQrImage() {
        return  Base64.decodeBase64(qrImage);
    }

    public void setQrImage(byte[] qrImage) {
        this.qrImage = Base64.encodeBase64String(qrImage);;
    }

    public VerificationStatus getVerifiedStatus() {
        return verifiedStatus;
    }

    public void setVerifiedStatus(VerificationStatus verifiedStatus) {
        this.verifiedStatus = verifiedStatus;
        lastVerified = LocalDate.now();
    }

    public List<EditHistory> getSaveHistory() {
        return saveHistory;
    }

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
        if(name != null) {
            this.name = name.toUpperCase();
        }
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
            this.setRevision(r.getRev());
        }
    }

    public void save(){
        save("system");
    }

    public static Person find(String id){
        return CouchDBUtil.getDbClient("person").find(Person.class, id);
    }


    public static List<Person> findByName(String name){
        String query = "{ \"selector\": { \"name\": { \"$eq\": \""+name.toUpperCase()+"\" } }, \"limit\":10 }";
        System.out.println("Query:"+query);
        return CouchDBUtil.getDbClient("person").findDocs(query,Person.class);
    }

    public static List<String> crosscheckTemplate(byte[] templateBytes){
        View allDocs = CouchDBUtil.getDbClient("fingerprint").view("_all_docs");
        String nextParam = "";
        Page<Fingerprint> page;
        List<String> hitList = new ArrayList<String>();
        do {
            page = allDocs.queryPage(10, null, Fingerprint.class);
            List<Fingerprint> list = page.getResultList();
            for(Fingerprint f : list){
                System.out.println("Matching"+f.getId());
                if(f.matchTemplate(templateBytes)){
                    System.out.println("Hit"+f.getId());
                    hitList.add(f.getId());
                }
            }
            nextParam = page.getNextParam();
        }while(page.isHasNext());
        return hitList;
    }
}

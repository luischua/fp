package model;

import com.machinezoo.sourceafis.FingerprintMatcher;
import org.apache.commons.codec.binary.Base64;
import org.lightcouch.*;
import util.CouchDBUtil;
import util.FingerprintAnalyzer;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Person extends Document {

    private static final CouchDbClient DB_CLIENT = CouchDBUtil.getDbClient(Person.class);

    private String idImage;
    private String idImageExtension;
    private String qrImage;
    private String name;
    private LocalDate birthDate;
    private VerificationStatus verifiedStatus;
    private LocalDate lastVerified;
    private List<EditHistory> saveHistory = new ArrayList<EditHistory>();

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if(name != null) {
            this.name = name.toUpperCase();
        }
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
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

    public long getAge(){
        if(birthDate != null) {
            return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
        }
        return -1;
    }

    public void save(String userId){
        EditHistory h = new EditHistory();
        h.setUserId(userId);
        saveHistory.add(h);
        if(this.getRevision() != null){
            Response r = DB_CLIENT.update(this);
        }else{
            Response r = DB_CLIENT.save(this);
            this.setId(r.getId());
            this.setRevision(r.getRev());
        }
    }

    public void save(){
        save("system");
    }

    public static Person find(String id){
        return DB_CLIENT.find(Person.class, id);
    }

    public static List<Person> findByName(String name){
        String query = "{ \"selector\": { \"name\": { \"$eq\": \""+name.toUpperCase()+"\" } }, \"limit\":10 }";
        System.out.println("Query:"+query);
        return DB_CLIENT.findDocs(query,Person.class);
    }
}

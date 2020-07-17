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
import java.util.Date;

public class Person extends Document {
    private String name;
    private String base64IdImage;
    private LocalDate birthDate;
    private List<History> saveHistory = new ArrayList<History>();

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public long getAge(){
        return ChronoUnit.YEARS.between(birthDate, LocalDate.now());
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
        History h = new History();
        h.setUserId(userId);
        saveHistory.add(h);
        Response r = CouchDBUtil.getDbClient("person").save(this);
        this.setId(r.getId());
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

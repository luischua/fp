package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Document;
import util.BusinessUtil;
import util.CouchDBUtil;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class CouchDocument extends Document {

    private String narrative = "";
    private LocalDateTime lastEdited;

    public String getLastEditedString(){
        return BusinessUtil.getDateTimeString(lastEdited);
    }

    public void addNarrative(String s){
        narrative = narrative.concat(s);
    }

    @Override
    public String toString() {
        return "CouchDocument(id="+getId()+", revision="+getRevision()+", narrative="+narrative+")";
    }

    public void beforeNew(SaveResult r){
    }

    public void beforeSave(SaveResult r){
        lastEdited = LocalDateTime.now();
    }

    public void afterSave(SaveResult r){
    }

    public static CouchDocument findById(String id, Class clz){
        CouchDbClient dbClient = CouchDBUtil.getDbClient(clz);
        return (CouchDocument)dbClient.find(clz, id);
    }

    public static List retreiveByFkId(String id, Class clz, String view){
        CouchDbClient dbClient = CouchDBUtil.getDbClient(clz);
        return dbClient.view(view)
                .key(id)
                .includeDocs(true)
                .query(clz);
    }

    public static String getIdByName(String name, Class clz, String view){
        CouchDbClient client = CouchDBUtil.getDbClient(clz);
        List<CouchDocument> list = client.view(view)
                .key(name)
                .includeDocs(true)
                .query(clz);
        System.out.println(list);
        if(list.size() == 1) {
            return list.get(0).getId();
        }
        return null;
    }

    public String getDBId(String name, Class clz){
        CouchDbClient dbClient = CouchDBUtil.getDbClient(clz);
        List<CouchDocument> list = dbClient.findDocs(
                "{\"selector\": {\"name\": {\"$eq\": \""+name+"\"}}}", clz);
        if(list.size() > 0) {
            return list.get(0).getId();
        }
        return null;
    }
}

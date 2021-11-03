package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Document;
import util.CouchDBUtil;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
public class CouchDocument extends Document {

    private String narrative = "";
    private long lastEdited = System.currentTimeMillis();

    public void setLastEdited(long s){
        //don't allow setting
    }

    public void addNarrative(String s){
        narrative = narrative.concat(s);
    }

    @Override
    public String toString() {
        return "CouchDocument(id="+getId()+", revision="+getRevision()+", narrative="+narrative+")";
    }

    public void beforeNew(){
    }

    public void beforeSave(){
    }

    public static CouchDocument findById(String id, Class clz){
        CouchDbClient dbClient = CouchDBUtil.getDbClient(clz);
        return (CouchDocument)dbClient.find(clz, id);
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

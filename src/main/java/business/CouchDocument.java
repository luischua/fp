package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Document;
import util.CouchDBUtil;

@Data
@EqualsAndHashCode(callSuper=false)
public class CouchDocument extends Document {

    private String narrative = "";

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
}

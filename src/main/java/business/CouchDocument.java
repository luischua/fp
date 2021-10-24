package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.lightcouch.Document;

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
}

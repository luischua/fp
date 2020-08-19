package model;

import lombok.EqualsAndHashCode;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Document;
import util.CouchDBUtil;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode(callSuper=false)
public class VerificationResult extends Document {

    private static final CouchDbClient DB_CLIENT = CouchDBUtil.getDbClient(VerificationResult.class);

    private String base64Image;
    private String userId;
    private double score;
    private String type;

    public void save(){
        DB_CLIENT.save(this);
    }
}

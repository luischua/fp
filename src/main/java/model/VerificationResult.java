package model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Document;
import util.CouchDBUtil;

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

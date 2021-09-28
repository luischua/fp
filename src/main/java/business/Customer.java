package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.lightcouch.CouchDbClient;
import util.CouchDBUtil;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class Customer extends CouchDocument {
    private String name;
    private String address;
    private String telephone;
    private String contact;
    private int terms;
    private String companyId;
    public Company findCompany(){
        CouchDbClient client = CouchDBUtil.getDbClient(Company.class);
        return client.find(Company.class, companyId);
    }
}

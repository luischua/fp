package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.lightcouch.CouchDbClient;
import util.CouchDBUtil;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class Company extends CouchDocument{
    private String name;
    private String ownerId;

    public User findOwner(){
        CouchDbClient client = CouchDBUtil.getDbClient(User.class);
        return client.find(User.class, ownerId);
    }
}

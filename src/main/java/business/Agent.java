package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class Agent extends CouchDocument{
    private String name;
    private String address;
    private String telephone;
}

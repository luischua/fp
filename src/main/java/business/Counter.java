package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class Counter extends CouchDocument{
    private String name;
    private long value;
    public void reserve(int d){
        value = value + d;
    }
}

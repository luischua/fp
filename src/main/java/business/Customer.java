package business;

import lombok.Getter;
import lombok.Setter;
import org.lightcouch.Document;
//@Accessors(fluent = true)
@Getter
@Setter
public class Customer extends Document {
    private String name;
    private String address;
    private String telephone;
    private String contact;
    private int terms;
}

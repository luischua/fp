package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class Product extends CouchDocument {
    private String name;
    private BigDecimal price;
    private String defaultDiscount;
    private Map<String, String> customerDiscount = new HashMap<String, String>();

    public Product(){
        customerDiscount.put("test", "ssss");
    }

    public void addCustomerDiscount(Customer cust, String d){
        customerDiscount.put(cust.getName(), d);
    }

    public String getDiscount(Customer cust){
        if(cust == null){
            return defaultDiscount;
        }
        String d = customerDiscount.get(cust.getName());
        if(d == null){
            return defaultDiscount;
        }
        return d;
    }
}

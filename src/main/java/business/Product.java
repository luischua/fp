package business;

import lombok.Getter;
import lombok.Setter;
import org.lightcouch.Document;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Product extends Document {
    private String name;
    private BigDecimal price;
    private Discount defaultDiscount = new Discount();
    private Map<String, Discount> customerDiscount = new HashMap<String, Discount>();

    public void addCustomerDiscount(Customer cust, Discount d){
        customerDiscount.put(cust.getName(), d);
    }

    public Discount getDiscount(Customer cust){
        Discount d = customerDiscount.get(cust.getName());
        if(d == null){
            return defaultDiscount;
        }
        return d;
    }
}

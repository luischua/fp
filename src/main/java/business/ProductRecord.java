package business;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRecord extends Product {
    private int quantity;
    public BigDecimal getTotal(){
        return this.getPrice().multiply(new BigDecimal(quantity));
    }
}

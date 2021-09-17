package business;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class ProductRecord{
    private String name;
    private BigDecimal price;
    private int quantity;
    private Discount discount;
    public BigDecimal getTotal(){
        return this.getPrice().multiply(new BigDecimal(quantity)).multiply(discount.getValue()).setScale(2, RoundingMode.UP);
    }
}

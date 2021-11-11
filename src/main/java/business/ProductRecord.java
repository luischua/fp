package business;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class ProductRecord{
    private String productId;
    private String name;
    private BigDecimal price;
    private int quantity;
    private String discount;
    private String displayOrdering = "zzz";
    public BigDecimal getTotal(){
        return this.getPrice().multiply(new BigDecimal(quantity)).multiply(Discount.calculateDiscount(discount)).setScale(2, RoundingMode.UP);
    }
}

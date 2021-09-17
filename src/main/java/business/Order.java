package business;

import lombok.Getter;
import lombok.Setter;
import org.lightcouch.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class Order extends Document {
    private Customer customer;
    private long receiptNo;
    private LocalDateTime createTime;
    List<ProductRecord> products;
    List<ProductRecord> promo;

    public void addProductRecord(Product p, int quantity) {
        addProductRecord(p, quantity, false);
    }

    public void addProductRecord(Product p, int quantity, boolean isPromo){
        ProductRecord record = new ProductRecord();
        record.setName(p.getName());
        record.setPrice(p.getPrice());
        record.setQuantity(quantity);
        record.setDiscount(p.getDiscount(customer));
        if(products == null){
            products = new ArrayList<ProductRecord>();
        }
        if(promo == null){
            promo = new ArrayList<ProductRecord>();
        }
        if(isPromo) {
            promo.add(record);
        }
        else{
            products.add(record);
        }
    }

    public BigDecimal getTotal(){
        BigDecimal total = new BigDecimal(0);
        for(ProductRecord record: products){
            //System.out.println(record);
            total = total.add(record.getTotal());
        }
        return total;
    }
}

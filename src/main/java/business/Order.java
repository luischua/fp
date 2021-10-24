package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.lightcouch.CouchDbClient;
import util.CouchDBUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class Order extends CouchDocument {
    private Customer customer;
    private long receiptNo;
    private LocalDate orderDate;
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

    public void beforeNew(){
        try {
            String tableClass = "business.Counter";
            Class clz = Class.forName(tableClass);
            CouchDbClient dbClient = CouchDBUtil.getDbClient(clz);
            List<Counter> list = dbClient.findDocs(
                    "{\"selector\": {\"name\": {\"$eq\": \"Order\"}}}", clz);
            Counter c = list.get(0);
            receiptNo = c.getValue();
            c.reserve(1);
            dbClient.update(c);
            if(orderDate == null){
                orderDate = LocalDate.now();
            }
        }catch (Exception e){
        }
    }
}

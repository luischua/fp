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
    private String customerId;
    private String customerName;
    private long receiptNo;
    private int terms;
    private LocalDate deliveredDate;
    List<ProductRecord> products;
    List<ProductRecord> promo;
    private int month;
    private int year;

    public void setDeliveredDate(LocalDate d){
        deliveredDate = d;
        month = deliveredDate.getMonth().getValue();
        year = deliveredDate.getYear();
    }

    public LocalDate getCollectionDate(){
        if(deliveredDate != null && terms != 0) {
            return deliveredDate.plusDays(terms);
        }
        return null;
    }

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
        if(products != null) {
            for (ProductRecord record : products) {
                //System.out.println(record);
                total = total.add(record.getTotal());
            }
        }
        return total;
    }

    public void beforeNew(){
        try {
            CouchDbClient dbClient = CouchDBUtil.getDbClient(Counter.class);
            List<Counter> list = dbClient.findDocs(
                    "{\"selector\": {\"name\": {\"$eq\": \"Order\"}}}", Counter.class);
            Counter c = list.get(0);
            c.reserve(1);
            dbClient.update(c);
            receiptNo = c.getValue();
        }catch (Exception e){
            throw new RuntimeException("Can't get sequence number");
        }
    }

    public void beforeSave(){
        CouchDbClient dbClient = CouchDBUtil.getDbClient(Customer.class);
        List<Customer> list = dbClient.findDocs(
                "{\"selector\": {\"name\": {\"$eq\": \""+customerName+"\"}}}", Customer.class);
        if(list.size() > 0) {
            Customer c = list.get(0);
            customerId = c.getId();
            terms = c.getTerms();
        }
    }
}

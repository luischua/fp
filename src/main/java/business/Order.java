package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.lightcouch.CouchDbClient;
import util.CouchDBUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private LocalDate canceledDate;
    private LocalDate paidDate;
    List<ProductRecord> products;
    List<ProductRecord> promo;
    Map<String, OrderPayment> payments;
    private OrderPeriod period;

    public void addPayments(String paymentId, OrderPayment p){
        if(payments == null){
            payments = new HashMap<String, OrderPayment>();
        }
        payments.put(paymentId, p);
        if( getTotalPayments().compareTo(getTotal()) == 0 ){
            paidDate = LocalDate.now();
        }
    }

    public BigDecimal getTotalPayments(){
        BigDecimal total = new BigDecimal(0);
        for(OrderPayment s : payments.values()){
            total = total.add(s.getValue());
        }
        return total;
    }

    public void setDeliveredDate(LocalDate d){
        deliveredDate = d;
        period.setMonth(deliveredDate.getMonth().getValue());
        period.setYear(deliveredDate.getYear());
        period.setYearMonth(period.getYear()*100+period.getMonth());
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
        record.setDisplayOrdering(p.getDisplayOrdering());
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
        //sort using stream
        products = products.stream()
                .sorted(Comparator.comparing(ProductRecord::getDisplayOrdering))
                .collect(Collectors.toList());
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

    public void beforeNew(SaveResult r){
        super.beforeNew(r);
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

    public void beforeSave(SaveResult r){
        super.beforeSave(r);
        CouchDbClient dbClient = CouchDBUtil.getDbClient(Customer.class);
        List<Customer> list = dbClient.findDocs(
                "{\"selector\": {\"name\": {\"$eq\": \""+customerName+"\"}}}", Customer.class);
        if(list.size() > 0) {
            Customer c = list.get(0);
            customerId = c.getId();
            terms = c.getTerms();
        }
    }

    public void afterSave(){
        computeOrderStats(customerId);
    }

    public static void computeOrderStats(String customerId){
        List<Order> orders = (List<Order>)CouchDocument.retreiveByFkId(customerId, Order.class, "Order/byCustomerId");
        OrderStats stats = new OrderStats();
        for(Order order: orders){
            if(order.getCanceledDate() != null){
                stats.getCancelled().addTotal(order.getTotal(), order.getReceiptNo());
            } else if(order.getDeliveredDate() != null) {
                stats.getToBeCollected().addTotal(order.getTotal(), order.getReceiptNo());
            } else if(order.getPaidDate() != null) {
                stats.getPaid().addTotal(order.getTotal(), order.getReceiptNo());
            } else{
                stats.getToBeDelivered().addTotal(order.getTotal(), order.getReceiptNo());
            }
        }
        CouchDbClient dbClient = CouchDBUtil.getDbClient(Customer.class);
        Customer c = dbClient.find(Customer.class, customerId);
        c.setOrderStats(stats);
        dbClient.update(c);
    }
}

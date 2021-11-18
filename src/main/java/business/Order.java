package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.lightcouch.CouchDbClient;
import util.BusinessUtil;
import util.CouchDBUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private LocalDate collectionDate;
    private LocalDate paidDate;
    List<ProductRecord> products;
    List<ProductRecord> promo;
    Map<String, OrderPayment> payments;
    private OrderPeriod period;
    private boolean paymentExceeded;

    public Collection<OrderPayment> getAllPayments(){
        if(payments != null) {
            return payments.values();
        }
        return null;
    }

    public String getDeliveredDateString(){
        return BusinessUtil.getDateString(deliveredDate);
    }

    public String getCanceledDateString(){
        return BusinessUtil.getDateString(canceledDate);
    }

    public String getPaidDateString(){
        return BusinessUtil.getDateString(paidDate);
    }

    public String getCollectionDateString(){
        return BusinessUtil.getDateString(getCollectionDate());
    }

    public Long getDaysFromDeliveredToPaid(){
        if(deliveredDate != null && paidDate != null){
            return ChronoUnit.DAYS.between(deliveredDate, paidDate);
        }
        return null;
    }

    public void addPayments(String paymentId, OrderPayment p){
        if(payments == null){
            payments = new HashMap<String, OrderPayment>();
        }
        payments.put(paymentId, p);
        if( getTotalPayments().compareTo(getTotal()) == 0 ){
            paidDate = LocalDate.now();
        }
        if( getTotalPayments().compareTo(getTotal()) == 1 ){
            paymentExceeded = true;
        }
    }

    public BigDecimal getTotalPayments(){
        BigDecimal total = new BigDecimal(0);
        if(payments != null) {
            for(OrderPayment s : payments.values()){
                total = total.add(s.getValue());
            }
        }
        return total;
    }

    public void setDeliveredDate(LocalDate d){
        deliveredDate = d;
        period = new OrderPeriod();
        period.setMonth(deliveredDate.getMonth().getValue());
        period.setYear(deliveredDate.getYear());
        period.setYearMonth(period.getYear()*100+period.getMonth());
    }

    public LocalDate getCollectionDate(){
        if(deliveredDate != null) {
            collectionDate = deliveredDate.plusDays(terms);
        } else {
            collectionDate = null;
        }
        return collectionDate;
    }

    public void addProductRecord(Product p, int quantity) {
        addProductRecord(p, quantity, false);
    }

    public void addProductRecord(Product p, int quantity, boolean isPromo){
        ProductRecord record = new ProductRecord();
        record.setProductId(p.getId());
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
        //refresh computed collectionDate value
        getCollectionDate();
    }

    public void afterSave(){
        Customer.computeOrderStats(customerId);
    }
}

package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jsoup.helper.StringUtil;
import org.lightcouch.CouchDbClient;
import util.CouchDBUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class Payment extends CouchDocument{

    private long paymentNo;
    //cash, online, check
    private String type;
    private String receiptNoList;
    private BigDecimal value;
    private String bank;
    private String checkNo;
    private LocalDate clearingDate;
    private LocalDate bouncedDate;
    private List<PaymentRecord> receiptNos;

    public void beforeNew(){
        super.beforeNew();
        try {
            CouchDbClient dbClient = CouchDBUtil.getDbClient(Counter.class);
            List<Counter> list = dbClient.findDocs(
                    "{\"selector\": {\"name\": {\"$eq\": \"Payment\"}}}", Counter.class);
            Counter c = list.get(0);
            c.reserve(1);
            dbClient.update(c);
            paymentNo = c.getValue();
        }catch (Exception e){
            throw new RuntimeException("Can't get sequence number");
        }
    }

    public void beforeSave() {
        super.beforeSave();
        if(!StringUtil.isBlank(receiptNoList)) {
            CouchDbClient dbClient = CouchDBUtil.getDbClient(Order.class);
            List<String> keyList = Arrays.asList(receiptNoList.split(","));
            System.out.println(keyList);
            List<Order> orderList = dbClient.view("Order/byReceiptNo").keys(keyList)
                    .includeDocs(true)
                    .query(Order.class);
            receiptNos = new ArrayList<PaymentRecord>();
            System.out.println(orderList);
            for (Order o : orderList) {
                PaymentRecord p = new PaymentRecord();
                p.setOrderId(o.getId());
                p.setReceiptNo(o.getReceiptNo());
                p.setValue(o.getTotal());
                receiptNos.add(p);
            }
        }
    }

    public void afterSave() {
        super.afterSave();
        CouchDbClient dbClient = CouchDBUtil.getDbClient(Order.class);
        for (PaymentRecord r : receiptNos) {
            Order o = dbClient.find(Order.class, r.getOrderId());
            OrderPayment p = new OrderPayment();
            p.setPaymentNo(getPaymentNo());
            p.setValue(getValue());
            o.addPayments(getId(), p);
            dbClient.save(o);
        }
    }
}

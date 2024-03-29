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
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class Payment extends CouchDocument{

    private long paymentNo;
    private String agentId;
    private String agentName;
    //cash, online, check
    private String type;
    private String receiptNoList;
    private BigDecimal value;
    private BigDecimal rebateValue;
    private String bank;
    private long checkNo;
    private LocalDate clearingDate;
    private LocalDate processedDate;
    private LocalDate bouncedDate;
    private List<PaymentRecord> receiptNos;
    private String customerId;
    private String customerName;

    private BigDecimal computeValueAfterRebate(){
        if(rebateValue != null) {
            return value.subtract(rebateValue);
        }
        return value;
    }

    public void beforeNew(SaveResult r){
        super.beforeNew(r);
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

    public void beforeSave(SaveResult r) {
        super.beforeSave(r);
        if(!StringUtil.isBlank(receiptNoList)) {
            CouchDbClient dbClient = CouchDBUtil.getDbClient(Order.class);
            List<Long> keyList = new ArrayList<Long>();
            for(String num :receiptNoList.split(",")){
                keyList.add(Long.parseLong(num));
            }
            System.out.println(keyList);
            List<Order> orderList = dbClient.view("Order/byReceiptNo").keys(keyList)
                    .includeDocs(true)
                    .query(Order.class);
            receiptNos = new ArrayList<PaymentRecord>();
            System.out.println(orderList);
            customerId = null;
            customerName = null;
            BigDecimal paymentAmount = computeValueAfterRebate();
            for (Order o : orderList) {
                PaymentRecord p = new PaymentRecord();
                if(customerId == null){
                    customerId = o.getCustomerId();
                    customerName = o.getCustomerName();
                    agentId = o.getAgentId();
                    agentName = o.getAgentName();
                }else{
                    if(!customerId.equals(o.getCustomerId())){
                        r.addError("multiple receiptNo doesn't belong to one customer");
                    }
                }
                if(o.getDeliveredDate() == null){
                    r.addError(o.getReceiptNo()+" not yet delivered");
                }
                p.setOrderId(o.getId());
                p.setReceiptNo(o.getReceiptNo());
                if(paymentAmount.compareTo(o.getTotal()) == -1){
                    p.setValue(paymentAmount);
                    p.setPartial(true);
                }else{
                    p.setValue(o.getTotal());
                    paymentAmount = paymentAmount.subtract(p.getValue());
                }
                receiptNos.add(p);
            }
        }
    }

    public void afterSave(SaveResult r) {
        super.afterSave(r);
        CouchDbClient dbClient = CouchDBUtil.getDbClient(Order.class);
        for (PaymentRecord record : receiptNos) {
            Order o = dbClient.find(Order.class, record.getOrderId());
            OrderPayment p = new OrderPayment();
            p.setPaymentNo(getPaymentNo());
            p.setValue(record.getValue());
            if(this.getBouncedDate() == null) {
                o.addPayments(getId(), p);
            }else{
                o.removePayments(getId());
            }
            dbClient.update(o);
        }
        Customer.computeOrderStats(customerId);
    }
}

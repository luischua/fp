package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.lightcouch.CouchDbClient;
import util.CouchDBUtil;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class Customer extends CouchDocument {
    private String name;
    private String address;
    private String telephone;
    private String contact;
    private int terms;
    private String companyId;
    private String truckingId;
    private String truckingName;
    private String agentId;
    private String agentName;
    private OrderStats orderStats;

    public Company findCompany(){
        CouchDbClient client = CouchDBUtil.getDbClient(Company.class);
        return client.find(Company.class, companyId);
    }

    public void beforeSave() {
        truckingId = getDBId(truckingName, Trucking.class);
        agentId = getDBId(agentName, Agent.class);
    }

    public static void computeOrderStats(String customerId){
        List<Order> orders = (List<Order>)CouchDocument.retreiveByFkId(customerId, Order.class, "Order/byCustomerId");
        OrderStats stats = new OrderStats();
        for(Order order: orders){
            if(order.getCanceledDate() != null){
                stats.getCancelled().addTotal(order.getTotal(), order.getReceiptNo());
            } else if(order.getPaidDate() != null) {
                stats.getPaid().addTotal(order.getTotal(), order.getReceiptNo());
            } else if(order.getDeliveredDate() != null) {
                stats.getToBeCollected().addTotal(order.getTotal(), order.getReceiptNo());
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

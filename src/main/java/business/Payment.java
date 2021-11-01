package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class Payment extends CouchDocument{
    List<PaymentRecord> orderList;
    private String checkNo;
    private String bank;
    private BigDecimal value;
}

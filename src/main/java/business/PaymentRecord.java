package business;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRecord {
    private String orderId;
    private long receiptNo;
    private BigDecimal value;
    private boolean partial;
}

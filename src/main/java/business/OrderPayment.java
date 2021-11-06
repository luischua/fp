package business;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class OrderPayment {
    private long paymentNo;
    private BigDecimal value;
}

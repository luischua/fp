package business;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class OrderStatsDetail {
    private BigDecimal total = new BigDecimal(0);
    private int count = 0;
    private List<Long> receiptNos = new ArrayList<Long>();

    public void addTotal(BigDecimal b, long receiptNo){
        total = total.add(b);
        receiptNos.add(receiptNo);
        count++;
    }
}

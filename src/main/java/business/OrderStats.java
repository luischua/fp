package business;

import lombok.Data;

@Data
public class OrderStats {
    private OrderStatsDetail toBeDelivered = new OrderStatsDetail();
    private OrderStatsDetail toBeCollected = new OrderStatsDetail();
    private OrderStatsDetail paid = new OrderStatsDetail();
    private OrderStatsDetail cancelled = new OrderStatsDetail();
}

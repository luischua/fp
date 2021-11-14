package business;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.lightcouch.CouchDbClient;
import util.CouchDBUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString(callSuper=true)
public class MonthlySales extends CouchDocument {
    private int yearMonth;
    private int totalOrder;
    private LocalDateTime generatedDateTime = LocalDateTime.now();
    private BigDecimal totalValue = new BigDecimal(0);
    private Map<String, ProductSales> productSales;

    public List<ProductSales> getOrderedProductSales(){
        return productSales.values().stream()
                .sorted(Comparator.comparing(ProductSales::getQuantity).reversed())
                .collect(Collectors.toList());
    }

    public MonthlySales(){
        CouchDbClient dbClient = CouchDBUtil.getDbClient(Product.class);
        productSales = new HashMap<String, ProductSales>();
        for(Product p : dbClient.view("_all_docs").query(Product.class)){
            ProductSales sales = new ProductSales();
            sales.setName(p.getName());
            productSales.put(p.getId(), sales);
        }
    }

    public void computeSales(List<Order> orderList){
        totalOrder =  orderList.size();
        for(Order order: orderList){
            totalValue = totalValue.add(order.getTotal());
            for(ProductRecord r : order.getProducts()){
                ProductSales sales = productSales.get(r.getProductId());
                if(sales == null){
                    sales = new ProductSales();
                    sales.setName(r.getName());
                    productSales.put(r.getProductId(), sales);
                }
                sales.addQty(r.getQuantity());
            }
            for(ProductRecord r : order.getPromo()){
                ProductSales sales = productSales.get(r.getProductId());
                sales.addQty(r.getQuantity());
            }
        }
    }
}

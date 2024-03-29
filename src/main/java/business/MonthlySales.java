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
    private int totalUnitsSold = 0;
    private BigDecimal totalValue = new BigDecimal(0);
    private Map<String, ProductSales> productSales;

    public List<ProductSales> getOrderedProductSales(){
        if(productSales == null){
            return null;
        }
        return productSales.values().stream()
                .sorted(Comparator.comparing(ProductSales::getQuantity).reversed())
                .collect(Collectors.toList());
    }

    public void computeSales(List<Order> orderList){
        setLastEdited(LocalDateTime.now());
        setNarrative("gen at "+getLastEditedString());
        System.out.println("Starting computing sales: "+yearMonth);
        CouchDbClient dbClient = CouchDBUtil.getDbClient(Product.class);

        productSales = new HashMap<String, ProductSales>();
        for(Product p : dbClient.view("_all_docs").includeDocs(true).query(Product.class)){
            ProductSales sales = new ProductSales();
            sales.setName(p.getName());
            productSales.put(p.getId(), sales);
        }

        totalOrder =  orderList.size();
        totalValue = new BigDecimal(0);
        totalUnitsSold = 0;
        for(Order order: orderList){
            totalValue = totalValue.add(order.getTotal());
            if(order.getProducts() != null) {
                for (ProductRecord r : order.getProducts()) {
                    ProductSales sales = productSales.get(r.getProductId());
                    if (sales == null) {
                        sales = new ProductSales();
                        sales.setName(r.getName());
                        productSales.put(r.getProductId(), sales);
                    }
                    sales.addQty(r.getQuantity());
                    totalUnitsSold += r.getQuantity();
                }
            }
            if(order.getPromo() != null) {
                for (ProductRecord r : order.getPromo()) {
                    ProductSales sales = productSales.get(r.getProductId());
                    sales.addQty(r.getQuantity());
                    totalUnitsSold += r.getQuantity();
                }
            }
        }
        System.out.println("Total Order: "+totalOrder);
        System.out.println("Product Sales: "+productSales);
    }
}

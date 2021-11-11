package business;

import lombok.Data;

@Data
public class ProductSales {
    private String name;
    private int quantity = 0;

    public void addQty(int q){
        quantity += q;
    }
}

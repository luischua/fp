package util;

import business.Customer;
import business.Order;
import business.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TestCaseUtil {
    public static final Customer CUSTOMER;
    public static final Order ORDER;
    static {
        CUSTOMER = new Customer();
        CUSTOMER.setName("Trisha MP & Accessories");
        CUSTOMER.setAddress("Ugong Valenzuela City");
        CUSTOMER.setTelephone("09773147249");
        ORDER = new Order();
        ORDER.setReceiptNo(20001);
        ORDER.setCreateTime(LocalDateTime.now());
        Product product = new Product();
        product.setName("Shogun");
        product.setPrice(new BigDecimal(36));
        ORDER.addProductRecord(product, 125);
        product.setName("CB110");
        product.setPrice(new BigDecimal(32.75));
        ORDER.addProductRecord(product, 12);
        product.setName("BEAT");
        ORDER.addProductRecord(product, 3, true);
        product.setName("TRINITY");
        ORDER.addProductRecord(product, 4, true);
    }
}

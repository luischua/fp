package util;

import business.Customer;
import business.Discount;
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
        ORDER.setCustomer(CUSTOMER);
        ORDER.setCreateTime(LocalDateTime.now());
        Product product1 = new Product();
        product1.setName("Shogun");
        product1.setPrice(new BigDecimal(36));
        ORDER.addProductRecord(product1, 125);
        Product product2 = new Product();
        product2.setName("CB110");
        product2.setPrice(new BigDecimal(32.75));
        Discount d = new Discount();
        d.setName("5%-3%");
        product2.addCustomerDiscount(CUSTOMER, d);
        ORDER.addProductRecord(product2, 12);
        Product product = new Product();
        product.setName("BEAT");
        ORDER.addProductRecord(product, 3, true);
        product.setName("TRINITY");
        ORDER.addProductRecord(product, 4, true);
    }
}

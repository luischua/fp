package business;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.TestCaseUtil;

import java.math.BigDecimal;

public class OrderTest {
    @Test
    public void testComputation() {
        Assertions.assertEquals(TestCaseUtil.ORDER.getTotal(), new BigDecimal("4862.15"));
    }

    @Test
    public void testDiscount() {
        Assertions.assertEquals(Discount.calculateDiscount("5%-5%-5%"), new BigDecimal("0.857375"));
    }
}

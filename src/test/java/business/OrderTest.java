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
        Discount d = new Discount();
        d.setName("5%-5%-5%");
        Assertions.assertEquals(d.getValue(), new BigDecimal("0.857375"));
    }
}

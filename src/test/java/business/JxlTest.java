package business;

import util.TestCaseUtil;
import util.UtilTest;
import org.junit.jupiter.api.Test;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class JxlTest {
    @Test
    public void testWriteOrder() throws Exception{
        try(InputStream is = UtilTest.getTestPathInputStream("jxl_template.xlsx")) {
            try (OutputStream os = new FileOutputStream(new File("jxl_output.xlsx"))) {
                Context context = new Context();
                context.putVar("customer",  TestCaseUtil.CUSTOMER);
                context.putVar("order",  TestCaseUtil.ORDER);
                JxlsHelper.getInstance().processTemplate(is, os, context);
            }
        }
    }
}

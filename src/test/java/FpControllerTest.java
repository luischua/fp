
import app.controller.FpController;
import model.Person;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

public class FpControllerTest {

    FpController controller = new FpController();

    @Test
    public void testController() throws Exception {
        MockMultipartFile fingerprint =
                new MockMultipartFile(
                        "fingerprint",
                        "probe.tif",
                        null,
                        UtilTest.getTestPath("probe.tif"));
        MockMultipartFile idPic =
                new MockMultipartFile(
                        "idpic",
                        "logo.jpg",
                        null,
                        UtilTest.getTestPath("logo.jpg"));
        byte[] qrCodeByte = controller.register(fingerprint, idPic, "test", null);
        Person p = controller.findByFingerprint(fingerprint);
        Assert.assertTrue(p != null);
        Assert.assertTrue(controller.verify(fingerprint, p.getId()));
        Assert.assertArrayEquals(qrCodeByte, controller.getQrCode(p.getId()));
        MockMultipartFile qrCode =
                new MockMultipartFile(
                        "qrcode",
                        "qr.png",
                        null,
                        qrCodeByte);
        Assert.assertEquals(p.getId(), controller.findByQrCode(qrCode).getId());
        Assert.assertEquals(p.getId(), controller.findById(p.getId()).getId());
        //Assert.assertEquals(p.getId(), controller.findByName("test")[0].getId());
    }
}


import app.controller.FpController;
import model.Fingerprint;
import model.Person;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.mock.web.MockMultipartFile;
import util.CouchDBUtil;

import java.time.LocalDate;

public class FpControllerTest {

    @Test
    public void testController() throws Exception {
        FpController controller = new FpController();
        controller.newTestInstance();
        MockMultipartFile fingerprint =
                new MockMultipartFile(
                        "fingerprint",
                        UtilTest.USER_12_SAMPLE_1,
                        null,
                        UtilTest.getTestPathByte(UtilTest.USER_12_SAMPLE_1));
        Person p = controller.register(fingerprint, UtilTest.getIdPic(), "test", LocalDate.of(1985,6,15));
        byte[] qrCodeByte = p.getQrImage();
        Assertions.assertTrue(controller.verify(fingerprint, p.getId()));
        Assertions.assertArrayEquals(qrCodeByte, controller.getQrCode(p.getId()));
        MockMultipartFile qrCode =
                new MockMultipartFile(
                        "qrcode",
                        "qr.png",
                        null,
                        qrCodeByte);
        Assertions.assertEquals(p.getId(), controller.findByQrCode(qrCode).getId());
        Assertions.assertEquals(p.getId(), controller.findById(p.getId()).getId());
        //Assert.assertEquals(p.getId(), controller.findByName("test")[0].getId());

        //remove added records after testing
        CouchDBUtil.getDbClient("person").remove(Person.find(p.getId()));
        CouchDBUtil.getDbClient("fingerprint").remove(Fingerprint.find(p.getId()));
    }
}

import app.controller.FpController;
import model.Fingerprint;
import model.Person;
import model.Registration;
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
        MockMultipartFile fingerprint = UtilTest.getFingerprintPic();
        Registration registration = controller.register(fingerprint, UtilTest.getIdPic(), "test", LocalDate.of(1985, 6, 15));
        Person p = registration.getPerson();
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
        CouchDBUtil.getDbClient(Person.class).remove(Person.find(p.getId()));
        CouchDBUtil.getDbClient(Fingerprint.class).remove(Fingerprint.find(p.getId()));
    }

    @Test
    public void testControllerIdPicFailure() throws Exception {
        FpController controller = new FpController();
        controller.newTestInstance();
        MockMultipartFile fingerprint = UtilTest.getFingerprintPic();
        Registration registration = controller.register(fingerprint, UtilTest.getIdPic(UtilTest.NO_FACE_SAMPLE), "test", LocalDate.of(1985, 6, 15));
        Assertions.assertEquals(Registration.NO_FACE, registration.getError());
        registration = controller.register(fingerprint, UtilTest.getIdPic(UtilTest.MULTIPLE_FACE_SAMPLE), "test", LocalDate.of(1985, 6, 15));
        Assertions.assertEquals(Registration.MULTIPLE_FACE, registration.getError());
    }

    @Test
    public void testControllerFingerprintFailure() throws Exception {
        FpController controller = new FpController();
        controller.newTestInstance();
        MockMultipartFile fingerprint = UtilTest.getFingerprintPic(UtilTest.ID_SAMPLE);
        Registration registration = controller.register(fingerprint, UtilTest.getIdPic(), "test", LocalDate.of(1985, 6, 15));
        Assertions.assertEquals(Registration.FINGERPRINT_INVALID, registration.getError());
    }
}
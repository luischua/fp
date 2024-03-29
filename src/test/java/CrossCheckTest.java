import app.controller.FpController;
import model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import util.CouchDBUtil;
import util.CrossCheckMain;
import util.UtilTest;

import java.util.List;

public class CrossCheckTest {

    @Test
    public void testCrossCheckViaController() throws Exception{

        FpController controller = new FpController();
        controller.newTestInstance();
        MockMultipartFile fingerprint =
                new MockMultipartFile(
                        "fingerprint",
                        UtilTest.USER_12_SAMPLE_1,
                        null,
                        UtilTest.getTestPathByte(UtilTest.USER_12_SAMPLE_1));

        Registration registration = controller.register(fingerprint, UtilTest.getIdPic(), "test 1", null);
        Person registeredPerson = registration.getPerson();
        Fingerprint f = new Fingerprint(UtilTest.getTestPathByte(UtilTest.USER_12_SAMPLE_1));
        List<VerificationResult> people = Fingerprint.crosscheckTemplate(f);
        Assertions.assertTrue(people.size() > 0);

        f = new Fingerprint(UtilTest.getTestPathByte(UtilTest.USER_13_SAMPLE_1));
        people = Fingerprint.crosscheckTemplate(f);
        Assertions.assertTrue(people.size() == 0);

        Thread.sleep(CrossCheckMain.SLEEP_DURATION);
        Assertions.assertEquals(VerificationStatus.SUCCESS, Person.find(registeredPerson.getId()).getVerifiedStatus());
        Registration failedRegistration = controller.register(fingerprint, UtilTest.getIdPic(), "test 2", null);
        Person failedRegisteredPerson = failedRegistration.getPerson();
        Thread.sleep(CrossCheckMain.SLEEP_DURATION);
        Assertions.assertEquals(VerificationStatus.FAIL, Person.find(failedRegisteredPerson.getId()).getVerifiedStatus());

        //remove added records after testing
        CouchDBUtil.getDbClient(Person.class).remove(Person.find(registeredPerson.getId()));
        CouchDBUtil.getDbClient(Fingerprint.class).remove(Fingerprint.find(registeredPerson.getId()));
        CouchDBUtil.getDbClient(Person.class).remove(Person.find(failedRegisteredPerson.getId()));
        CouchDBUtil.getDbClient(Fingerprint.class).remove(Fingerprint.find(failedRegisteredPerson.getId()));
    }
}

import app.controller.FpController;
import model.Fingerprint;
import model.Person;
import model.VerificationStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import util.CouchDBUtil;
import util.CrossCheckMain;

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

        Person registeredPerson = controller.register(fingerprint, UtilTest.getIdPic(), "test 1", null);
        List<String> people = Person.crosscheck(UtilTest.getTestPathByte(UtilTest.USER_12_SAMPLE_1));
        Assertions.assertTrue(people.size() > 0);
        people = Person.crosscheck(UtilTest.getTestPathByte(UtilTest.USER_13_SAMPLE_1));
        Assertions.assertTrue(people.size() == 0);

        Thread.sleep(CrossCheckMain.SLEEP_DURATION);
        Assertions.assertEquals(VerificationStatus.SUCCESS, Person.find(registeredPerson.getId()).getVerifiedStatus());
        Person failedRegisteredPerson =controller.register(fingerprint, UtilTest.getIdPic(), "test 2", null);

        Thread.sleep(CrossCheckMain.SLEEP_DURATION);
        Assertions.assertEquals(VerificationStatus.FAIL, Person.find(failedRegisteredPerson.getId()).getVerifiedStatus());

        //remove added records after testing
        CouchDBUtil.getDbClient("person").remove(Person.find(registeredPerson.getId()));
        CouchDBUtil.getDbClient("fingerprint").remove(Fingerprint.find(registeredPerson.getId()));
        CouchDBUtil.getDbClient("person").remove(Person.find(failedRegisteredPerson.getId()));
        CouchDBUtil.getDbClient("fingerprint").remove(Fingerprint.find(failedRegisteredPerson.getId()));
    }
}

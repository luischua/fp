
import util.FingerprintAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FingerprintTest{
    @Test
    public void testSourceAFIS(){
        try {
            byte[] probeImage =  UtilTest.getTestPathByte(UtilTest.USER_12_SAMPLE_1);
            byte[] candidateImage = UtilTest.getTestPathByte(UtilTest.USER_12_SAMPLE_2);
            double score = FingerprintAnalyzer.getScore(probeImage, candidateImage);
            Assertions.assertTrue(score > UtilTest.SCORE_THRESHOLD);
        }catch (Exception e){
            System.out.println(e);
            Assertions.fail();
        }
    }


}
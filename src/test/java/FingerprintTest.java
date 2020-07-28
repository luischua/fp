
import util.FingerprintAnalyzer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FingerprintTest{
    @Test
    public void testSourceAFIS(){
        try {
            byte[] probeImageTemplate =  UtilTest.getTestTemplateByte(UtilTest.USER_12_SAMPLE_1);
            byte[] candidateImageTemplate = UtilTest.getTestTemplateByte(UtilTest.USER_12_SAMPLE_2);
            double score = FingerprintAnalyzer.getScoreComparingCachedTemplate(probeImageTemplate, candidateImageTemplate);
            Assertions.assertTrue(score > UtilTest.SCORE_THRESHOLD);
        }catch (Exception e){
            System.out.println(e);
            Assertions.fail();
        }
    }


}
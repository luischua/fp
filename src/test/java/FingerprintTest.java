
import util.FingerprintAnalyzer;
import org.junit.Assert;
import org.junit.Test;

public class FingerprintTest{
    @Test
    public void testSourceAFIS(){
        try {
            byte[] probeImage =  UtilTest.getTestPath("probe.tif");
            byte[] candidateImage = UtilTest.getTestPath("candidate.tif");
            double score = FingerprintAnalyzer.getScore(probeImage, candidateImage);
            double threshold = 40;
            Assert.assertTrue(score > threshold);
        }catch (Exception e){
            System.out.println(e);
            Assert.fail();
        }
    }


}
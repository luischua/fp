package util;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

public class FingerprintAnalyzer {

    public static final double MATCH_THRESHOLD = 40;
    public static final double NOT_A_FINGERPRINT_THRESHOLD = 2;

    private FingerprintMatcher matcher;

    public FingerprintAnalyzer(byte[] probeTemplate){
        FingerprintTemplate probe = new FingerprintTemplate(probeTemplate);
        matcher = new FingerprintMatcher().index(probe);
    }

    //cached template comparison faster
    public double getScore(byte[] candidateTemplate) {
        FingerprintTemplate candidate = new FingerprintTemplate(candidateTemplate);
        return matcher.match(candidate);
    }

    public static byte[] getTemplateByte(byte[] imageByte){
        return new FingerprintTemplate(
                new FingerprintImage()
                        .dpi(500)
                        .decode(imageByte)).toByteArray();
    }
}

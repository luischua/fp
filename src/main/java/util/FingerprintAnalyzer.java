package util;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

public class FingerprintAnalyzer {


    public static FingerprintTemplate getFingerprintTemplate(byte[] imageByte) {
        return new FingerprintTemplate(
                new FingerprintImage()
                        .dpi(500)
                        .decode(imageByte));
    }

    //cached template comparison which is faster
    public static double getScoreComparingCachedTemplate(byte[] probeTemplate, byte[] candidateTemplate) {
        FingerprintTemplate probe = getCachedFingerprintTemplate(probeTemplate);
        FingerprintTemplate candidate = getCachedFingerprintTemplate(candidateTemplate);
        return new FingerprintMatcher()
                .index(probe)
                .match(candidate);
    }

    public static FingerprintTemplate getCachedFingerprintTemplate(byte[] templateByte) {
        return new FingerprintTemplate(templateByte);
    }

    public static byte[] getTemplateByte(byte[] imageByte){
        return getFingerprintTemplate(imageByte).toByteArray();
    }
}

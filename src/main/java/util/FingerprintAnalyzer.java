package util;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

public class FingerprintAnalyzer {
    public static double getScore(byte[] probeImage, byte[] candidateImage) {
        FingerprintTemplate probe = getFingerprintTemplate(probeImage);
        FingerprintTemplate candidate = getFingerprintTemplate(candidateImage);
        return new FingerprintMatcher()
                .index(probe)
                .match(candidate);
    }

    public static FingerprintTemplate getFingerprintTemplate(byte[] image) {
        return new FingerprintTemplate(
                new FingerprintImage()
                        .dpi(500)
                        .decode(image));
    }
}

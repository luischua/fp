package model;

import org.apache.commons.codec.binary.Base64;
import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Registration {
    private Person person;
    private String error;
    private List<String> faces;
    public static final String MISSING_TEST_FINGERPRINT = "Test Fingerprint on server is missing";
    public static final String FINGERPRINT_INVALID = "Provided image is not a fingerprint";
    public static final String NO_FACE = "No Face Detected";
    public static final String MULTIPLE_FACE = "Multiple Face Detected";
    public void add(List<byte[]> facesList){
        for(byte[] b : facesList){
            faces.add(Base64.encodeBase64String(b));
        }
    }
}

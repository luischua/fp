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
    public void add(List<byte[]> facesList){
        for(byte[] b : facesList){
            faces.add(Base64.encodeBase64String(b));
        }
    }
}

import org.apache.commons.codec.binary.Base64;
import org.springframework.mock.web.MockMultipartFile;
import util.FingerprintAnalyzer;

import java.io.InputStream;
import java.util.Scanner;

public class UtilTest {

    public static final String USER_12_SAMPLE_1 = "012_1_1.tif";
    public static final String USER_12_SAMPLE_2 = "012_1_2.tif";
    public static final String USER_13_SAMPLE_1 = "013_1_1.tif";
    public static final String USER_13_SAMPLE_2 = "013_1_2.tif";
    public static final String ID_SAMPLE = "lady.jpg";
    public static final String NO_FACE_SAMPLE = "logo.jpg";
    public static final String MULTIPLE_FACE_SAMPLE = "face.jpg";

    public static double SCORE_THRESHOLD = 40;

    public static MockMultipartFile getIdPic() throws Exception{
        return getMultipart("idpic", UtilTest.ID_SAMPLE);
    }

    public static MockMultipartFile getIdPic(String filename) throws Exception{
        return getMultipart("idpic", filename);
    }

    public static MockMultipartFile getFingerprintPic() throws Exception{
        return getMultipart("fingerprint", UtilTest.USER_12_SAMPLE_1);
    }

    public static MockMultipartFile getFingerprintPic(String filename) throws Exception{
        return getMultipart("fingerprint", filename);
    }

    public static MockMultipartFile getMultipart(String name, String filename) throws Exception{
        return new MockMultipartFile(
                name,
                filename,
                null,
                UtilTest.getTestPathByte(filename));
    }

    public static byte[] getTestPathByte(String url) throws Exception{
        return UtilTest.getTestPathInputStream(url).readAllBytes();
    }

    public static byte[] getTestTemplateByte(String url) throws Exception{
        return FingerprintAnalyzer.getTemplateByte(getTestPathByte(url));
    }

    public static InputStream getTestPathInputStream(String url) throws Exception{
        return UtilTest.class.getClassLoader().getResourceAsStream(url);
    }

    public static String getTestPathBase64(String url) throws Exception{
        return Base64.encodeBase64String(getTestPathInputStream(url).readAllBytes());
    }

    public static String geTestPathString(String url) throws Exception {
        Scanner sc = new Scanner(getTestPathInputStream(url));
        StringBuffer sb = new StringBuffer();
        while(sc.hasNext()){
            sb.append(sc.nextLine());
        }
        return sb.toString();
    }
}

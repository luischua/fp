import org.apache.commons.codec.binary.Base64;
import org.springframework.mock.web.MockMultipartFile;
import util.FingerprintAnalyzer;

import java.io.InputStream;
import java.util.Scanner;

public class UtilTest {

    public static String USER_12_SAMPLE_1 = "012_1_1.tif";
    public static String USER_12_SAMPLE_2 = "012_1_2.tif";
    public static String USER_13_SAMPLE_1 = "013_1_1.tif";
    public static String USER_13_SAMPLE_2 = "013_1_2.tif";
    public static String ID_SAMPLE = "logo.jpg";

    public static double SCORE_THRESHOLD = 40;

    public static MockMultipartFile getIdPic() throws Exception{
        return new MockMultipartFile(
                        "idpic",
                        UtilTest.ID_SAMPLE,
                        null,
                        UtilTest.getTestPathByte(UtilTest.ID_SAMPLE));
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

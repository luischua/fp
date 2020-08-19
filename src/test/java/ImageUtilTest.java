import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import util.ImageUtil;
import java.util.List;

import java.io.FileInputStream;

public class ImageUtilTest {
    @Test
    public void testFaceRecognition() throws Exception{
        List<byte[]> imageList = ImageUtil.getFace(UtilTest.getTestPathInputStream("face.jpg"));
        Assertions.assertTrue(imageList.size() == 2);
    }
}

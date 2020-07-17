public class UtilTest {
    public static byte[] getTestPath(String url) throws Exception{
        return UtilTest.class.getClassLoader().getResourceAsStream(url).readAllBytes();
    }
}

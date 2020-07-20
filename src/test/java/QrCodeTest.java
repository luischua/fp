import model.QrCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class QrCodeTest {
    @Test
    public void testZXing() {

        String id = "60063955ff3b45cd910fced46237d319";
        String filePath = "QR.png";
        try {
            QrCode.writeQrCode(id, "QR.png", 250);
            String decodedId = QrCode.readQRCode(filePath);
            Assertions.assertEquals(id, decodedId);
        } catch (Exception e) {
            System.out.println(e);
            Assertions.fail();
        }
        System.out.println("\n\nYou have successfully created QR Code.");
    }


}




package model;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.bind.annotation.PathVariable;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class QrCode {

    public static byte[] generateQrCodeByte(String id) throws Exception{
        String qrFilePath = id+".png";
        QrCode.writeQrCode(id, qrFilePath,250);
        return IOUtils.toByteArray(new FileInputStream(qrFilePath));
    }

    public static void writeQrCode(String myCodeText, String filePath, int size) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(myCodeText, BarcodeFormat.QR_CODE, size,
                size, getHintMap());
        BufferedImage image = writeImage(byteMatrix);
        String extension = FilenameUtils.getExtension(filePath);
        ImageIO.write(image, extension, new File(filePath));
    }

    public static String readQRCode(String filePath)
            throws FileNotFoundException, IOException, NotFoundException{
        return readQRCode(new FileInputStream(filePath));
    }

    public static String readQRCode(InputStream input)
            throws FileNotFoundException, IOException, NotFoundException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                new BufferedImageLuminanceSource(
                        ImageIO.read(input))));
        Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap);
        return qrCodeResult.getText();
    }

    private static Map<EncodeHintType, Object> getHintMap() {
        Map<EncodeHintType, Object> hintMap = new HashMap<EncodeHintType, Object>();
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // Now with zxing version 3.2.1 you could change border size (white border size to just 1)
        hintMap.put(EncodeHintType.MARGIN, 1); /* default = 4 */
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        return hintMap;
    }

    private static BufferedImage writeImage(BitMatrix byteMatrix) {
        int width = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(width, width,
                BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, width);
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < width; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }
        return image;
    }
}

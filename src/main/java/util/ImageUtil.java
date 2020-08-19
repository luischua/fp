package util;

import net.coobird.thumbnailator.Thumbnails;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;

public class ImageUtil{
    public static final int WIDTH = 50;
    public static final int HEIGHT = 50;
    private static final HaarCascadeDetector detector = new HaarCascadeDetector();
    public static byte[] getResizeImage(InputStream input) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Thumbnails.of(input).size(WIDTH, HEIGHT).toOutputStream(outputStream);
        byte[] image = outputStream.toByteArray();
        outputStream.close();
        return image;
    }

    public static boolean hasFace(InputStream input) throws Exception{
        FImage inputImage = ImageUtilities.readF(input);
        List < DetectedFace > faces = detector.detectFaces(inputImage);
        if(faces.size() > 0){
            return true;
        }
        return false;
    }

    public static List<byte[]> getFace(InputStream input) throws Exception
    {
        FImage inputImage = ImageUtilities.readF(input);
        List<byte[]> imageList = new ArrayList<byte[]>();
        for(DetectedFace face : detector.detectFaces(inputImage)){
            FImage faceImage = face.getFacePatch();
            imageList.add(faceImage.toByteImage());
        }
        return imageList;
    }
}
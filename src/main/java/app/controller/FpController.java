package app.controller;

import app.exception.RequestContext;
import model.*;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import util.CrossCheckMain;

import javax.servlet.http.HttpSession;;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import net.coobird.thumbnailator.Thumbnails;
import util.ImageUtil;

@RestController
public class FpController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private RequestContext requestContext;

    public void newTestInstance(){
        requestContext = new RequestContext();
    }

    @PostMapping("/register")
    public Registration register(@RequestParam MultipartFile fingerprint,
                    @RequestParam("idpic") MultipartFile idPic,
                    @RequestParam String name,
                    @RequestParam(value = "birthdate", required=false) LocalDate birthDate) throws Exception{
        requestContext.setClassMethod("FpController.register");
        Registration r = new Registration();
        if(!Fingerprint.checkIfTestFingerprintExist()){
            r.setError("Test Fingerprint on server is missing");
            return r;
        }
        Fingerprint fp = new Fingerprint(fingerprint.getBytes());
        if(!fp.checkIfValidFingerprint()){
            r.setError("Provided image is not a fingerprint");
            return r;
        }
        String idExtension = FilenameUtils.getExtension(idPic.getOriginalFilename());
        ByteArrayInputStream bais = new ByteArrayInputStream(idPic.getInputStream().readAllBytes());
        List<byte[]> faceList = ImageUtil.getFace(bais);
        if(faceList.size() == 0) {
            r.setError("No Face Detected");
            return r;
        }
        if(faceList.size() > 1) {
            r.setError("Multiple Face Detected");
            //r.add(faceList);
            return r;
        }
        bais.reset();
        byte[] resizedImage = ImageUtil.getResizeImage(bais);
        fp.save();
        String id = fp.getId();
        Person createdPerson = new Person();
        createdPerson.setId(id);
        createdPerson.setIdImage(resizedImage, idExtension);
        createdPerson.setName(name);
        createdPerson.setBirthDate(birthDate);
        createdPerson.setQrImage(QrCode.generateQrCodeByte(id));
        createdPerson.save("luis");
        CrossCheckMain.getInstance().addId(fp.getId());
        r.setPerson(createdPerson);
        return r;
    }

    @GetMapping(
            value = "/qrcode/{id}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody
    byte[] getQrCode(@PathVariable String id) throws Exception{
        requestContext.setClassMethod("FpController.getQrCode");
        return Person.find(id).getQrImage();
    }

    @GetMapping("/person/{id}")
    public Person findById(@PathVariable String id){
        requestContext.setClassMethod("FpController.findById");
        return Person.find(id);
    }

    /* to be implemented */
    @GetMapping("/search/{name}")
    public List<Person> findByName(@PathVariable String name){
        requestContext.setClassMethod("FpController.searchByName");
        return Person.findByName(name);
    }

    @PostMapping("/person")
    public Person findByQrCode( @RequestParam("qrcode") MultipartFile qrCode) throws Exception{
        requestContext.setClassMethod("FpController.findByQrCode");
        String id = QrCode.readQRCode(qrCode.getInputStream());
        return findById(id);
    }

    @PostMapping("/verify")
    public boolean verify(@RequestParam MultipartFile fingerprint,
                               @RequestParam String id) throws Exception{
        requestContext.setClassMethod("FpController.verify");
        Fingerprint fp = Fingerprint.find(id);
        return fp.match(fingerprint.getBytes());
    }
}

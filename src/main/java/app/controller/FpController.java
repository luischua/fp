package app.controller;

import app.exception.RequestContext;
import model.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import util.CrossCheckMain;

import javax.servlet.http.HttpSession;;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicLong;


@RestController
public class FpController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private RequestContext requestContext;

    @PostMapping(
            value = "/register",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody
    byte[] register(@RequestParam MultipartFile fingerprint,
                    @RequestParam("idpic") MultipartFile idPic,
                    @RequestParam String name,
                    @RequestParam(value = "birthdate", required=false) LocalDate birthDate) throws Exception{
        requestContext.setClassMethod("FpController.register");
        Fingerprint fp = new Fingerprint();
        fp.setImage(fingerprint.getBytes());
        fp.save();
        Person createdPerson = new Person();
        createdPerson.setId(fp.getId());
        String idExtension = FilenameUtils.getExtension(idPic.getOriginalFilename());
        createdPerson.setBase64IdImage(Base64.encodeBase64String(idPic.getBytes()), idExtension);
        createdPerson.setName(name);
        createdPerson.setBirthDate(birthDate);
        createdPerson.save("luis");
        CrossCheckMain.getInstance().addId(fp.getId());
        System.out.println("BirthDate"+birthDate);
        System.out.println("Age"+ createdPerson.getAge());
        System.out.println("ID filename"+ idPic.getOriginalFilename());
        return getQrCode(fp.getId());
    }

    @GetMapping(
            value = "/qrcode/{id}",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody
    byte[] getQrCode(@PathVariable String id) throws Exception{
        String qrFilePath = id+".png";
        QrCode.writeQrCode(id, qrFilePath,250);
        return IOUtils.toByteArray(new FileInputStream(qrFilePath));
    }

    @GetMapping("/person/{id}")
    public Person findById(@PathVariable String id){
        return Person.find(id);
    }

    /* to be implemented */
    @GetMapping("/search/{name}")
    public Person[] findByName(@PathVariable String name){
        return null;
    }

    /* to be implemented */
    @PostMapping("/search")
    public Person findByFingerprint(@RequestParam MultipartFile fingerprint) throws Exception{
        return Person.crosscheck(fingerprint.getBytes());
    }

    @PostMapping("/person")
    public Person findByQrCode( @RequestParam("qrcode") MultipartFile qrCode) throws Exception{
       String id = QrCode.readQRCode(qrCode.getInputStream());
       return findById(id);
    }

    @PostMapping("/verify")
    public boolean verify(@RequestParam MultipartFile fingerprint,
                               @RequestParam String id) throws Exception{
        requestContext.setClassMethod("FpController.verify");
        Fingerprint fp = Fingerprint.find(id);
        byte[] fingerprintBytes = fingerprint.getBytes();
        return fp.match(fingerprintBytes, true);
    }
}

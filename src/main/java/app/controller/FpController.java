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
import java.util.List;

@RestController
public class FpController {

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private RequestContext requestContext;

    public void newTestInstance(){
        requestContext = new RequestContext();
    }

    @PostMapping(
            value = "/register",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public @ResponseBody
    Person register(@RequestParam MultipartFile fingerprint,
                    @RequestParam("idpic") MultipartFile idPic,
                    @RequestParam String name,
                    @RequestParam(value = "birthdate", required=false) LocalDate birthDate) throws Exception{
        requestContext.setClassMethod("FpController.register");
        Fingerprint fp = new Fingerprint();
        fp.setImage(fingerprint.getBytes());
        fp.save();
        String id = fp.getId();
        Person createdPerson = new Person();
        createdPerson.setId(id);
        String idExtension = FilenameUtils.getExtension(idPic.getOriginalFilename());
        createdPerson.setIdImage(idPic.getBytes(), idExtension);
        createdPerson.setName(name);
        createdPerson.setBirthDate(birthDate);
        createdPerson.setQrImage(QrCode.generateQrCodeByte(id));
        createdPerson.save("luis");
        CrossCheckMain.getInstance().addId(fp.getId());
        System.out.println("BirthDate"+birthDate);
        System.out.println("Age"+ createdPerson.getAge());
        System.out.println("ID filename"+ idPic.getOriginalFilename());
        return createdPerson;
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

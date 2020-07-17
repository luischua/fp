import model.Person;
import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;
import org.lightcouch.Attachment;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Params;
import org.lightcouch.Response;
import java.io.InputStream;

public class CouchDbTest {
    @Test
    public void testSave(){
        try {
            CouchDbClient dbClient = new CouchDbClient();
            Person createdPerson = new Person();
            createdPerson.setName("Shaggy");
            String fileName = "photo.tif";
            String data = getTestPathBase64(fileName);
            Attachment attachment = new Attachment(data, "image/tif");
            createdPerson.addAttachment(fileName, attachment);
            Response response = dbClient.save(createdPerson);
            String id = response.getId();
            System.out.println(id);
            Person retrievedPerson = dbClient.find(Person.class, id, new Params().attachments());
            Assert.assertEquals(createdPerson.getName(), retrievedPerson.getName());
            Assert.assertEquals(createdPerson.getAttachments().get(fileName).getData(), retrievedPerson.getAttachments().get(fileName).getData());
            dbClient.shutdown();
        }catch (Exception e){
            System.out.println(e);
            Assert.fail();
        }
    }

    InputStream getTestPathInputStream(String url) throws Exception{
        return getClass().getClassLoader().getResourceAsStream(url);
    }

    String getTestPathBase64(String url) throws Exception{
        return Base64.encodeBase64String(getTestPathInputStream(url).readAllBytes());
    }
}

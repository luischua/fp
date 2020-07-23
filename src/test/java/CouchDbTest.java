import model.Fingerprint;
import model.Person;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.lightcouch.Attachment;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Params;
import org.lightcouch.Response;

import java.io.InputStream;
import java.util.List;

public class CouchDbTest {
    @Test
    public void testSave(){
        try {
            CouchDbClient dbClient = new CouchDbClient("person.properties");
            Person createdPerson = new Person();
            createdPerson.setName("Shaggy");
            String fileName = UtilTest.USER_13_SAMPLE_2;
            String data = UtilTest.getTestPathBase64(fileName);
            Attachment attachment = new Attachment(data, "image/tif");
            createdPerson.addAttachment(fileName, attachment);
            Response response = dbClient.save(createdPerson);
            String id = response.getId();
            String rev = response.getRev();
            System.out.println(id);
            Person retrievedPerson = dbClient.find(Person.class, id, new Params().attachments());
            Assertions.assertEquals(createdPerson.getName(), retrievedPerson.getName());
            Assertions.assertEquals(createdPerson.getAttachments().get(fileName).getData(), retrievedPerson.getAttachments().get(fileName).getData());
            dbClient.remove(id, rev);
            dbClient.shutdown();
        }catch (Exception e){
            System.out.println(e);
            Assertions.fail();
        }
    }

    @Test
    public void testSearchByName() throws Exception{
        Person person1 = new Person();
        person1.setName("Luis Chua");
        person1.save();

        Person person2 = new Person();
        person2.setName("Shaggy");
        person2.save();

        Person person3 = new Person();
        person3.setName("Luis Chua");
        person3.save();

        List<Person> list = Person.findByName("Luis Chua");
        System.out.println(list);
        Assertions.assertTrue(list.size() == 2);
        //dbClient.remove(person1.getId(), person1.getRevision());
        //dbClient.remove(person2.getId(), person2.getRevision());
        //dbClient.remove(person3.getId(), person3.getRevision());
    }

    //@Test
    public void testFind() throws Exception{
        CouchDbClient dbClient = new CouchDbClient("fingerprint.properties");
        String query = UtilTest.geTestPathString("queryDateTimeInLong.js");
        System.out.println("Query:"+query);
        List<Fingerprint> f = dbClient.findDocs(query,Fingerprint.class);
        Assertions.assertTrue(f.size() >= 1);
    }


}

package business;

import app.controller.BusinessController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class HttpSaveTest {

    @Test
    public void testSaveOnBusinessController() {
        //create
        Map<String, String> request = new HashMap<String, String>();
        request.put(BusinessController.TABLE_KEY, "Customer");
        request.put("name", "Luis");
        request.put("address", "QC");
        request.put("terms", "4");
        //next parameters should be ignored
        request.put("revision", "2");
        request.put("narrative", "Ignore");
        BusinessController b = new BusinessController();
        CouchDocument d = b.saveToDb(request).getResult();
        Assertions.assertNotNull(d);

        //modify
        Map<String, String> modifyRequest = new HashMap<String, String>();
        modifyRequest.put(BusinessController.TABLE_KEY, "Customer");
        modifyRequest.put("name", "Luis Edited");
        modifyRequest.put("id", d.getId());
        modifyRequest.put("revision", d.getRevision());
        CouchDocument edited = b.saveToDb(modifyRequest).getResult();
        Assertions.assertNotNull(edited);
    }



    @Test
    public void testlist() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "");
        request.addParameter(BusinessController.TABLE_KEY, "Customer");
        //request.addParameter(BusinessController.ROWS_KEY, "1");
        BusinessController b = new BusinessController();
        List<Customer> list = (List<Customer>)b.list(request);
        for(Customer d : list){
            System.out.println(d);
        }
        Assertions.assertEquals(list.size(), 4);
    }
}

package business;

import app.controller.BusinessController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

public class HttpSaveTest {

    @Test
    public void testSaveOnBusinessController() {
        //create
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "");
        request.addParameter(BusinessController.TABLE_KEY, "Customer");
        request.addParameter("name", "Luis");
        request.addParameter("address", "QC");
        request.addParameter("terms", "4");
        //next parameters should be ignored
        request.addParameter("revision", "2");
        request.addParameter("narrative", "Ignore");
        BusinessController b = new BusinessController();
        CouchDocument d = b.saveToDb(request);
        Assertions.assertNotNull(d);

        //modify
        MockHttpServletRequest modifyRequest = new MockHttpServletRequest("POST", "");
        modifyRequest.addParameter(BusinessController.TABLE_KEY, "Customer");
        modifyRequest.addParameter("name", "Luis Edited");
        modifyRequest.addParameter("id", d.getId());
        modifyRequest.addParameter("revision", d.getRevision());
        CouchDocument edited = b.saveToDb(modifyRequest);
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

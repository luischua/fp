package app.controller;

import app.exception.RequestContext;
import business.CouchDocument;
import business.SaveResult;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import util.BusinessUtil;
import util.CouchDBUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class BusinessController {
    @Autowired
    private HttpSession httpSession;

    @Autowired
    private RequestContext requestContext;

    public static final String TABLE_KEY = "table";
    public static final String PAGE_KEY = "page";
    public static final String ROWS_KEY = "rows";
    public static final String ID_KEY = "id";
    public static final String REVISION_KEY = "revision";
    public static final String NARRATIVE_KEY = "narrative";
    public static final List<String> IGNORE_KEY_LIST = new ArrayList<String>();

    static{
        IGNORE_KEY_LIST.add(TABLE_KEY);
        IGNORE_KEY_LIST.add(ID_KEY);
        IGNORE_KEY_LIST.add(REVISION_KEY);
        IGNORE_KEY_LIST.add(NARRATIVE_KEY);
    }

    @PostMapping("/saveToDb")
    public SaveResult saveToDb(@RequestBody Map<String, Object> request) {
        SaveResult r = new SaveResult();
        Set<String> keys = request.keySet();
        try {
            System.out.println("Paramter set:"+request.keySet());
            String table = (String)request.get(TABLE_KEY);
            String tableProperties = table.concat(".properties");
            String tableClass = "business.".concat(table);
            Class clz = Class.forName(tableClass);
            CouchDbClient dbClient = CouchDBUtil.getDbClient(clz);
            CouchDocument d = null;
            String id = (String)request.get(ID_KEY);
            if(id == null) {
                d = (CouchDocument) clz.getDeclaredConstructor().newInstance();
                d.addNarrative("Created;");
            }else{
                d = (CouchDocument) dbClient.find(clz, id);
                String revision = (String)request.get(REVISION_KEY);
                System.out.println("From DB Revision: "+d.getRevision());
                System.out.println("Passed from HTTP: "+revision);
                if(!revision.equals(d.getRevision())){
                    throw new Exception("Wrong Revision");
                }
                d.addNarrative("Updated by on");
            }
            for (String k : keys) {
                if (!IGNORE_KEY_LIST.contains(k)) {
                    callSetter(d, k, (String)request.get(k));
                }
            }
            System.out.println("Save to DB: " + d);
            Response response;
            if(id == null){
                response = dbClient.save(d);
            }else{
                response = dbClient.update(d);
            }
            d.setId(response.getId());
            d.setRevision(response.getRev());
            r.setResult(d);

        } catch (Exception e) {
            e.printStackTrace();
            r.addError(BusinessUtil.convertStackTraceToString(e));
        }
        return r;
    }

    private void callSetter(Object obj, String fieldName, String value) throws Exception {
        PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
        //System.out.println("Type: "pd.getPropertyType().getTypeName());
        String type = pd.getPropertyType().getTypeName();
        if (type.equals("int")) {
            pd.getWriteMethod().invoke(obj, Integer.parseInt(value));
        } else {
            pd.getWriteMethod().invoke(obj, value);
        }
    }

    @GetMapping("/list")
    public List list(HttpServletRequest request) {
        String table = request.getParameter(TABLE_KEY);
        String tableProperties = table.concat(".properties");
        String page = request.getParameter(PAGE_KEY);
        String rowsString = request.getParameter(ROWS_KEY);
        int rows = 100;
        if(rowsString != null){
            rows = Integer.parseInt(rowsString);
        }
        String tableClass = "business.".concat(table);
        List documentList = null;
        Class clz;
        try{
            clz = Class.forName(tableClass);
            CouchDbClient dbClient = CouchDBUtil.getDbClient(clz);
            documentList =  dbClient.view("_all_docs").queryPage(rows, page, clz).getResultList();
        }catch (Exception e){
            e.printStackTrace();
        }
        return documentList;
    }
}
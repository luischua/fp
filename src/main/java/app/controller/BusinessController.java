package app.controller;

import app.exception.RequestContext;
import business.CouchDocument;
import business.SaveResult;
import org.jsoup.helper.StringUtil;
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
import java.math.BigDecimal;
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
    public SaveResult saveToDb(@RequestBody Map<String, String> request) {
        SaveResult r = new SaveResult();
        Set<String> keys = request.keySet();
        try {
            System.out.println("Paramter set:"+request.keySet());
            String table = request.get(TABLE_KEY);
            if(StringUtil.isBlank(table)){
                r.addError( "table parameter missing");
                return r;
            }
            String tableProperties = table.concat(".properties");
            String tableClass = "business.".concat(table);
            Class clz = Class.forName(tableClass);
            CouchDbClient dbClient = CouchDBUtil.getDbClient(clz);
            CouchDocument d = null;
            String id = request.get(ID_KEY);
            if(StringUtil.isBlank(id)) {
                d = (CouchDocument) clz.getDeclaredConstructor().newInstance();
                d.addNarrative("Created;");
            }else{
                d = (CouchDocument) dbClient.find(clz, id);
                String revision = request.get(REVISION_KEY);
                System.out.println("From DB Revision: "+d.getRevision());
                System.out.println("Passed from HTTP: "+revision);
                if(!revision.equals(d.getRevision())){
                    r.addError( "Data Updated by other user. Form updated to the latest");
                    r.setRevisionError(true);
                    r.setResult(d);
                    return r;
                }else {
                    d.addNarrative("Updated by on");
                }
            }
            for (String k : keys) {
                if (!IGNORE_KEY_LIST.contains(k)) {
                    callSetter(d, k, request.get(k), r);
                }
            }
            System.out.println("Save to DB: " + d);
            Response response;
            if(StringUtil.isBlank(id)){
                response = dbClient.save(d);
            }else{
                response = dbClient.update(d);
            }
            d.setId(response.getId());
            d.setRevision(response.getRev());
            r.setResult(d);

        } catch (Exception e) {
            e.printStackTrace();
            r.setStackTrace(BusinessUtil.convertStackTraceToString(e));
        }
        return r;
    }

    private void callSetter(Object obj, String fieldName, String value, SaveResult r) throws Exception {
        if(value != null) {
            PropertyDescriptor pd = new PropertyDescriptor(fieldName, obj.getClass());
            System.out.println("Field: " + fieldName);
            System.out.println("Type: " + pd.getPropertyType().getTypeName());
            System.out.println("value Type: " + value.getClass());
            String type = pd.getPropertyType().getTypeName();
            if ("int".equals(type)) {
                try{
                    int v = Integer.parseInt(value);
                    pd.getWriteMethod().invoke(obj, v);
                }catch(Exception e){
                    r.addError(fieldName + " should be an integer");
                }
            } else if ("java.math.BigDecimal".equals(type)){
                try{
                    BigDecimal v = new BigDecimal(value);
                    pd.getWriteMethod().invoke(obj, v);
                }catch(Exception e){
                    r.addError(fieldName + " should be a number");
                }
            }
            else {
                pd.getWriteMethod().invoke(obj, value);
            }
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
package app.controller;

import app.exception.RequestContext;
import business.*;
import org.jsoup.helper.StringUtil;
import org.jxls.common.Context;
import org.jxls.util.JxlsHelper;
import org.lightcouch.CouchDbClient;
import org.lightcouch.Response;
import org.lightcouch.View;
import org.lightcouch.ViewResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import util.BusinessUtil;
import util.CouchDBUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
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
    public static final String METHOD_KEY = "method";

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
            String tableClass = "business.".concat(table);
            Class clz = Class.forName(tableClass);
            CouchDbClient dbClient = CouchDBUtil.getDbClient(clz);
            CouchDocument d = null;
            String id = request.get(ID_KEY);
            if(StringUtil.isBlank(id)) {
                d = (CouchDocument) clz.getDeclaredConstructor().newInstance();
                d.addNarrative("Created by Luis on "+LocalDate.now()+";");
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
                    d.addNarrative("Updated by Luis on"+LocalDate.now()+";");
                }
            }

            String method = request.get(METHOD_KEY);
            if(!StringUtil.isBlank(method)) {
                if(method.equals("addProductRecord")) {
                    String productName = request.get("productName");
                    CouchDbClient productDBClient = CouchDBUtil.getDbClient(Product.class);
                    List<Product> list = productDBClient.findDocs(
                            "{\"selector\": {\"name\": {\"$eq\": \""+productName+"\"}}}", Product.class);
                    int qty = Integer.parseInt(request.get("quantity"));
                    boolean isPromo = Boolean.parseBoolean(request.get("isPromo"));
                    ((Order) d).addProductRecord(list.get(0), qty, isPromo);
                }
            } else {
                for (String k : keys) {
                    if (!IGNORE_KEY_LIST.contains(k)) {
                        callSetter(d, k, request.get(k), r);
                    }
                }
            }
            System.out.println("Save to DB: " + d);
            Response response;
            d.beforeSave(r);
            if(r.hasError()){
                return r;
            }
            if(StringUtil.isBlank(id)){
                d.beforeNew(r);
                if(r.hasError()){
                    return r;
                }
                response = dbClient.save(d);
            }else{
                response = dbClient.update(d);
            }
            d.setId(response.getId());
            d.setRevision(response.getRev());
            r.setResult(d);
            d.afterSave(r);
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
            if ("java.lang.String".equals(type)){
                pd.getWriteMethod().invoke(obj, value);
            } else if(!StringUtil.isBlank(value)){
                if ("java.math.BigDecimal".equals(type)){
                    try{
                        BigDecimal v = new BigDecimal(value);
                        pd.getWriteMethod().invoke(obj, v);
                    }catch(Exception e){
                        r.addError(fieldName + " should be a number");
                    }
                } else if ("java.time.LocalDate".equals(type)){
                    try{
                        System.out.println(value);
                        LocalDate v = LocalDate.parse(value);
                        pd.getWriteMethod().invoke(obj, v);
                    }catch(Exception e){
                        e.printStackTrace();
                        r.addError(fieldName + " should be a date");
                    }
                } else if ("int".equals(type)) {
                    try{
                        int v = Integer.parseInt(value);
                        pd.getWriteMethod().invoke(obj, v);
                    }catch(Exception e){
                        r.addError(fieldName + " should be a number");
                    }
                } else if ("long".equals(type)) {
                    try{
                        long v = Long.parseLong(value);
                        pd.getWriteMethod().invoke(obj, v);
                    }catch(Exception e){
                        r.addError(fieldName + " should be a number");
                    }
                } else {
                    System.out.println("Ignoring " + fieldName);
                }
            }
        }
    }

    @GetMapping("/list")
    public List list(HttpServletRequest request) {
        String table = request.getParameter(TABLE_KEY);
        String tableClass = "business.".concat(table);
        List documentList = null;
        Class clz;
        try{
            clz = Class.forName(tableClass);
            CouchDbClient dbClient = CouchDBUtil.getDbClient(clz);
            String viewId = request.getParameter("viewId");
            View v = dbClient.view(viewId)
                    .includeDocs(true)
                    .descending(true);
            String key = request.getParameter("key");
            if(!StringUtil.isBlank(key)){
                //test if key can be parsed to Long (autogenerated numbers)
                try{
                    v = v.key(Long.parseLong(key));
                }catch (Exception e){
                    v = v.key(key);
                }
            }
            if("Order/byDueOrder".equals(viewId) || "Payment/byClearingDate".equals(viewId)){
                v = v.descending(false).endKey(LocalDate.now());
            }

            String rowsString = request.getParameter(ROWS_KEY);
            if(rowsString != null){
                int rows = Integer.parseInt(rowsString);
                String page = request.getParameter(PAGE_KEY);
                documentList = v.queryPage(rows, page, clz)
                        .getResultList();
            }else{
                documentList = v.query(clz);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return documentList;
    }

    @GetMapping("/monthlySales")
    public void computeMonthlySales(int year, int month){
        CouchDbClient dbClient = CouchDBUtil.getDbClient(Order.class);
        int yearMonth = year * 100 + month;
        List<Order> documentList = dbClient.view("Order/byYearMonth")
                .key(yearMonth)
                .limit(10000)
                .includeDocs(true)
                .query(Order.class);
        saveMonthlySales(yearMonth, documentList);
    }

    @GetMapping("/totalSales")
    public void totalSales(){
        CouchDbClient dbClient = CouchDBUtil.getDbClient(Order.class);
        List<Order> documentList = dbClient.view("Order/byYearMonth")
                .limit(10000)
                .includeDocs(true)
                .query(Order.class);
        saveMonthlySales(99999999, documentList);
    }

    private void saveMonthlySales(int yearMonth, List<Order> documentList) {
        CouchDbClient dbClient = CouchDBUtil.getDbClient(MonthlySales.class);
        List<MonthlySales> list = dbClient.view("MonthlySales/byYearMonth")
                .key(yearMonth)
                .includeDocs(true)
                .query(MonthlySales.class);
        MonthlySales sales = null;
        if(list.size() == 0) {
            sales = new MonthlySales();
            sales.setYearMonth(yearMonth);
            sales.computeSales(documentList);
            dbClient.save(sales);
        }else{
            System.out.println(sales);
            sales = list.get(0);
            sales.computeSales(documentList);
            dbClient.update(sales);
        }
    }

    @GetMapping("/allMonthlySales")
    public void generateAllMonthlySales(){
        CouchDbClient dbClient = CouchDBUtil.getDbClient(Order.class);
        List<ViewResult<Integer, Integer, String>.Rows> documentList = dbClient.view("Order/byYearMonthReduce")
                .group(true).queryView(Integer.class, Integer.class, String.class).getRows();
        for(ViewResult.Rows row: documentList){
            Integer yearMonth = (Integer)row.getKey();
            System.out.println(yearMonth);
            computeMonthlySales(yearMonth/100, yearMonth%100);
        }
    }

    @GetMapping("/productFields")
    public List productFields(){
        CouchDbClient dbClient = CouchDBUtil.getDbClient(Product.class);
        List<KeyLabel> keyLabelList = new ArrayList<KeyLabel>();
        KeyLabel yearMonth = new KeyLabel();
        yearMonth.setKey("yearMonth");
        yearMonth.setLabel("Year Month");
        keyLabelList.add(yearMonth);
        for(Product p : dbClient.view("_all_docs").includeDocs(true).query(Product.class)){
            KeyLabel label = new KeyLabel();
            label.setKey("productSales."+p.getId()+".quantity");
            label.setLabel(p.getName());
            keyLabelList.add(label);
        }
        return keyLabelList;
    }

    @GetMapping("/computeOrderStats")
    public void computeOrderStats(String customerId) {
        Customer.computeOrderStats(customerId);
    }

    @GetMapping("/generateXLS")
    public ResponseEntity<Resource> generateXLS(HttpServletRequest request) throws Exception{
        String orderId = request.getParameter("id");
        Order order = (Order) CouchDocument.findById(orderId, Order.class);
        Customer customer = (Customer) CouchDocument.findById(order.getCustomerId(), Customer.class);
        Trucking trucking = null;
        if(customer.getTruckingId() != null){
            trucking = (Trucking) CouchDocument.findById(customer.getTruckingId(), Trucking.class);
        }
        Agent agent = null;
        if(customer.getAgentId() != null) {
            agent = (Agent) CouchDocument.findById(customer.getAgentId(), Agent.class);
        }
        System.out.println(order);
        System.out.println(customer);
        String fileName = order.getReceiptNo() + ".xlsx";
        File output = new File(fileName);
        try(InputStream is = Order.class.getClassLoader().getResourceAsStream("jxl_template.xlsx")) {
            try (OutputStream os = new FileOutputStream(output)) {
                Context context = new Context();
                context.putVar("order",  order);
                context.putVar("customer",  customer);
                context.putVar("trucking",  trucking);
                context.putVar("agent",  agent);
                context.putVar("generateTime",  LocalDate.now());
                JxlsHelper.getInstance().processTemplate(is, os, context);
            }
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+fileName);
        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(Paths.get(fileName)));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(output.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}
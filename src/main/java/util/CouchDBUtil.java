package util;

import org.lightcouch.CouchDbClient;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class CouchDBUtil {
    static Map<String, CouchDbClient> dbClientMap;
    static {
        dbClientMap = new HashMap<String, CouchDbClient>();
        List<String> dbList = List.of("fingerprint", "person", "verification");
        for(String db: dbList) {
            dbClientMap.put(db, new CouchDbClient(db+".properties"));
        }
    }
    public static CouchDbClient getDbClient(String key){
        return dbClientMap.get(key);
    }
}

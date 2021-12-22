package util;

import app.exception.FPConfigProperties;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.jsoup.helper.StringUtil;
import org.lightcouch.CouchDbClient;
import org.lightcouch.CouchDbProperties;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class CouchDBUtil {
    static Map<Class, CouchDbClient> dbClientMap = new HashMap<Class, CouchDbClient>();

    private static FPConfigProperties config = new FPConfigProperties();
    public static void setMyConfig(FPConfigProperties config) {
        System.out.println("setMyConfig: "+config);
        CouchDBUtil.config = config;
    }

    private static GsonBuilder getGsonBuilder() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>(){
            @Override
            public LocalDate read(final JsonReader jsonReader) throws IOException {
                return LocalDate.parse(jsonReader.nextString(), DateTimeFormatter.ISO_DATE);
            }

            @Override
            public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
                jsonWriter.value(DateTimeFormatter.ISO_DATE.format(localDate));
            }


        }.nullSafe());

        builder.registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>(){
            @Override
            public LocalDateTime read(final JsonReader jsonReader) throws IOException {
                return LocalDateTime.parse(jsonReader.nextString(), DateTimeFormatter.ISO_DATE_TIME);
            }

            @Override
            public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
                jsonWriter.value(DateTimeFormatter.ISO_DATE_TIME.format(localDateTime));
            }
        }.nullSafe());
        return builder;
    }

    public static CouchDbClient getDbClient(Class key) {
        return getDbClient(key, "");
    }

    public static CouchDbClient getDbClient(Class key, String appName){
        CouchDbClient client = dbClientMap.get(key);
        if(client == null){
            String name = key.getSimpleName().toLowerCase();
            System.out.println("getDBClient ("+name+"): "+config);
            if(!StringUtil.isBlank(appName)){
                appName += "_";
            }
            String tablename = appName+name+"_"+config.getType();
            CouchDbProperties properties = new CouchDbProperties(tablename,
                    true, "http", config.getHost(), config.getPort(),
                    config.getCouchDbUsername(), config.getCouchDbPassword());
            properties.setConnectionTimeout(config.getTimeout());
            properties.setMaxConnections(config.getMaxConn());
            client = new CouchDbClient(properties);
            GsonBuilder builder = getGsonBuilder();
            client.setGsonBuilder(builder);
            dbClientMap.put(key, client);
        }
        return client;
    }
}

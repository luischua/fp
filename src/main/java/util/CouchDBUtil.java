package util;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.lightcouch.CouchDbClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class CouchDBUtil {
    static Map<Class, CouchDbClient> dbClientMap = new HashMap<Class, CouchDbClient>();

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

    public static CouchDbClient getDbClient(Class key){
        CouchDbClient client = dbClientMap.get(key);
        if(client == null){
            String dbPropertiesFile = key.getSimpleName().toLowerCase() +".properties";
            System.out.println("DBProperties:"+dbPropertiesFile);
            GsonBuilder builder = getGsonBuilder();
            client = new CouchDbClient(dbPropertiesFile);
            client.setGsonBuilder(builder);
            dbClientMap.put(key, client);
        }
        return client;
    }
}

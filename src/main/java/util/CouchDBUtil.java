package util;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import org.lightcouch.CouchDbClient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class CouchDBUtil {
    static Map<String, CouchDbClient> dbClientMap;
    static {
        dbClientMap = new HashMap<String, CouchDbClient>();
        List<String> dbList = List.of("fingerprint", "person", "verification", "request");
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(LocalDate.class, new TypeAdapter<LocalDate>(){
            @Override
            public void write(final JsonWriter jsonWriter, final LocalDate localDate) throws IOException {
                if (localDate == null) {
                    jsonWriter.nullValue();
                } else {
                    jsonWriter.value(localDate.toString());
                }
            }

            @Override
            public LocalDate read(final JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                } else {
                    return LocalDate.parse(jsonReader.nextString());
                }
            }
        });
        builder.registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>(){
            @Override
            public void write(final JsonWriter jsonWriter, final LocalDateTime localDate) throws IOException {
                if (localDate == null) {
                    jsonWriter.nullValue();
                } else {
                    jsonWriter.value(localDate.toString());
                }
            }

            @Override
            public LocalDateTime read(final JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                } else {
                    return LocalDateTime.parse(jsonReader.nextString());
                }
            }
        });
        for(String db: dbList) {
            CouchDbClient client = new CouchDbClient(db+".properties");
            client.setGsonBuilder(builder);
            dbClientMap.put(db, client);
        }
    }
    public static CouchDbClient getDbClient(String key){
        return dbClientMap.get(key);
    }
}

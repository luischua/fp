package app.exception;

import lombok.Data;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties()
@Data
@ToString
public class FPConfigProperties {
    //config initialize to test values
    @Value("${COUCHDB_USER}")
    private String couchDbUsername = "admin";
    @Value("${COUCHDB_PASSWORD}")
    private String couchDbPassword = "admin";
    @Value("${HTTP_TIMEOUT}")
    private int timeout = 3000;
    @Value("${MAX_CONNECTION}")
    private int maxConn = 20;
    @Value("${TYPE}")
    private String type = "test";
    @Value("${HOST}")
    private String host = "127.0.0.1";
}

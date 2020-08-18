package app.exception;

import app.exception.FPConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import util.CouchDBUtil;

import javax.annotation.PostConstruct;
@Component
public class StaticContextInitializer {
    @Autowired
    private FPConfigProperties config;
    @PostConstruct
    public void init() {
        CouchDBUtil.setMyConfig(config);
    }
}
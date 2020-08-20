package app.exception;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import util.CouchDBUtil;

import java.time.LocalDateTime;

@RequestScope
@Component
public class RequestContext {

    private String exception;
    private LocalDateTime start;
    private LocalDateTime end;
    private String classMethod;
    private String requestString;

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public String getRequestString() {
        return requestString;
    }

    public void setRequestString(String requestString) {
        this.requestString = requestString;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void start(){
        start = LocalDateTime.now();
    }

    public void end(){
        end = LocalDateTime.now();
    }

    public String getClassMethod() {
        return classMethod;
    }

    public void setClassMethod(String classMethod) {
        this.classMethod = classMethod;
    }

    public void save(){
        CouchDBUtil.getDbClient(RequestContext.class).save(this);
    }
}
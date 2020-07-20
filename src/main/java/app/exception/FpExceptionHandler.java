package app.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class FpExceptionHandler{

    @Autowired
    private RequestContext requestContext;

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception ex) {
        System.out.println("FpExceptionHandler: "+requestContext.getClassMethod());
        ex.printStackTrace();
        StringWriter sw = new StringWriter();
        ex.printStackTrace(new PrintWriter(sw));
        String exceptionString = sw.toString();
        requestContext.setException(exceptionString);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionString);
    }

    @ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
    public String handleHttpMediaTypeNotAcceptableException() {
        return "acceptable MIME type:" + MediaType.APPLICATION_JSON_VALUE;
    }
}

package business;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class SaveResult {
    private List<String> error = new ArrayList<>();
    private String stackTrace;
    private CouchDocument result;
    private boolean revisionError;
    public void addError(String s){
        error.add(s.replace("\r\n", "<br/><br><br>"));
    }
}
